package com.ichigoadam.jarvis.memory

data class MessageEnvelope(
    val source: String,
    val sender: String?,
    val text: String,
    val timestamp: Long,
    val importance: String
)
