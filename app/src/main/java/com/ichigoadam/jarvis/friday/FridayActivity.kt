package com.ichigoadam.jarvis.friday

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
import android.widget.Space
import android.widget.TextView
import com.ichigoadam.jarvis.JarvisService
import com.ichigoadam.jarvis.MainActivity
import com.ichigoadam.jarvis.guardian.GuardianService

/** A separate FRIDAY screen and control surface. */
class FridayActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var state: TextView
    private val background = Color.rgb(14, 8, 20)
    private val panel = Color.rgb(28, 15, 37)
    private val primary = Color.rgb(250, 235, 255)
    private val secondary = Color.rgb(183, 155, 199)
    private val accent = Color.rgb(224, 92, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildInterface()
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(26.dp(), 34.dp(), 26.dp(), 30.dp())
            setBackgroundColor(background)
        }

        val heading = TextView(this).apply {
            text = "F . R . I . D . A . Y"
            textSize = 25f
            letterSpacing = 0.12f
            gravity = Gravity.CENTER
            setTextColor(primary)
        }
        root.addView(heading, lp(-1, -2))

        val subtitle = TextView(this).apply {
            text = "INDEPENDENT ASSISTANT  •  FALLBACK READY"
            textSize = 10f
            letterSpacing = 0.06f
            gravity = Gravity.CENTER
            setTextColor(secondary)
        }
        root.addView(subtitle, lp(-1, -2, 8))
        root.addView(Space(this), lp(1, 28))

        val core = TextView(this).apply {
            text = "FRIDAY"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(primary)
            background = circle()
            elevation = 8f
        }
        root.addView(core, lp(176, 176))

        state = TextView(this).apply {
            text = "●  STANDBY"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(accent)
        }
        root.addView(state, lp(-1, -2, 22))

        status = TextView(this).apply {
            text = "FRIDAY is ready. Start it to take over the microphone from JARVIS."
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(secondary)
            setPadding(14.dp(), 16.dp(), 14.dp(), 16.dp())
            background = rounded(panel)
        }
        root.addView(status, lp(-1, -2, 12))

        val start = actionButton("ACTIVATE FRIDAY")
        start.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 41)
                return@setOnClickListener
            }
            // Mark JARVIS stop as intentional so Guardian does not restart it over FRIDAY.
            JarvisService.stop(this)
            FridayFallbackService.start(this)
            state.text = "●  FRIDAY ACTIVE"
            status.text = "FRIDAY is active in its own process. Only FRIDAY should hold the microphone."
        }
        root.addView(start, lp(-1, 54, 16))

        val resume = actionButton("HAND CONTROL BACK TO JARVIS")
        resume.setOnClickListener {
            JarvisService.start(this)
            startForegroundService(Intent(this, GuardianService::class.java))
            state.text = "●  JARVIS RESUMING"
            status.text = "JARVIS is being started. FRIDAY will stop as JARVIS takes control."
        }
        root.addView(resume, lp(-1, 54, 10))

        val stop = actionButton("STOP FRIDAY")
        stop.setOnClickListener {
            FridayFallbackService.stop(this)
            state.text = "●  STANDBY"
            status.text = "FRIDAY stopped. Select JARVIS to resume the primary assistant."
        }
        root.addView(stop, lp(-1, 54, 10))

        val settings = actionButton("MICROPHONE PERMISSIONS")
        settings.setOnClickListener { startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.parse("package:$packageName")
        }) }
        root.addView(settings, lp(-1, 54, 10))

        val back = actionButton("BACK TO ASSISTANT SELECTOR")
        back.setOnClickListener { finish() }
        root.addView(back, lp(-1, 48, 18))

        setContentView(root)
    }

    private fun actionButton(label: String) = Button(this).apply {
        text = label
        textSize = 11f
        isAllCaps = false
        gravity = Gravity.CENTER
        setTextColor(primary)
        background = rounded(panel)
        stateListAnimator = null
    }

    private fun rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = 16f * resources.displayMetrics.density
        setStroke((resources.displayMetrics.density).toInt().coerceAtLeast(1), Color.rgb(78, 39, 94))
    }

    private fun circle() = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(panel)
        setStroke((3 * resources.displayMetrics.density).toInt().coerceAtLeast(2), accent)
    }

    private fun lp(width: Int, height: Int, top: Int = 0) =
        LinearLayout.LayoutParams(
            if (width == -1) LinearLayout.LayoutParams.MATCH_PARENT else width.dp(),
            if (height == -1) LinearLayout.LayoutParams.WRAP_CONTENT else height.dp()
        ).apply { topMargin = top.dp() }

    private fun Int.dp() = (this * resources.displayMetrics.density).toInt()
}
