package com.saidim.nawa.player

import android.app.ForegroundServiceStartNotAllowedException
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.saidim.nawa.Constants
import com.saidim.nawa.R
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.base.utils.toFilenameWithoutExtension
import com.saidim.nawa.base.utils.waitForCover
import com.saidim.nawa.view.utils.Versioning

private const val WAKELOCK_MILLI: Long = 25000

private const val DOUBLE_CLICK = 400

class PlayerService : Service() {

    private val binder = LocalBinder()

    // Check if it is already running
    var isRunning = false

    // WakeLock
    private lateinit var mWakeLock: PowerManager.WakeLock

    lateinit var mediaMetadataCompat: MediaMetadataCompat

    var headsetClicks = 0
    private var mLastTimeClick = 0L

    var isNotificationShowing = false

    lateinit var notificationManager: PlayerNotificationManager

    private val playerController get() = PlayerController.getInstance()

    private val musicController by lazy { PlayerController.getInstance() }

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            playerController.playOrPause()
        }

        override fun onPause() {
            playerController.playOrPause()
        }

        override fun onSkipToNext() {
            playerController.playNext()
        }

        override fun onSkipToPrevious() {
            playerController.playPrevious()
        }

        override fun onStop() {
            playerController.stopPlaybackService(stopPlayback = true, fromUser = true, fromFocus = false)
        }

        override fun onSeekTo(pos: Long) {
            playerController.seekTo(
                pos.toInt()
            )
        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?) = handleMediaIntent(mediaButtonEvent)
    }

    private lateinit var mMediaSessionCompat: MediaSessionCompat

    private fun initializeNotificationManager() {
        if (!::notificationManager.isInitialized) {
            notificationManager = PlayerNotificationManager(this)
        }
    }

    fun configureMediaSession() {

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val mediaButtonReceiverComponentName = ComponentName(applicationContext, MediaBtnReceiver::class.java)

        var flags = 0
        if (Versioning.isMarshmallow()) flags = PendingIntent.FLAG_IMMUTABLE or 0
        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0, mediaButtonIntent, flags
        )

        mMediaSessionCompat = MediaSessionCompat(
            this,
            packageName,
            mediaButtonReceiverComponentName,
            mediaButtonReceiverPendingIntent
        ).apply {
            isActive = true
            setCallback(mMediaSessionCallback)
            setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
        }
    }

    fun updateMetadata() {
        with(Builder()) {
            (musicController.getCurrentMusic())?.run {
                putLong(METADATA_KEY_DURATION, duration)
                putString(METADATA_KEY_ARTIST, artist)
                putString(METADATA_KEY_AUTHOR, artist)
                putString(METADATA_KEY_COMPOSER, artist)
                var songTitle = name
                if (ServiceLocator.getPreference().songsVisualization == Constants.FN) {
                    songTitle = displayName.toFilenameWithoutExtension()
                }
                putString(METADATA_KEY_TITLE, songTitle)
                putString(METADATA_KEY_DISPLAY_TITLE, songTitle)
                putString(METADATA_KEY_ALBUM_ARTIST, album)
                putString(METADATA_KEY_DISPLAY_SUBTITLE, album)
                putString(METADATA_KEY_ALBUM, album)
                putBitmap(
                    METADATA_KEY_DISPLAY_ICON,
                    ContextCompat.getDrawable(this@PlayerService, R.drawable.ic_music_note)?.toBitmap()
                )
                putLong(METADATA_KEY_TRACK_NUMBER, track.toLong())
                albumId.waitForCover(this@PlayerService) { bmp, _ ->
                    putBitmap(METADATA_KEY_ALBUM_ART, bmp)
                    putBitmap(METADATA_KEY_ART, bmp)
                    mediaMetadataCompat = build()
                    getMediaSession().setMetadata(mediaMetadataCompat)
                    if (musicController.isPlaying()) {
                        startForeground()
                        notificationManager.run {
                            updatePlayPauseAction()
                            updateNotificationContent {
                                updateNotification()
                            }
                        }
                    }
                }
            }
        }
    }

    fun startForeground() {
        if (!isNotificationShowing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isNotificationShowing = try {
                    notificationManager.createNotification { notification ->
                        startForeground(Constants.NOTIFICATION_ID, notification)
                    }
                    true
                } catch (fsNotAllowed: ForegroundServiceStartNotAllowedException) {
                    synchronized(musicController.playOrPause()) {
                        notificationManager.createNotificationForError()
                    }
                    fsNotAllowed.printStackTrace()
                    false
                }
            } else {
                notificationManager.createNotification { notification ->
                    startForeground(Constants.NOTIFICATION_ID, notification)
                    isNotificationShowing = true
                }
            }
        }
    }

    fun getMediaSession(): MediaSessionCompat = mMediaSessionCompat

    override fun onBind(intent: Intent): Binder {
        synchronized(initializeNotificationManager()) {
            playerController.initPlayerService(this@PlayerService)
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
                                playerController.playNext()
                            } else {
                                playerController.playOrPause()
                            }
                            mLastTimeClick = eventTime
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_CLOSE, KeyEvent.KEYCODE_MEDIA_STOP -> {
                            playerController.stopPlaybackService(
                                stopPlayback = true,
                                fromUser = true,
                                fromFocus = false
                            )
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            playerController.playPrevious()
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            playerController.playNext()
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_REWIND -> {
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

    private inner class MediaBtnReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (handleMediaIntent(intent) && isOrderedBroadcast) {
                abortBroadcast()
            }
        }
    }
}