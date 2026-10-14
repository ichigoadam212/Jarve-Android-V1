package com.ichigoadam.jarvis.voice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class VoiceInput(
    context: Context,
    private val onResult: (String) -> Unit,
    private val onRecognitionError: (Int) -> Unit = {},
    private val onSessionFinished: () -> Unit = {}
) {
    private val appContext = context.applicationContext

    private val recognizer: SpeechRecognizer? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)
        ) {
            SpeechRecognizer.createOnDeviceSpeechRecognizer(appContext)
        } else if (SpeechRecognizer.isRecognitionAvailable(appContext)) {
            SpeechRecognizer.createSpeechRecognizer(appContext)
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }

    init {
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                try {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.takeIf { it.isNotBlank() }
                        ?.let { recognizedText ->
                            try {
                                onResult(recognizedText)
                            } catch (_: Exception) {
                                // A command handler must never crash Android's speech callback.
                            }
                        }
                } finally {
                    safelyFinishSession()
                }
            }

            override fun onError(error: Int) {
                try {
                    onRecognitionError(error)
                } catch (_: Exception) {
                    // Keep recognition alive even if an error observer fails.
                } finally {
                    safelyFinishSession()
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun safelyFinishSession() {
        try {
            onSessionFinished()
        } catch (_: Exception) {
            // Speech callbacks are invoked by Android; never let app code crash that thread.
        }
    }

    fun startListening(language: String = "ar-EG"): Boolean {
        val activeRecognizer = recognizer ?: return false
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
        return try {
            activeRecognizer.startListening(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isOnDevice(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)

    fun isRecognitionAvailable(): Boolean =
        try {
            SpeechRecognizer.isRecognitionAvailable(appContext)
        } catch (_: Exception) {
            false
        }

    fun stopListening() {
        try { recognizer?.stopListening() } catch (_: Exception) { }
    }

    fun destroy() {
        try { recognizer?.destroy() } catch (_: Exception) { }
    }
}
