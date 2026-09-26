package com.ichigoadam.jarvis.integration

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ichigoadam.jarvis.JarvisService

class JarvisTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (JarvisService.isRunning(this)) {
            JarvisService.stop(this)
        } else {
            JarvisService.start(this)
        }
        updateTile()
    }

    private fun updateTile() {
        qsTile?.apply {
            state = if (JarvisService.isRunning(this@JarvisTileService)) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            label = "JARVIS"
            contentDescription = "JARVIS"
            updateTile()
        }
    }
}
