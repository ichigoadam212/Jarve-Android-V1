package com.ichigoadam.jarvis.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceOutput(context: Context) {
    private val tts = TextToSpeech(context.applicationContext, null)

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
    }

    fun shutdown() {
        tts.shutdown()
    }
}
