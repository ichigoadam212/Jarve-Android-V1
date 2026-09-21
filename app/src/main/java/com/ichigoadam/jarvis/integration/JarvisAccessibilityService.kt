package com.ichigoadam.jarvis.integration

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        private var instance: JarvisAccessibilityService? = null

        private val supportedPackages = setOf(
            "com.whatsapp",
            "com.instagram.android",
            "com.facebook.katana"
        )

        fun isConnected(): Boolean = instance != null

        fun currentPackage(): String {
            return instance?.rootInActiveWindow?.packageName?.toString().orEmpty()
        }

        fun currentChatKey(): String {
            val service = instance ?: return ""
            val root = service.rootInActiveWindow ?: return ""
            if (!supportedPackages.contains(root.packageName?.toString())) return ""

            val lines = mutableListOf<String>()
            service.collectText(root, lines)
            val ignored = setOf(
                "WhatsApp", "Instagram", "Facebook",
                "إرسال", "ارسال", "send", "send message"
            )
            return lines.map { it.trim() }
                .firstOrNull { it.length in 2..60 && it !in ignored }
                .orEmpty()
        }

        fun currentAppName(): String {
            return when (currentPackage()) {
                "com.whatsapp" -> "WhatsApp"
                "com.instagram.android" -> "Instagram"
                "com.facebook.katana" -> "Facebook"
                else -> "تطبيق غير مدعوم"
            }
        }

        fun readVisibleText(): String {
            val service = instance ?: return ""
            val root = service.rootInActiveWindow ?: return ""
            if (!supportedPackages.contains(root.packageName?.toString())) return ""

            val lines = mutableListOf<String>()
            service.collectText(root, lines)

            return lines
                .distinct()
                .filter { it.length <= 500 }
                .takeLast(80)
                .joinToString("\n")
                .take(5000)
        }

        fun readRecentConversation(): String {
            val service = instance ?: return ""
            val root = service.rootInActiveWindow ?: return ""
            if (!supportedPackages.contains(root.packageName?.toString())) return ""

            val lines = mutableListOf<String>()
            service.collectText(root, lines)

            val cleaned = lines
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .filter { it.length in 2..500 }
                .distinct()

            return cleaned.takeLast(20).joinToString("\n").take(3000)
        }

        fun replyToFocusedInput(message: String): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            val packageName = root.packageName?.toString() ?: return false
            if (!supportedPackages.contains(packageName)) return false
            if (message.isBlank()) return false

            val input = service.findEditable(root) ?: return false
            val args = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    message
                )
            }

            if (!input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)) {
                return false
            }

            return service.clickSendButton(root)
        }

        fun openSupportedApp(app: String): Boolean {
            val service = instance ?: return false
            val packageName = when (app.lowercase()) {
                "whatsapp", "واتساب" -> "com.whatsapp"
                "instagram", "انستجرام", "إنستجرام" -> "com.instagram.android"
                "facebook", "فيسبوك" -> "com.facebook.katana"
                else -> return false
            }

            return try {
                val intent = service.packageManager.getLaunchIntentForPackage(packageName)
                    ?: return false
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                service.startActivity(intent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Content is accessed only after an explicit Jarvis command.
    }

    private fun findEditable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null || !node.isVisibleToUser) return null
        if (node.isEditable) return node

        for (i in 0 until node.childCount) {
            findEditable(node.getChild(i))?.let { return it }
        }
        return null
    }

    private fun clickSendButton(root: AccessibilityNodeInfo): Boolean {
        val labels = listOf(
            "send", "إرسال", "ارسال", "send message",
            "إرسال رسالة", "مشاركة"
        )

        for (label in labels) {
            val nodes = root.findAccessibilityNodeInfosByText(label)
            for (node in nodes) {
                if (node.isVisibleToUser && node.isClickable &&
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                ) {
                    return true
                }

                var parent = node.parent
                repeat(3) {
                    if (parent != null && parent.isVisibleToUser && parent.isClickable &&
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    ) {
                        return true
                    }
                    parent = parent?.parent
                }
            }
        }

        return false
    }

    private fun collectText(
        node: AccessibilityNodeInfo?,
        output: MutableList<String>
    ) {
        if (node == null || !node.isVisibleToUser) return

        node.text?.toString()
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.let { output.add(it) }

        node.contentDescription?.toString()
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.let { output.add(it) }

        for (i in 0 until node.childCount) {
            collectText(node.getChild(i), output)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }
}
