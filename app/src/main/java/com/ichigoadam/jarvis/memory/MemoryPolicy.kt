package com.ichigoadam.jarvis.memory

object MemoryPolicy {
    const val MAX_GLOBAL_STYLE_SAMPLES = 30
    const val MAX_CHAT_STYLE_SAMPLES = 12
    const val MAX_SAMPLE_LENGTH = 12000
    const val MAX_HISTORY_PAGES = 25

    fun bounded(text: String): String =
        text.trim().take(MAX_SAMPLE_LENGTH)
}
