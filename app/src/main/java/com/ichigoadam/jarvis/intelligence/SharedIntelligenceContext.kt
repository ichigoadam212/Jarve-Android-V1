package com.ichigoadam.jarvis.intelligence

data class SharedIntelligenceContext(
    val command: String,
    val jarvisResult: String = "",
    val fridayResult: String = "",
    val useFusion: Boolean = true
)
