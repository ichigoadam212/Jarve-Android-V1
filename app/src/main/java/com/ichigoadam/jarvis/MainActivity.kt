package com.ichigoadam.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import com.ichigoadam.jarvis.core.JarvisController
import com.ichigoadam.jarvis.voice.AssistantLanguage
import com.ichigoadam.jarvis.guardian.GuardianService

class MainActivity : Activity() {
    private var controller: JarvisController? = null
    private lateinit var status: TextView
    private lateinit var coreState: TextView
    private var showingSelector = true

    private val background = Color.rgb(8, 12, 18)
    private val panel = Color.rgb(15, 21, 30)
    private val textPrimary = Color.rgb(235, 242, 250)
    private val textSecondary = Color.rgb(145, 160, 176)
    private val accent = Color.rgb(70, 180, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildSelector()
    }

    private fun buildSelector() {
        showingSelector = true
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28.dp(), 32.dp(), 28.dp(), 32.dp())
            setBackgroundColor(this@MainActivity.background)
        }

        val title = TextView(this).apply {
            text = "J A R V I S  /  F R I D A Y"
            textSize = 23f
            letterSpacing = 0.08f
            gravity = Gravity.CENTER
            setTextColor(textPrimary)
        }
        root.addView(title, lp(-1, -2))

        val subtitle = TextView(this).apply {
            text = AssistantLanguage.ui(this, "SELECT YOUR ASSISTANT", "اختار المساعد")
            textSize = 12f
            letterSpacing = 0.16f
            gravity = Gravity.CENTER
            setTextColor(textSecondary)
        }
        root.addView(subtitle, lp(-1, -2, top = 10))
        root.addView(Space(this), lp(1, 32))

        val jarvis = actionButton("JARVIS  •  PRIMARY ASSISTANT")
        jarvis.setOnClickListener { buildInterface() }
        root.addView(jarvis, lp(-1, 62))

        val friday = actionButton("F.R.I.D.A.Y.  •  INDEPENDENT ASSISTANT")
        friday.setOnClickListener {
            startActivity(Intent(this, com.ichigoadam.jarvis.friday.FridayActivity::class.java))
        }
        root.addView(friday, lp(-1, 62, top = 14))

        val note = TextView(this).apply {
            text = AssistantLanguage.ui(this, "Separate interfaces • Independent state • Guardian failover", "واجهتان منفصلتان • حالة مستقلة • حماية واستعادة")
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(textSecondary)
            setPadding(8.dp(), 22.dp(), 8.dp(), 0)
        }
        root.addView(note, lp(-1, -2))
        val language = actionButton(if (AssistantLanguage.isArabic(this)) "English" else "العربية")
        language.setOnClickListener {
            AssistantLanguage.toggle(this)
            if (showingSelector) buildSelector() else buildInterface()
        }
        root.addView(language, lp(-1, 44, top = 18))
        setContentView(root)
    }

    private fun buildInterface() {
        showingSelector = false
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28.dp(), 34.dp(), 28.dp(), 34.dp())
            setBackgroundColor(this@MainActivity.background)
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(content)
        }

        val header = TextView(this).apply {
            text = "J A R V I S"
            textSize = 25f
            setTextColor(textPrimary)
            gravity = Gravity.CENTER
            letterSpacing = 0.28f
        }
        content.addView(header, lp(-1, -2))

        val subtitle = TextView(this).apply {
            text = AssistantLanguage.ui(this, "ANDROID V1  •  CORE CONTROL", "أندرويد V1 • التحكم الأساسي")
            textSize = 11f
            setTextColor(textSecondary)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
        }
        content.addView(subtitle, lp(-1, -2))

        val language = actionButton(if (AssistantLanguage.isArabic(this)) "English" else "العربية")
        language.setOnClickListener {
            AssistantLanguage.toggle(this)
            buildInterface()
        }
        content.addView(language, lp(-1, 42, top = 10))
        content.addView(Space(this), lp(1, 18))

        // Reserved visual core: the final JARVIS image can be placed here later.
        val core = TextView(this).apply {
            text = "JARVIS\nCORE"
            textSize = 22f
            setTextColor(textPrimary)
            gravity = Gravity.CENTER
            setBackground(circleDrawable(accent))
            elevation = 10f
        }
        content.addView(core, lp(190, 190))

        content.addView(Space(this), lp(1, 22))

        coreState = TextView(this).apply {
            text = AssistantLanguage.ui(this, "●  CORE READY", "● النواة جاهزة")
            textSize = 13f
            setTextColor(accent)
            gravity = Gravity.CENTER
        }
        content.addView(coreState, lp(-1, -2))

        status = TextView(this).apply {
            text = AssistantLanguage.ui(this, "JARVIS Android V1\nStatus: Ready", "جارفيس أندرويد V1\nالحالة: جاهز")
            textSize = 14f
            setTextColor(textSecondary)
            gravity = Gravity.CENTER
            setPadding(12.dp(), 16.dp(), 12.dp(), 16.dp())
            setBackground(rounded(panel, 18f))
        }
        content.addView(status, lp(-1, -2, top = 14))

        content.addView(Space(this), lp(1, 18))

        val start = actionButton("START JARVIS + GUARDIAN")
        start.setOnClickListener {
            if (!hasAudioPermission()) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                return@setOnClickListener
            }
            JarvisService.start(this)
            startForegroundService(Intent(this, GuardianService::class.java))
            coreState.text = AssistantLanguage.ui(this, "●  CORE ONLINE", "● النواة تعمل")
            status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nStatus: Running + Guardian", "جارفيس أندرويد V1\nالحالة: يعمل مع الحارس")
        }
        content.addView(start, lp(-1, 54))

        val listen = actionButton("VOICE LISTEN")
        listen.setOnClickListener {
            if (!hasAudioPermission()) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
            } else if (controller?.listen() == true) {
                coreState.text = AssistantLanguage.ui(this, "●  LISTENING", "● يستمع الآن")
                status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nListening...", "جارفيس أندرويد V1\nأستمع الآن...")
            } else {
                coreState.text = "●  CORE READY"
                status.text = "JARVIS Android V1\nSpeech recognition unavailable. Check Google speech services and microphone permission."
            }
        }
        content.addView(listen, lp(-1, 54, top = 10))

        val enrollVoice = actionButton("ENROLL OWNER VOICE")
        enrollVoice.setOnClickListener {
            if (!hasAudioPermission()) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
            } else {
                status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nSpeak naturally for about 3 seconds...", "جارفيس أندرويد V1\nاتكلم بشكل طبيعي لمدة 3 ثوانٍ تقريباً...")
                Thread {
                    val ok = controller?.enrollOwnerVoice() == true
                    runOnUiThread {
                        if (ok) {
                            coreState.text = AssistantLanguage.ui(this, "●  OWNER VOICE READY", "● تم تسجيل صوت المالك")
                            status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nOwner voice profile stored locally", "جارفيس أندرويد V1\nتم حفظ بصمة صوت المالك على الجهاز")
                        } else {
                            status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nVoice enrollment failed", "جارفيس أندرويد V1\nتعذر تسجيل الصوت")
                        }
                    }
                }.start()
            }
        }
        content.addView(enrollVoice, lp(-1, 54, top = 10))

        val accessibility = actionButton("ENABLE APP CONTROL")
        accessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        content.addView(accessibility, lp(-1, 54, top = 10))

        val stop = actionButton("STOP JARVIS")
        stop.setOnClickListener {
            JarvisService.stop(this)
            coreState.text = AssistantLanguage.ui(this, "●  CORE STANDBY", "● النواة في وضع الاستعداد")
            status.text = AssistantLanguage.ui(this, "JARVIS Android V1\nStatus: Stopped", "جارفيس أندرويد V1\nالحالة: متوقف")
        }
        content.addView(stop, lp(-1, 54, top = 10))

        content.addView(Space(this), lp(1, 22))

        val info = TextView(this).apply {
            text = "GUARDIAN  •  FRIDAY FALLBACK  •  ACCESSIBILITY\n" +
                "Voice profile and Close-chat memory are managed locally."
            textSize = 11f
            setTextColor(textSecondary)
            gravity = Gravity.CENTER
            setPadding(12.dp(), 10.dp(), 12.dp(), 10.dp())
        }
        content.addView(info, lp(-1, -2))

        setContentView(scroll)
    }

    private fun actionButton(label: String): Button =
        Button(this).apply {
            text = AssistantLanguage.ui(this@MainActivity, label)
            textSize = 12f
            setTextColor(textPrimary)
            gravity = Gravity.CENTER
            isAllCaps = false
            setBackground(rounded(panel, 16f))
            stateListAnimator = null
        }

    private fun rounded(color: Int, radiusDp: Float): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp * resources.displayMetrics.density
            setStroke(
                (1 * resources.displayMetrics.density).toInt().coerceAtLeast(1),
                Color.rgb(32, 45, 60)
            )
        }

    private fun circleDrawable(strokeColor: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(11, 19, 29))
            setStroke(
                (3 * resources.displayMetrics.density).toInt().coerceAtLeast(2),
                strokeColor
            )
        }

    private fun lp(width: Int, height: Int, top: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            if (width == -1) LinearLayout.LayoutParams.MATCH_PARENT else width.dp(),
            if (height == -1) LinearLayout.LayoutParams.WRAP_CONTENT else height.dp()
        ).apply {
            topMargin = top.dp()
        }

    private fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()

    private fun hasAudioPermission(): Boolean =
        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    override fun onStart() {
        super.onStart()
        controller = JarvisController(this)
    }

    override fun onDestroy() {
        controller?.destroy()
        controller = null
        super.onDestroy()
    }
}
