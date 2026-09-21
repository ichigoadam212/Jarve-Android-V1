package com.ichigoadam.jarvis.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ichigoadam.jarvis.voice.VoiceInput
import com.ichigoadam.jarvis.voice.VoiceOutput
import com.ichigoadam.jarvis.voice.VoiceProfileManager
import com.ichigoadam.jarvis.voice.WakeWordDetector

class JarvisController(context: Context) {
    private val engine = CommandEngine()
    private val wakeWord = WakeWordDetector()
    private val output = VoiceOutput(context)
    private val profiles = VoiceProfileManager(context)
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile private var active = false
    @Volatile private var listening = false
    @Volatile private var secretMode = false

    private val input = VoiceInput(
        context = context,
        onResult = { text -> handleVoice(text) },
        onSessionFinished = { scheduleNextListen() }
    )

    private fun handleVoice(text: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        if (wakeWord.isWakeWord(normalized)) {
            active = true
            if (!secretMode) output.speak("نعم")
            return
        }

        if (!active) return

        val command = wakeWord.removeWakeWord(normalized)
        if (command.isBlank()) return

        val lower = command.lowercase()
        if (lower == "secret" || lower == "الوضع السري" || lower == "silent" || lower == "صامت") {
            secretMode = !secretMode
            if (!secretMode) output.speak("تم إيقاف الوضع السري")
            return
        }

        if (lower == "stop" || lower == "إيقاف") {
            active = false
            if (!secretMode) output.speak("تم الإيقاف")
            return
        }

        val result = engine.execute(command)
        if (result.handled && result.response.isNotBlank() && !secretMode) {
            output.speak(result.response)
        }
    }

    fun listen() {
        if (listening) return
        listening = true
        input.startListening()
    }

    private fun scheduleNextListen() {
        if (!listening) return
        mainHandler.postDelayed({
            if (listening) {
                try {
                    input.startListening()
                } catch (_: Exception) {
                    scheduleNextListen()
                }
            }
        }, 350)
    }

    fun setActive(value: Boolean) {
        active = value
    }

    fun isActive(): Boolean = active

    fun stopListening() {
        listening = false
        input.stopListening()
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun destroy() {
        stopListening()
        input.destroy()
        output.shutdown()
    }
}
