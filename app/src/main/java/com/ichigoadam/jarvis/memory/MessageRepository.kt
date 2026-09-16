package com.ichigoadam.jarvis.memory

interface MessageRepository {
    fun save(message: MessageEnvelope)
    fun deleteOlderThan(timestamp: Long)
}
