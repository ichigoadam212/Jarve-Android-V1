package com.ichigoadam.jarvis.voice

import android.content.Context

class VoiceProfileManager(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("jarvis_voice_profile", Context.MODE_PRIVATE)

    fun getOwnerProfile(): VoiceProfile {
        return VoiceProfile(
            ownerId = "owner",
            enrolled = prefs.getBoolean("owner_enrolled", false)
        )
    }

    fun enrollOwner(): VoiceProfile {
        prefs.edit().putBoolean("owner_enrolled", true).apply()
        return getOwnerProfile()
    }

    fun isOwnerEnrolled(): Boolean = getOwnerProfile().enrolled

    fun resetNonOwnerProfiles() {
        // Reserved for future profiles. Owner enrollment is never removed here.
    }
}
