package com.ichigoadam.jarvis.memory

import android.content.Context
import org.json.JSONObject
import java.io.File

class LocalMessageRepository(private val context: Context) : MessageRepository {
    private val file: File
        get() = File(context.filesDir, "jarvis_messages.jsonl")

    @Synchronized
    override fun save(message: MessageEnvelope) {
        val json = JSONObject().apply {
            put("source", message.source)
            put("sender", message.sender ?: "")
            put("text", message.text)
            put("timestamp", message.timestamp)
            put("importance", message.importance)
        }
        file.appendText(json.toString() + "\n")
    }

    @Synchronized
    override fun deleteOlderThan(timestamp: Long) {
        if (!file.exists()) return
        val kept = file.readLines().filter { line ->
            runCatching {
                JSONObject(line).optLong("timestamp", 0L) >= timestamp
            }.getOrDefault(false)
        }
        file.writeText(if (kept.isEmpty()) "" else kept.joinToString("\n") + "\n")
    }
}
