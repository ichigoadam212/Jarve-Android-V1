package com.ichigoadam.jarvis.friday

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.ichigoadam.jarvis.JarvisService
import com.ichigoadam.jarvis.voice.VoiceInput
import com.ichigoadam.jarvis.voice.VoiceOutput
import com.ichigoadam.jarvis.voice.WakeWordDetector

/**
 * Emergency fallback for a JARVIS process crash.
 * Runs in a separate Android process through the manifest.
 */
class FridayFallbackService : Service() {
    companion object {
        private const val CHANNEL_ID = "jarvis_friday_fallback"
        private const val NOTIFICATION_ID = 1003

        fun start(context: Context) {
            context.startForegroundService(
                Intent(context, FridayFallbackService::class.java)
            )
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FridayFallbackService::class.java))
        }
    }

    private lateinit var input: VoiceInput
    private lateinit var output: VoiceOutput
    private val wakeWord = WakeWordDetector()
    private lateinit var fridayModule: FridayModule
    private var listening = false

    override fun onCreate() {
        super.onCreate()

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "JARVIS F.R.I.D.A.Y. Fallback",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS")
            .setContentText("F.R.I.D.A.Y. fallback mode is active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        output = VoiceOutput(this)
        fridayModule = FridayModule(applicationContext)
        input = VoiceInput(
            context = this,
            onResult = { handleVoice(it) },
            onSessionFinished = { scheduleListen() }
        )

        listen()
    }

    private fun handleVoice(text: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        if (wakeWord.isWakeWord(normalized)) {
            output.speak("نعم، فرايداي جاهزة")
            return
        }

        val command = normalized.lowercase()

        if (
            command == "شغل جارفيس" ||
            command == "شغل جارفس" ||
            command == "شغل jarvis" ||
            command == "start jarvis" ||
            command == "restart jarvis"
        ) {
            output.speak("جاري إعادة التحكم إلى جارفيس")
            JarvisService.start(this)
            stopSelf()
            return
        }

        val result = fridayModule.analyze(normalized)
        if (result.handled && result.response.isNotBlank()) {
            output.speak(result.response)
        } else {
            output.speak("أنا فرايداي. أستطيع فحص حالة النظام وتنفيذ أوامر فرايداي المتاحة. قل: حالة فرايداي، أو حلل.")
        }
    }

    private fun listen() {
        if (listening) return
        listening = true
        try {
            input.startListening()
        } catch (_: Exception) {
            scheduleListen()
        }
    }

    private fun scheduleListen() {
        if (!listening) return
        android.os.Handler(mainLooper).postDelayed({
            if (listening && !JarvisService.isRunning(this)) {
                try {
                    input.startListening()
                } catch (_: Exception) {
                    scheduleListen()
                }
            }
        }, 500)
    }

    override fun onDestroy() {
        listening = false
        if (::input.isInitialized) input.destroy()
        if (::output.isInitialized) output.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
