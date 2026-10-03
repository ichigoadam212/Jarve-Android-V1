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
import com.ichigoadam.jarvis.guardian.GuardianService

class MainActivity : Activity() {
    private var controller: JarvisController? = null
    private lateinit var status: TextView
    private lateinit var coreState: TextView

    private val background = Color.rgb(8, 12, 18)
    private val panel = Color.rgb(15, 21, 30)
    private val textPrimary = Color.rgb(235, 242, 250)
    private val textSecondary = Color.rgb(145, 160, 176)
    private val accent = Color.rgb(70, 180, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildInterface()
    }

    private fun buildInterface() {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28.dp(), 34.dp(), 28.dp(), 34.dp())
            setBackgroundColor(background)
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
            text = "ANDROID V1  •  CORE CONTROL"
            textSize = 11f
            setTextColor(textSecondary)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
        }
        content.addView(subtitle, lp(-1, -2))

        content.addView(Space(this), lp(1, 26))

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
            text = "●  CORE READY"
            textSize = 13f
            setTextColor(accent)
            gravity = Gravity.CENTER
        }
        content.addView(coreState, lp(-1, -2))

        status = TextView(this).apply {
            text = "JARVIS Android V1\nStatus: Ready"
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
            coreState.text = "●  CORE ONLINE"
            status.text = "JARVIS Android V1\nStatus: Running + Guardian"
        }
        content.addView(start, lp(-1, 54))

        val listen = actionButton("VOICE LISTEN")
        listen.setOnClickListener {
            if (!hasAudioPermission()) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
            } else {
                controller?.listen()
                coreState.text = "●  LISTENING"
                status.text = "JARVIS Android V1\nListening..."
            }
        }
        content.addView(listen, lp(-1, 54, top = 10))

        val enrollVoice = actionButton("ENROLL OWNER VOICE")
        enrollVoice.setOnClickListener {
            if (!hasAudioPermission()) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
            } else {
                status.text = "JARVIS Android V1\nSpeak naturally for about 3 seconds..."
                Thread {
                    val ok = controller?.enrollOwnerVoice() == true
                    runOnUiThread {
                        if (ok) {
                            coreState.text = "●  OWNER VOICE READY"
                            status.text = "JARVIS Android V1\nOwner voice profile stored locally"
                        } else {
                            status.text = "JARVIS Android V1\nVoice enrollment failed"
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
            coreState.text = "●  CORE STANDBY"
            status.text = "JARVIS Android V1\nStatus: Stopped"
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
            text = label
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
