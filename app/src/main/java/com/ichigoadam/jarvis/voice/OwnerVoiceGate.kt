package com.ichigoadam.jarvis.voice

class OwnerVoiceGate {
    @Volatile
    private var ownerEnrolled = false

    fun setEnrolled(value: Boolean) {
        ownerEnrolled = value
    }

    fun isEnrolled(): Boolean = ownerEnrolled

    /**
     * This gate expects a real speaker-verification result.
     * Text recognition alone must never be treated as proof of identity.
     */
    fun authorize(isVerifiedOwner: Boolean): Boolean {
        return ownerEnrolled && isVerifiedOwner
    }
}
