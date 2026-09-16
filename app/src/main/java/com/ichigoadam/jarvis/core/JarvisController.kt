package com.ichigoadam.jarvis.core

import android.content.Context
import com.ichigoadam.jarvis.voice.VoiceInput
import com.ichigoadam.jarvis.voice.VoiceOutput
import com.ichigoadam.jarvis.voice.WakeWordDetector

class JarvisController(context: Context) {
    private val engine = CommandEngine()
    private val wakeWord = WakeWordDetector()
    private val output = VoiceOutput(context)
    private val input = VoiceInput(
        context = context,
        onResult = { text -> handleVoice(text) }
    )

    private fun handleVoice(text: String) {
        if (wakeWord.isWakeWord(text)) {
            output.speak("نعم")
            return
        }

        val result = engine.execute(text)
        if (result.handled && result.response.isNotBlank()) {
            output.speak(result.response)
        }
    }

    fun listen() {
        input.startListening()
    }

    fun destroy() {
        input.destroy()
        output.shutdown()
    }
}
