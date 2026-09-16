package com.ichigoadam.jarvis

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ichigoadam.jarvis.core.JarvisController

class MainActivity : Activity() {
    private var controller: JarvisController? = null
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply {
            text = "JARVIS Android V1\nStatus: Ready"
            textSize = 20f
            setPadding(32, 48, 32, 32)
        }

        val listen = Button(this).apply {
            text = "Listen"
            setOnClickListener {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                } else {
                    controller?.listen()
                    status.text = "JARVIS Android V1\nListening..."
                }
            }
        }

        val start = Button(this).apply {
            text = "Start JARVIS"
            setOnClickListener {
                JarvisService.start(this@MainActivity)
                status.text = "JARVIS Android V1\nStatus: Running"
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            addView(status)
            addView(start)
            addView(listen)
        })
    }

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
