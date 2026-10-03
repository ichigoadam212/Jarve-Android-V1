package com.ichigoadam.jarvis.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ichigoadam.jarvis.integration.JarvisAccessibilityService
import com.ichigoadam.jarvis.memory.CloseChatManager
import com.ichigoadam.jarvis.memory.StyleProfile
import com.ichigoadam.jarvis.memory.ReplyGenerator
import com.ichigoadam.jarvis.memory.ChatStyleProfileStore
import com.ichigoadam.jarvis.voice.OwnerVoiceGate
import com.ichigoadam.jarvis.voice.VoiceInput
import com.ichigoadam.jarvis.voice.VoiceOutput
import com.ichigoadam.jarvis.voice.VoiceProfileManager
import com.ichigoadam.jarvis.voice.SpeakerVerifier
import com.ichigoadam.jarvis.voice.WakeWordDetector

class JarvisController(private val context: Context) {
    private val engine = CommandEngine()
    private val intelligence = JarvisIntelligence(context.applicationContext)
    private val router = CommandRouter()
    private val wakeWord = WakeWordDetector()
    private val output = VoiceOutput(context)
    private val profiles = VoiceProfileManager(context)
    private val speakerVerifier = SpeakerVerifier(context)
    private val ownerGate = OwnerVoiceGate()
    private val closeChats = CloseChatManager(context)
    private val styleProfile = StyleProfile(context)
    private val replyGenerator = ReplyGenerator()
    private val chatStyles = ChatStyleProfileStore(context)
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
                if (!ownerGate.isEnrolled()) {
                    if (!secretMode) output.speak("سجل بصمة صوت المالك أولاً من إعدادات جارفيس")
                    return
                }

                // Stop the speech recognizer before the local speaker sample is captured.
                input.stopListening()
                val verified = speakerVerifier.verifyOwner()
                if (!verified) {
                    if (!secretMode) output.speak("لم يتم التحقق من صوت المالك")
                    scheduleNextListen()
                    return
                }
            }
            CommandPolicy.PUBLIC -> Unit
        }

        val lower = command.lowercase()

        if (lower == "status" || lower == "الحالة") {
            if (!secretMode) {
                val running = JarvisHealthReader.read(context)
                output.speak(
                    if (running.running) {
                        if (running.appControlConnected)
                            "جارفيس متصل وخدمة التحكم في التطبيقات مفعلة"
                        else
                            "جارفيس متصل لكن خدمة التحكم في التطبيقات غير مفعلة"
                    } else {
                        "جارفيس غير متصل"
                    }
                )
            }
            return
        }

        if (lower == "add chat to close" || lower == "ضيف الشات للكلوز" ||
            lower == "ضيف الشات للـكلوز") {
            val pkg = JarvisAccessibilityService.currentPackage()
            val chat = JarvisAccessibilityService.currentChatKey()
            if (pkg.isNotBlank() && chat.isNotBlank()) {
                closeChats.add(pkg, chat)
                if (!secretMode) output.speak("تمت إضافة الشات للكلوز")
            } else if (!secretMode) {
                output.speak("افتح الشات أولاً ثم أعطني الأمر")
            }
            return
        }

        if (lower == "remove chat from close" || lower == "شيل الشات من الكلوز") {
            val pkg = JarvisAccessibilityService.currentPackage()
            val chat = JarvisAccessibilityService.currentChatKey()
            if (pkg.isNotBlank() && chat.isNotBlank()) {
                closeChats.remove(pkg, chat)
                if (!secretMode) output.speak("تمت إزالة الشات من الكلوز")
            }
            return
        }

        if (lower == "teach jarvis my style" || lower == "علم جارفيس أسلوبي" ||
            lower == "learn this chat" || lower == "اتعلم من الشات") {
            val pkg = JarvisAccessibilityService.currentPackage()
            val chat = JarvisAccessibilityService.currentChatKey()
            if (pkg.isBlank() || chat.isBlank()) {
                if (!secretMode) output.speak("افتح الشات أولاً")
                return
            }

            val history = JarvisAccessibilityService.readCurrentChatHistory()
            if (history.isNotBlank()) {
                styleProfile.addSample(history)
                chatStyles.addSample(pkg, chat, history)
                if (!secretMode) output.speak("تم تحليل سجل الشات وحفظه محلياً")
            } else if (!secretMode) {
                output.speak("لم أستطع قراءة سجل الشات")
            }
            return
        }

        if (lower == "auto close replies on" || lower == "شغل الرد التلقائي للكلوز") {
            closeChats.setAutoReply(true)
            if (!secretMode) output.speak("تم تشغيل الرد التلقائي للكلوز")
            return
        }

        if (lower == "auto close replies off" || lower == "اقفل الرد التلقائي للكلوز") {
            closeChats.setAutoReply(false)
            if (!secretMode) output.speak("تم إيقاف الرد التلقائي للكلوز")
            return
        }

        if (lower == "reply in my style" || lower == "رد بأسلوبي") {
            val pkg = JarvisAccessibilityService.currentPackage()
            val chat = JarvisAccessibilityService.currentChatKey()
            if (!closeChats.isClose(pkg, chat)) {
                if (!secretMode) output.speak("الشات ده مش مضاف للكلوز")
                return
            }

            val conversation = JarvisAccessibilityService.readRecentConversation()
            val samples = chatStyles.getSamples(pkg, chat).ifEmpty { styleProfile.getSamples() }
            val generated = replyGenerator.generate(conversation, samples)
            sendReply(generated)
            return
        }

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

        val intelligenceResult = intelligence.process(command)
        if (intelligenceResult.handled) {
            if (!secretMode && intelligenceResult.response.isNotBlank()) {
                output.speak(intelligenceResult.response)
            }
            return
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
        val pkg = JarvisAccessibilityService.currentPackage()
        val chat = JarvisAccessibilityService.currentChatKey()
        val finalMessage = if (
            pkg.isNotBlank() && chat.isNotBlank() &&
            closeChats.isClose(pkg, chat)
        ) {
            message.trimEnd() + "\n\nتم الرد بواسطة جارفيس"
        } else {
            message
        }
        val ok = JarvisAccessibilityService.replyToFocusedInput(finalMessage)
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

    fun enrollOwnerVoice(): Boolean {
        val ok = speakerVerifier.enrollOwner()
        if (ok) {
            profiles.enrollOwner()
            ownerGate.setEnrolled(true)
        }
        return ok
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
