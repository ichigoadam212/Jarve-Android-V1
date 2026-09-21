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

        fun readVisibleText(): String {
            val service = instance ?: return ""
            if (!supportedPackages.contains(service.rootInActiveWindow?.packageName?.toString())) return ""

            val lines = mutableListOf<String>()
            service.collectText(service.rootInActiveWindow, lines)
            return lines.distinct().joinToString("\n").take(4000)
        }

        fun replyToFocusedInput(message: String): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            val packageName = root.packageName?.toString() ?: return false
            if (!supportedPackages.contains(packageName)) return false

            val input = service.findEditable(root) ?: return false
            val args = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    message
                )
            }

            val set = input.performAction(
                AccessibilityNodeInfo.ACTION_SET_TEXT,
                args
            )
            if (!set) return false

            return service.clickSendButton(root)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // V1 reads content only when Jarvis receives an explicit read/reply command.
        // No screen or message content is transmitted outside the device.
    }

    fun openApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
                ?: return false
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun findText(text: String): AccessibilityNodeInfo? {
        val root = rootInActiveWindow ?: return null
        return root.findAccessibilityNodeInfosByText(text).firstOrNull()
    }

    private fun findEditable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable && node.isVisibleToUser) return node

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
                if (node.isVisibleToUser && node.isClickable) {
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                var parent = node.parent
                repeat(2) {
                    if (parent != null && parent.isVisibleToUser && parent.isClickable) {
                        if (parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
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
