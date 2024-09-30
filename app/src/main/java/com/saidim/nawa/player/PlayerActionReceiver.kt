package com.saidim.nawa.player

import LogUtil
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.saidim.nawa.view.utils.Versioning

class PlayerActionReceiver: BroadcastReceiver() {
    companion object val TAG = "PlayerActionReceiver"
    private val player: IPlayerController by lazy { PlayerController.getInstance() }

    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.d(TAG, "onReceive: ${intent.action}")
        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> player.playOrPause(true)
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> player.playOrPause(true)
            Intent.ACTION_MEDIA_BUTTON -> handleMediaButton(intent).let { if (it) resultCode = Activity.RESULT_OK }

        }
    }

    /**
     * Handles "media button" which is how external applications
     * communicate with our app
     */
    @Suppress("DEPRECATION")
    private fun handleMediaButton(intent: Intent): Boolean {
        val event: KeyEvent = if (Versioning.isTiramisu()) {
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
        } else {
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
        } ?: return false

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_STOP, KeyEvent.KEYCODE_MEDIA_PAUSE -> if (player.isPlaying()) {
                    player.playOrPause(forcePause = true)
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> if (!player.isPlaying()) {
                    player.playOrPause()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    player.playOrPause()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    if (player.isPlaying()) {
                        player.playNext()
                        return true
                    }
                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> if (player.isPlaying()) {
                    player.playPrevious()
                    return true
                }
            }
        }
        return false
    }
}