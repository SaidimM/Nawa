package com.saidim.nawa.player

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import com.saidim.nawa.view.utils.Versioning

private const val WAKELOCK_MILLI: Long = 25000

private const val DOUBLE_CLICK = 400

class PlayerService: Service() {

    private val binder = LocalBinder()

    // Check if it is already running
    var isRunning = false

    // WakeLock
    private lateinit var mWakeLock: PowerManager.WakeLock

    var headsetClicks = 0
    private var mLastTimeClick = 0L

    lateinit var notificationManager: MusicNotificationManager

    private val mMediaPlayerHolder get() = MediaPlayerHolder.getInstance()

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            mMediaPlayerHolder.resumeOrPause()
        }

        override fun onPause() {
            mMediaPlayerHolder.resumeOrPause()
        }

        override fun onSkipToNext() {
            mMediaPlayerHolder.skip(isNext = true)
        }

        override fun onSkipToPrevious() {
            mMediaPlayerHolder.skip(isNext = false)
        }

        override fun onStop() {
            mMediaPlayerHolder.stopPlaybackService(stopPlayback = true, fromUser = true, fromFocus = false)
        }

        override fun onSeekTo(pos: Long) {
            mMediaPlayerHolder.seekTo(
                pos.toInt(),
                updatePlaybackStatus = true,
                restoreProgressCallBack = false
            )
        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?) = handleMediaIntent(mediaButtonEvent)
    }

    private var mMediaSessionCompat: MediaSessionCompat? = null

    private fun initializeNotificationManager() {
        if (!::notificationManager.isInitialized) {
            notificationManager = MusicNotificationManager(this)
        }
    }

    fun configureMediaSession() {

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val mediaButtonReceiverComponentName = ComponentName(applicationContext, MediaBtnReceiver::class.java)

        var flags = 0
        if (Versioning.isMarshmallow()) flags = PendingIntent.FLAG_IMMUTABLE or 0
        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(applicationContext,
            0, mediaButtonIntent, flags)

        mMediaSessionCompat = MediaSessionCompat(this, packageName, mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent).apply {
            isActive = true
            setCallback(mMediaSessionCallback)
            setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
        }
    }

    fun getMediaSession(): MediaSessionCompat? = mMediaSessionCompat

    override fun onBind(intent: Intent): Binder {
        synchronized(initializeNotificationManager()) {
            mMediaPlayerHolder.setMusicService(this@PlayerService)
        }
        return binder
    }

    @Suppress("DEPRECATION")
    fun handleMediaIntent(intent: Intent?): Boolean {

        try {
            intent?.let {

                val event = if (Versioning.isTiramisu()) {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                } ?: return false

                val eventTime =
                    if (event.eventTime != 0L) event.eventTime else System.currentTimeMillis()

                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK -> {
                            // respond to double click
                            if (eventTime - mLastTimeClick <= DOUBLE_CLICK) headsetClicks = 2
                            if (headsetClicks == 2) {
                                mMediaPlayerHolder.skip(isNext = true)
                            } else {
                                mMediaPlayerHolder.resumeOrPause()
                            }
                            mLastTimeClick = eventTime
                            return true
                        }
                        KeyEvent.KEYCODE_MEDIA_CLOSE, KeyEvent.KEYCODE_MEDIA_STOP -> {
                            mMediaPlayerHolder.stopPlaybackService(stopPlayback = true, fromUser = true, fromFocus = false)
                            return true
                        }
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            mMediaPlayerHolder.skip(isNext = false)
                            return true
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            mMediaPlayerHolder.skip(isNext = true)
                            return true
                        }
                        KeyEvent.KEYCODE_MEDIA_REWIND -> {
                            mMediaPlayerHolder.repeatSong(0)
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }

    fun acquireWakeLock() {
        if (::mWakeLock.isInitialized && !mWakeLock.isHeld) mWakeLock.acquire(WAKELOCK_MILLI)
    }

    fun releaseWakeLock() {
        if (::mWakeLock.isInitialized && mWakeLock.isHeld) mWakeLock.release()
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@PlayerService
    }

    private inner class MediaBtnReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (handleMediaIntent(intent) && isOrderedBroadcast) {
                abortBroadcast()
            }
        }
    }
}