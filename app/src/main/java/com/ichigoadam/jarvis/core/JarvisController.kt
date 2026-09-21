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

    @Volatile
    private var active = false

    private fun handleVoice(text: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        if (wakeWord.isWakeWord(normalized)) {
            active = true
            output.speak("نعم")
            return
        }

        if (!active) return

        val command = wakeWord.removeWakeWord(normalized)
        if (command.isBlank()) return

        val result = engine.execute(command)
        if (result.handled && result.response.isNotBlank()) {
            output.speak(result.response)
        }
    }

    fun listen() {
        input.startListening()
    }

    fun setActive(value: Boolean) {
        active = value
    }

    fun isActive(): Boolean = active

    fun destroy() {
        input.destroy()
        output.shutdown()
    }
}
