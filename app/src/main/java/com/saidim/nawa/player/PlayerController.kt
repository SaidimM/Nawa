package com.saidim.nawa.player

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import com.saidim.nawa.Constants
import com.saidim.nawa.R
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.base.utils.toFilenameWithoutExtension
import com.saidim.nawa.base.utils.waitForCover
import com.saidim.nawa.media.local.bean.Music

class PlayerController {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playerService: PlayerService
    private var audioManager: AudioManager? = null

    private lateinit var notificationManager: MusicNotificationManager
    private var isNotificationShowing = false

    private lateinit var mediaMetadataCompat: MediaMetadataCompat

    private var currentMusic: Music? = null
    private var isPlaying = false
    var state = Constants.PAUSED

    private var currentPlaybackSpeed = ServiceLocator.getPreference().latestPlaybackSpeed

    private val mediaSessionActions = ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_STOP or ACTION_SEEK_TO

    private val onPreparedListener = MediaPlayer.OnPreparedListener {

    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener {

    }

    private val onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->

        true
    }

    fun initService(playerService: PlayerService) {
        this.playerService = playerService
        mediaPlayer = MediaPlayer()
        audioManager = playerService.getSystemService()
        notificationManager = playerService.notificationManager
        configureAudioEffect(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
    }

    fun configureAudioEffect(action: String) {
        val intent = Intent(action)
            .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, playerService.packageName)
            .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.audioSessionId)
            .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        playerService.sendBroadcast(intent)
    }

    fun updateMetadata() {
        with(MediaMetadataCompat.Builder()) {
            (currentMusic)?.run {
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
                    ContextCompat.getDrawable(playerService, R.drawable.ic_music_note)?.toBitmap()
                )
                putLong(METADATA_KEY_TRACK_NUMBER, track.toLong())
                albumId.waitForCover(playerService) { bmp, _ ->
                    putBitmap(METADATA_KEY_ALBUM_ART, bmp)
                    putBitmap(METADATA_KEY_ART, bmp)
                    mediaMetadataCompat = build()
                    playerService.getMediaSession().setMetadata(mediaMetadataCompat)
                    if (isPlaying) {
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

    private fun startForeground() {
        if (!isNotificationShowing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isNotificationShowing = try {
                    notificationManager.createNotification { notification ->
                        playerService.startForeground(Constants.NOTIFICATION_ID, notification)
                    }
                    true
                } catch (fsNotAllowed: ForegroundServiceStartNotAllowedException) {
                    synchronized(pauseMediaPlayer()) {
                        notificationManager.createNotificationForError()
                    }
                    fsNotAllowed.printStackTrace()
                    false
                }
            } else {
                notificationManager.createNotification { notification ->
                    playerService.startForeground(Constants.NOTIFICATION_ID, notification)
                    isNotificationShowing = true
                }
            }
        }
    }

    fun pauseMediaPlayer() {
        // Do not pause foreground service, we will need to resume likely
        MediaPlayerUtils.safePause(mediaPlayer)
        isNotificationShowing = false
        state = Constants.PAUSED
        updatePlaybackStatus(updateUI = true)
        notificationManager.run {
            updatePlayPauseAction()
            updateNotification()
        }
//        if (::mediaPlayerInterface.isInitialized && !isCurrentSongFM) {
//            mediaPlayerInterface.onBackupSong()
//        }
    }

    private fun updatePlaybackStatus(updateUI: Boolean) {
        playerService.getMediaSession().setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(mediaSessionActions)
                .setState(
                    if (isPlaying) Constants.PLAYING else Constants.PAUSED,
                    mediaPlayer.currentPosition.toLong(),
                    currentPlaybackSpeed
                ).build()
        )
//        if (updateUI && ::mediaPlayerInterface.isInitialized) {
//            mediaPlayerInterface.onStateChanged()
//        }
    }
}