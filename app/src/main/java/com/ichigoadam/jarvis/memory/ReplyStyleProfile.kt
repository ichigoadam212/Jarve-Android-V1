package com.ichigoadam.jarvis.memory

data class ReplyStyleProfile(
    val averageLength: Int = 35,
    val usesQuestions: Boolean = false,
    val usesExclamation: Boolean = false,
    val usesEllipsis: Boolean = false,
    val usesEgyptianArabic: Boolean = true,
    val commonOpeners: List<String> = emptyList(),
    val commonClosers: List<String> = emptyList()
)
