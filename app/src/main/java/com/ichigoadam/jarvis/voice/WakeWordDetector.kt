package com.ichigoadam.jarvis.voice

class WakeWordDetector(
    private val wakeWord: String = "jarvis"
) {
    fun isWakeWord(text: String): Boolean {
        return text.trim().lowercase()
            .split(Regex("\\s+"))
            .any { it == wakeWord }
    }
}
