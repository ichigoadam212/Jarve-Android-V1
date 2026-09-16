package com.ichigoadam.jarvis

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val status = TextView(this).apply {
            text = "JARVIS Android V1\nStatus: Ready"
            textSize = 20f
            setPadding(32, 48, 32, 32)
        }
        val start = Button(this).apply {
            text = "Start JARVIS"
            setOnClickListener {
                JarvisService.start(this@MainActivity)
                status.text = "JARVIS Android V1\nStatus: Running"
            }
        }
        val stop = Button(this).apply {
            text = "Stop JARVIS"
            setOnClickListener {
                stopService(android.content.Intent(this@MainActivity, JarvisService::class.java))
                status.text = "JARVIS Android V1\nStatus: Stopped"
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            addView(status)
            addView(start)
            addView(stop)
        })
    }
}
