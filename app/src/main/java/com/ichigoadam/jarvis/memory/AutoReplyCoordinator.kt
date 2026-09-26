package com.ichigoadam.jarvis.memory

import android.content.Context

class AutoReplyCoordinator(context: Context) {
    private val appContext = context.applicationContext
    private val closeChats = CloseChatManager(appContext)
    private val styleProfile = StyleProfile(appContext)
    private val chatStyles = ChatStyleProfileStore(appContext)
    private val generator = ReplyGenerator()

    @Volatile
    private var lastConversationHash: Int = 0

    @Volatile
    private var lastReplyHash: Int = 0

    @Volatile
    private var lastReplyAt: Long = 0L

    fun onConversationChanged(
        packageName: String,
        chatKey: String,
        conversation: String,
        send: (String) -> Boolean
    ) {
        if (!closeChats.isAutoReplyEnabled()) return
        if (!closeChats.isClose(packageName, chatKey)) return

        val clean = conversation.trim()
        if (clean.isBlank()) return

        val conversationHash = clean.hashCode()
        if (conversationHash == lastConversationHash) return
        lastConversationHash = conversationHash

        // Never react to our own tagged reply.
        if (clean.contains("تم الرد بواسطة جارفيس")) return

        val now = System.currentTimeMillis()
        if (now - lastReplyAt < 2500L) return

        val samples = chatStyles.getSamples(packageName, chatKey)
            .ifEmpty { styleProfile.getSamples() }
        val reply = generator.generate(clean, samples).trim()
        if (reply.isBlank() || reply.hashCode() == lastReplyHash) return

        val finalMessage = reply + "\n\nتم الرد بواسطة جارفيس"
        if (send(finalMessage)) {
            lastReplyHash = reply.hashCode()
            lastReplyAt = now
        }
    }
}
