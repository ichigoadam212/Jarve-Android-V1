package com.ichigoadam.jarvis.guardian

import android.content.Context
import com.ichigoadam.jarvis.JarvisService

class Guardian(private val context: Context) {
    private var failures = 0
    private val maxRestarts = 3

    @Synchronized
    fun shouldRestart(): Boolean {
        if (failures >= maxRestarts) return false
        failures++
        JarvisService.start(context)
        return true
    }

    @Synchronized
    fun resetFailures() {
        failures = 0
    }

    @Synchronized
    fun failureCount(): Int = failures
}
