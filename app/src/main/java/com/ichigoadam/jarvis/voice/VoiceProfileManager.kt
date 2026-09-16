package com.ichigoadam.jarvis.voice

class VoiceProfileManager {
    private var ownerProfile = VoiceProfile()

    fun getOwnerProfile(): VoiceProfile = ownerProfile

    fun enrollOwner(): VoiceProfile {
        ownerProfile = ownerProfile.copy(enrolled = true)
        return ownerProfile
    }

    fun resetNonOwnerProfiles() {
        // Reserved for future additional voice profiles.
        // The owner identity is intentionally never removed by this API.
    }
}
