package com.ichigoadam.jarvis.voice

class WakeWordDetector(
    private val wakeWord: String = "jarvis"
) {
    fun isWakeWord(text: String): Boolean {
        return text.trim().lowercase()
            .split(Regex("\\s+"))
            .any { it == wakeWord }
    }

    fun removeWakeWord(text: String): String {
        return text.trim()
            .replace(Regex("(?i)\\bjarvis\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
