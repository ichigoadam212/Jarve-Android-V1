package com.ichigoadam.jarvis.voice

/**
 * V1 policy boundary for owner-only commands.
 * Actual speaker verification is deliberately separate from command execution.
 */
class OwnerVoiceGate {
    private var ownerEnrolled = false

    fun enrollOwner() { ownerEnrolled = true }

    fun isEnrolled(): Boolean = ownerEnrolled

    fun authorize(isVerifiedOwner: Boolean): Boolean {
        return ownerEnrolled && isVerifiedOwner
    }
}
