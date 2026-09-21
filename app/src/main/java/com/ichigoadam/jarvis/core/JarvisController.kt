package com.ichigoadam.jarvis.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ichigoadam.jarvis.integration.JarvisAccessibilityService
import com.ichigoadam.jarvis.voice.OwnerVoiceGate
import com.ichigoadam.jarvis.voice.VoiceInput
import com.ichigoadam.jarvis.voice.VoiceOutput
import com.ichigoadam.jarvis.voice.VoiceProfileManager
import com.ichigoadam.jarvis.voice.WakeWordDetector

class JarvisController(context: Context) {
    private val engine = CommandEngine()
    private val router = CommandRouter()
    private val wakeWord = WakeWordDetector()
    private val output = VoiceOutput(context)
    private val profiles = VoiceProfileManager(context)
    private val ownerGate = OwnerVoiceGate()
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile private var active = false
    @Volatile private var listening = false
    @Volatile private var secretMode = false

    private val input = VoiceInput(
        context = context,
        onResult = { text -> handleVoice(text) },
        onSessionFinished = { scheduleNextListen() }
    )

    init {
        ownerGate.setEnrolled(profiles.isOwnerEnrolled())
    }

    private fun handleVoice(text: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        if (wakeWord.isWakeWord(normalized)) {
            active = true
            if (!secretMode) output.speak("نعم")
            return
        }

        if (!active) return

        val command = wakeWord.removeWakeWord(normalized)
        if (command.isBlank()) return

        when (router.policyFor(command)) {
            CommandPolicy.OWNER_ONLY,
            CommandPolicy.SECRET_OWNER_ONLY -> {
                if (!ownerGate.authorize(false)) return
            }
            CommandPolicy.PUBLIC -> Unit
        }

        val lower = command.lowercase()

        when {
            lower == "read messages" || lower == "اقرأ الرسائل" ||
            lower == "read screen" || lower == "اقرأ الشاشة" -> {
                speakAppText(JarvisAccessibilityService.readVisibleText())
                return
            }

            lower == "read last message" || lower == "اقرأ آخر رسالة" -> {
                speakAppText(JarvisAccessibilityService.readRecentConversation())
                return
            }

            lower == "what app" || lower == "ما التطبيق" -> {
                if (!secretMode) {
                    output.speak(
                        if (JarvisAccessibilityService.isConnected()) {
                            JarvisAccessibilityService.currentAppName()
                        } else {
                            "خدمة التحكم في التطبيقات غير مفعلة"
                        }
                    )
                }
                return
            }

            lower.startsWith("reply to last ") || lower.startsWith("رد على آخر رسالة ") -> {
                val message = when {
                    lower.startsWith("reply to last ") ->
                        command.substring("reply to last ".length).trim()
                    else ->
                        command.substring("رد على آخر رسالة ".length).trim()
                }
                sendReply(message)
                return
            }

            lower.startsWith("reply ") || lower.startsWith("رد ") -> {
                val message = command.substringAfter(" ", "").trim()
                sendReply(message)
                return
            }

            lower == "open whatsapp" || lower == "افتح واتساب" -> {
                openApp("واتساب")
                return
            }

            lower == "open instagram" || lower == "افتح انستجرام" ||
            lower == "افتح إنستجرام" -> {
                openApp("انستجرام")
                return
            }

            lower == "open facebook" || lower == "افتح فيسبوك" -> {
                openApp("فيسبوك")
                return
            }

            lower == "secret" || lower == "الوضع السري" ||
            lower == "silent" || lower == "صامت" -> {
                secretMode = !secretMode
                if (!secretMode) output.speak("تم إيقاف الوضع السري")
                return
            }

            lower == "stop" || lower == "إيقاف" -> {
                active = false
                if (!secretMode) output.speak("تم الإيقاف")
                return
            }
        }

        val result = engine.execute(command)
        if (result.handled && result.response.isNotBlank() && !secretMode) {
            output.speak(result.response)
        }
    }

    private fun speakAppText(text: String) {
        if (secretMode) return
        output.speak(
            when {
                !JarvisAccessibilityService.isConnected() ->
                    "خدمة التحكم في التطبيقات غير مفعلة"
                text.isBlank() ->
                    "لا يوجد نص مقروء من التطبيق الحالي"
                else -> text
            }
        )
    }

    private fun sendReply(message: String) {
        val ok = JarvisAccessibilityService.replyToFocusedInput(message)
        if (secretMode) return

        output.speak(
            when {
                message.isBlank() -> "قل نص الرد بعد كلمة رد"
                !JarvisAccessibilityService.isConnected() ->
                    "خدمة التحكم في التطبيقات غير مفعلة"
                ok -> "تم الإرسال"
                else -> "لم أستطع الإرسال من التطبيق الحالي"
            }
        )
    }

    private fun openApp(app: String) {
        val ok = JarvisAccessibilityService.openSupportedApp(app)
        if (!secretMode) {
            output.speak(
                when {
                    ok -> "تم فتح $app"
                    !JarvisAccessibilityService.isConnected() ->
                        "فعّل خدمة التحكم في التطبيقات أولاً"
                    else -> "لم أستطع فتح $app"
                }
            )
        }
    }

    fun listen() {
        if (listening) return
        listening = true
        input.startListening()
    }

    private fun scheduleNextListen() {
        if (!listening) return
        mainHandler.postDelayed({
            if (listening) {
                try {
                    input.startListening()
                } catch (_: Exception) {
                    scheduleNextListen()
                }
            }
        }, 350)
    }

    fun setActive(value: Boolean) { active = value }
    fun isActive(): Boolean = active

    fun stopListening() {
        listening = false
        input.stopListening()
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun destroy() {
        stopListening()
        input.destroy()
        output.shutdown()
    }
}
