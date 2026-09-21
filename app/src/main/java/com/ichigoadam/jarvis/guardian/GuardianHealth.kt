package com.ichigoadam.jarvis.guardian

import android.content.Context
import com.ichigoadam.jarvis.JarvisService
import com.ichigoadam.jarvis.integration.JarvisAccessibilityService

data class GuardianHealth(
    val jarvisRunning: Boolean,
    val accessibilityConnected: Boolean,
    val restartCount: Int
)

class GuardianHealthReader(private val context: Context) {
    fun read(restartCount: Int = 0): GuardianHealth =
        GuardianHealth(
            jarvisRunning = JarvisService.isRunning(context),
            accessibilityConnected = JarvisAccessibilityService.isConnected(),
            restartCount = restartCount
        )
}
