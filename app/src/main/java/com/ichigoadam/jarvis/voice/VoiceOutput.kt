package com.ichigoadam.jarvis.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Language-aware text-to-speech. FRIDAY prefers an installed female voice when one is available. */
class VoiceOutput(context: Context, private val preferFemaleVoice: Boolean = false) {
    private val appContext = context.applicationContext
    private val ttsLock = Any()
    @Volatile private var initialized = false
    @Volatile private var closed = false
    private var pendingText: String? = null

    private val tts = TextToSpeech(appContext) { status ->
        synchronized(ttsLock) {
            if (closed) return@synchronized
            initialized = status == TextToSpeech.SUCCESS
            if (initialized) {
                configureVoice(AssistantLanguage.speechTag(appContext))
                val pending = pendingText
                pendingText = null
                if (pending != null) speakInitialized(pending)
            }
        }
    }

    fun speak(text: String) {
        if (text.isBlank() || closed) return
        val spokenText = AssistantLanguage.localizeSpeech(appContext, text)
        synchronized(ttsLock) {
            if (closed) return
            if (!initialized) {
                pendingText = spokenText
                return
            }
            speakInitialized(spokenText)
        }
    }

    private fun speakInitialized(text: String) {
        val languageTag = if (text.any { it in '\u0600'..'\u06FF' }) "ar-EG" else "en-US"
        configureVoice(languageTag)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "assistant-" + System.nanoTime())
    }

    private fun configureVoice(languageTag: String) {
        val locale = Locale.forLanguageTag(languageTag)
        tts.language = locale
        if (!preferFemaleVoice) return

        // Prefer an explicitly labelled female voice; pitch is a fallback, not a guaranteed gender change.
        tts.setPitch(1.12f)
        val female = tts.voices.orEmpty()
            .filter { it.locale.language == locale.language }
            .firstOrNull {
                val name = it.name.lowercase(Locale.ROOT)
                name.contains("female") || name.contains("woman") || name.contains("feminine")
            }
        if (female != null) tts.voice = female
    }

    fun shutdown() {
        synchronized(ttsLock) {
            closed = true
            initialized = false
            pendingText = null
            tts.shutdown()
        }
    }
}
