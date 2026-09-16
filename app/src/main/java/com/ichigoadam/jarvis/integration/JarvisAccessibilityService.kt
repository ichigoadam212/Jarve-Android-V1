package com.ichigoadam.jarvis.integration

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class JarvisAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // V1 intentionally does not inspect or automate app content.
        // Future cross-app features will be explicit and user-controlled.
    }

    override fun onInterrupt() = Unit
}
