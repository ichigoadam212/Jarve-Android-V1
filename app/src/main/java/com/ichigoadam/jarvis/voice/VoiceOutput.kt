package com.ichigoadam.jarvis.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Text-to-speech follows the language of the spoken text. FRIDAY prefers a female voice when installed. */
class VoiceOutput(context: Context, private val preferFemaleVoice: Boolean = false) {
    private val appContext = context.applicationContext
    private val ttsLock = Any()
    @Volatile private var initialized = false
    private val tts = TextToSpeech(appContext) { status ->
        initialized = status == TextToSpeech.SUCCESS
        if (initialized) {
            synchronized(ttsLock) { configureVoice(if (preferFemaleVoice) "en-US" else AssistantLanguage.speechTag(appContext)) }
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        val spokenText = AssistantLanguage.localizeSpeech(appContext, text)
        val languageTag = if (spokenText.any { it in '\u0600'..'\u06FF' }) "ar-EG" else "en-US"
        synchronized(ttsLock) {
            if (initialized) configureVoice(languageTag)
            tts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null, "assistant-" + System.nanoTime())
        }
    }

    private fun configureVoice(languageTag: String) {
        val locale = Locale.forLanguageTag(languageTag)
        tts.language = locale
        if (!preferFemaleVoice) return
        val languagePrefix = locale.language
        val choices = tts.voices.orEmpty().filter { it.locale.language == languagePrefix }
        val female = choices.firstOrNull {
            val name = it.name.lowercase(Locale.ROOT)
            name.contains("female") || name.contains("woman") || name.contains("feminine")
        }
        if (female != null) tts.voice = female
    }

    fun shutdown() {
        synchronized(ttsLock) { tts.shutdown(); initialized = false }
    }
}
