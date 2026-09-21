package com.ichigoadam.jarvis.core

import android.content.Context
import com.ichigoadam.jarvis.JarvisService
import com.ichigoadam.jarvis.integration.JarvisAccessibilityService

data class JarvisHealth(
    val running: Boolean,
    val appControlConnected: Boolean
)

object JarvisHealthReader {
    fun read(context: Context): JarvisHealth =
        JarvisHealth(
            running = JarvisService.isRunning(context),
            appControlConnected = JarvisAccessibilityService.isConnected()
        )
}
