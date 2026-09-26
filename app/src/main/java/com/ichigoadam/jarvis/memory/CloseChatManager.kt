package com.ichigoadam.jarvis.memory

import android.content.Context

class CloseChatManager(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("jarvis_close_chats", Context.MODE_PRIVATE)

    fun add(packageName: String, chatKey: String) {
        if (packageName.isBlank() || chatKey.isBlank()) return
        prefs.edit().putBoolean(key(packageName, chatKey), true).apply()
    }

    fun remove(packageName: String, chatKey: String) {
        prefs.edit().remove(key(packageName, chatKey)).apply()
    }

    fun setAutoReply(enabled: Boolean) {
        prefs.edit().putBoolean("auto_reply", enabled).apply()
    }

    fun isAutoReplyEnabled(): Boolean =
        prefs.getBoolean("auto_reply", false)

    fun isClose(packageName: String, chatKey: String): Boolean {
        return packageName.isNotBlank() && chatKey.isNotBlank() &&
            prefs.getBoolean(key(packageName, chatKey), false)
    }

    private fun key(packageName: String, chatKey: String): String =
        "chat_" + packageName + "_" + chatKey.hashCode()
}
