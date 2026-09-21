package com.ichigoadam.jarvis.memory

import android.content.Context

class ChatStyleProfileStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("jarvis_chat_styles", Context.MODE_PRIVATE)

    fun addSample(packageName: String, chatKey: String, sample: String) {
        if (packageName.isBlank() || chatKey.isBlank() || sample.isBlank()) return
        val key = key(packageName, chatKey)
        val current = getSamples(packageName, chatKey).toMutableList()
        current.add(sample.trim().take(12000))
        val kept = current.takeLast(12)
        prefs.edit().putStringSet(key, kept.toSet()).apply()
    }

    fun getSamples(packageName: String, chatKey: String): List<String> {
        if (packageName.isBlank() || chatKey.isBlank()) return emptyList()
        return prefs.getStringSet(key(packageName, chatKey), emptySet())
            ?.toList()
            .orEmpty()
    }

    fun clear(packageName: String, chatKey: String) {
        prefs.edit().remove(key(packageName, chatKey)).apply()
    }

    private fun key(packageName: String, chatKey: String): String =
        "style_" + packageName + "_" + chatKey.hashCode()
}
