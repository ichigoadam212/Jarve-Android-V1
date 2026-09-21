package com.ichigoadam.jarvis.memory

import android.content.Context

class StyleProfile(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("jarvis_style_profile", Context.MODE_PRIVATE)

    fun addSample(sample: String) {
        val clean = sample.trim()
        if (clean.isBlank()) return

        val old = getSamples().toMutableList()
        old.add(clean.take(500))
        val kept = old.takeLast(30)

        prefs.edit()
            .putStringSet("samples", kept.toSet())
            .apply()
    }

    fun getSamples(): List<String> {
        return prefs.getStringSet("samples", emptySet())
            ?.toList()
            .orEmpty()
    }
}
