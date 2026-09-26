package com.ichigoadam.jarvis.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.util.Locale
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Lightweight local speaker fingerprint.
 *
 * This is a privacy-friendly, on-device voice similarity layer, not biometric-grade
 * authentication. It stores only a normalized acoustic feature vector.
 */
class SpeakerVerifier(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("jarvis_voice_signature", Context.MODE_PRIVATE)

    private val sampleRate = 16_000
    private val frameSize = 256
    private val featureCount = 16
    private val enrollmentMs = 2500
    private val verificationMs = 1500

    fun hasSignature(): Boolean = prefs.getString("signature", null).isNullOrBlank().not()

    fun enrollOwner(): Boolean {
        if (!hasRecordPermission()) return false
        val features = captureFeatures(enrollmentMs) ?: return false
        prefs.edit().putString("signature", encode(features)).apply()
        return true
    }

    fun verifyOwner(): Boolean {
        if (!hasRecordPermission()) return false
        val reference = decode(prefs.getString("signature", null) ?: return false) ?: return false
        val sample = captureFeatures(verificationMs) ?: return false
        return cosine(reference, sample) >= 0.90f
    }

    private fun hasRecordPermission(): Boolean =
        appContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun captureFeatures(durationMs: Int): FloatArray? {
        val minBuffer = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBuffer <= 0) return null

        val bufferSize = max(minBuffer, frameSize * 8)
        val record = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        } catch (_: Exception) {
            return null
        }

        val targetSamples = sampleRate * durationMs / 1000
        val audio = ShortArray(targetSamples)
        var offset = 0

        return try {
            record.startRecording()
            while (offset < targetSamples) {
                val read = record.read(audio, offset, targetSamples - offset)
                if (read <= 0) break
                offset += read
            }
            if (offset < frameSize * 4) return null
            extractFeatures(audio.copyOf(offset))
        } catch (_: Exception) {
            null
        } finally {
            try { record.stop() } catch (_: Exception) {}
            record.release()
        }
    }

    private fun extractFeatures(audio: ShortArray): FloatArray {
        val sums = FloatArray(featureCount)
        var frames = 0
        var start = 0

        while (start + frameSize <= audio.size) {
            var energy = 0.0
            for (i in 0 until frameSize) {
                val sample = audio[start + i].toDouble() / 32768.0
                energy += sample * sample
            }
            if (energy > 0.000001) {
                for (bin in 1..featureCount) {
                    var real = 0.0
                    var imag = 0.0
                    for (i in 0 until frameSize) {
                        val window = 0.54 - 0.46 * cos(2.0 * Math.PI * i / (frameSize - 1))
                        val value = audio[start + i].toDouble() / 32768.0 * window
                        val angle = 2.0 * Math.PI * bin * i / frameSize
                        real += value * cos(angle)
                        imag -= value * sin(angle)
                    }
                    sums[bin - 1] += ln(1.0 + hypot(real, imag)).toFloat()
                }
                frames++
            }
            start += frameSize
        }

        val safeFrames = max(frames, 1)
        val result = FloatArray(featureCount)
        for (i in result.indices) result[i] = sums[i] / safeFrames

        var norm = 0.0
        for (v in result) norm += v * v
        val divisor = sqrt(max(norm, 0.000001))
        for (i in result.indices) result[i] = (result[i] / divisor).toFloat()
        return result
    }

    private fun cosine(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dot = 0.0
        var na = 0.0
        var nb = 0.0
        for (i in a.indices) {
            dot += a[i] * b[i]
            na += a[i] * a[i]
            nb += b[i] * b[i]
        }
        if (na <= 0.0 || nb <= 0.0) return 0f
        return (dot / (sqrt(na) * sqrt(nb))).toFloat()
    }

    private fun encode(values: FloatArray): String =
        values.joinToString(",") { String.format(Locale.US, "%.7f", it) }

    private fun decode(value: String?): FloatArray? {
        if (value.isNullOrBlank()) return null
        return try {
            value.split(",").map { it.toFloat() }.toFloatArray()
        } catch (_: Exception) {
            null
        }
    }
}
