package com.ichigoadam.jarvis.server

import com.ichigoadam.jarvis.memory.MessageEnvelope

interface ServerBridge {
    suspend fun upload(message: MessageEnvelope): Boolean
}
