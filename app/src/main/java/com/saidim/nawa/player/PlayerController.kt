package com.saidim.nawa.player

import LogUtil
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.PowerManager
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.saidim.nawa.Constants
import com.saidim.nawa.Constants.VOLUME_DUCK
import com.saidim.nawa.Constants.VOLUME_NORMAL
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.base.utils.toContentUri
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.pref.Preference
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.ln

class PlayerController : IPlayerController {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playerService: PlayerService
    private lateinit var audioManager: AudioManager
    private lateinit var playerReceiver: BroadcastReceiver
    private lateinit var listener: PlayerControllerListener
    private lateinit var audioFocusRequestCompat: AudioFocusRequestCompat

    private val preference = ServiceLocator.getPreference()

    private val updateIndexTask by lazy { Runnable { listener.onIndexChanged(mediaPlayer.currentPosition) } }

    private val isPlayerInitialized get() = ::mediaPlayer.isInitialized
    private val isPlayServiceInitialized get() = ::playerService.isInitialized
    private val isPlayerListenerInitialized get() = ::listener.isInitialized
    private val isAudioFocusRequestCompatInitialized get() = ::audioFocusRequestCompat.isInitialized
    private val isAudioManagerInitialized get() = ::audioManager.isInitialized

    private val mediaSessionActions = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_SEEK_TO

    private var audioFocusState = AudioManager.AUDIOFOCUS_NONE
    private var executor: ScheduledExecutorService? = null

    private var currentMusic: Music? = null
        set(value) {
            if (value != null) preference.latestPlayedSong = value
            field = value
        }
    private var currentList: List<Music> = arrayListOf()

    private var playOnFocusGain = false
    private var restoreVolume = false
    private var isRepeatEnabled = false
    private var isShuffleEnabled = false

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        syncPlayerIndex()
        playerService.updateMetadata()
        if (!isAudioFocusRequestCompatInitialized) initializeAudioFocusRequestCompat()
        if (!hasFocus()) requestAudioFocus()
        resumePlayer()
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener { playPrevious() }

    private val onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        LogUtil.d(TAG, "what: $what, extra: $extra")
        mediaPlayer.release()
        playNext()
        true
    }

    private val mOnAudioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> audioFocusState = AudioManager.AUDIOFOCUS_GAIN
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                    audioFocusState = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
                    playOnFocusGain = false
                    restoreVolume = isPlayerInitialized && isPlaying()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // Lost audio focus, but will gain it back (shortly), so note whether
                    // playback should resume
                    audioFocusState = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    restoreVolume = false
                    playOnFocusGain = isPlayerInitialized && isPlaying()
                }
                // Lost audio focus, probably "permanently"
                AudioManager.AUDIOFOCUS_LOSS -> audioFocusState = AudioManager.AUDIOFOCUS_LOSS
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> audioFocusState = AudioManager.AUDIOFOCUS_REQUEST_FAILED
            }
            // Update the player state based on the change
            if (hasFocus()) {
                if (restoreVolume || !isPlaying() && playOnFocusGain) {
                    configurePlayerState()
                }
            }
        }

    companion object {
        const val TAG = "PlayerController"
        @Volatile
        private var INSTANCE: PlayerController? = null
        fun getInstance(): PlayerController {
            if (INSTANCE == null) {
                synchronized(PlayerController::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = PlayerController()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun initPlayerService(service: PlayerService) {
        mediaPlayer = MediaPlayer()
        currentMusic = preference.latestPlayedSong
        playerService = service
        audioManager = playerService.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        registerActionReceiver()
        playerService.configureMediaSession()
        openOrCloseAudioEffectAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
    }

    override fun playMusic(music: Music?, needReset: Boolean) {
        try {
            if (music == null) return
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.reset()
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
            mediaPlayer.run {
                setOnPreparedListener(onPreparedListener)
                setOnCompletionListener(onCompletionListener)
                setOnErrorListener(onErrorListener)
                setWakeMode(playerService, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(audioAttributes)
            }
            mediaPlayer.setDataSource(playerService, music.id.toContentUri())
            mediaPlayer.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun startPlay(index: Int, musics: List<Music>) {
        LogUtil.d(TAG, "startPlay")
        if (currentList != musics) currentList = if (isShuffleEnabled) musics.shuffled() else musics
        LogUtil.d(TAG, "musics[index] != currentMusic: " + (musics[index] != currentMusic))
        if (musics.size > index && musics[index] != currentMusic) {
            LogUtil.d(TAG, "initMediaPlayer(currentMusic)")
            currentMusic = musics[index]
            playMusic(currentMusic)
        } else playOrPause()
    }

    override fun syncPlayerIndex() {
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleWithFixedDelay(updateIndexTask, 0, Constants.SEEKBAR_UPDATE_INTERVAL, TimeUnit.SECONDS)
    }

    override fun stopSyncPlayerIndex() {
        executor?.shutdown()
        executor = null
    }

    override fun stopPlaybackService(stopPlayback: Boolean, fromUser: Boolean, fromFocus: Boolean) {
        try {
            if (playerService.isRunning && isPlayerInitialized && stopPlayback) {
                if (playerService.isNotificationShowing) {
                    ServiceCompat.stopForeground(playerService, ServiceCompat.STOP_FOREGROUND_REMOVE)
                    playerService.isNotificationShowing = false
                } else {
                    playerService.notificationManager.cancelNotification()
                }
                if (!fromFocus) playerService.stopSelf()
            }
            if (isPlayerListenerInitialized && fromUser) {
                listener.onClose()
            }
        } catch (e: java.lang.IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun requestAudioFocus() {
        if (!isAudioFocusRequestCompatInitialized) initializeAudioFocusRequestCompat()
        if (isAudioManagerInitialized) audioFocusState =
            AudioManagerCompat.requestAudioFocus(audioManager, audioFocusRequestCompat)
    }

    override fun looseAudioFocus() {
        if (isAudioFocusRequestCompatInitialized && isAudioManagerInitialized && hasFocus()) {
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, audioFocusRequestCompat)
            audioFocusState = AudioManager.AUDIOFOCUS_LOSS
        }
    }

    override fun releasePlayer() {
        LogUtil.d(TAG, "releasePlayer")
        if (isPlayerInitialized) {
            openOrCloseAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            mediaPlayer.release()
            looseAudioFocus()
            stopSyncPlayerIndex()
        }
        if (isPlayServiceInitialized) playerService.notificationManager.cancelNotification()
        unregisterActionReceiver()
    }

    override fun playOrPause(forcePause: Boolean) {
        LogUtil.d(TAG, "playOrPause")
        LogUtil.d(TAG, "isPlaying: ${isPlaying()}")
        if (isPlaying() || forcePause) {
            pausePlayer()
        } else {
            resumePlayer()
        }
    }

    override fun playNext() {
        val isLast = currentList.last() == currentMusic
        currentMusic = when {
            isLast && isRepeatEnabled && isShuffleEnabled -> {
                currentList.first()
            }
            isLast && isRepeatEnabled -> currentList.first()
            else -> currentList[currentList.indexOf(currentMusic) + 1]
        }
        playMusic(currentMusic!!)
    }

    override fun playPrevious() {
        currentMusic = currentList[currentList.indexOf(currentMusic) - 1]
        playMusic(currentMusic!!)
    }

    override fun setRepeatEnabled(enabled: Boolean) {
        isRepeatEnabled = enabled
        updatePlayMode()
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        isShuffleEnabled = enabled
        updatePlayMode()
    }

    override fun seekTo(pos: Int) {
        if (isPlayerInitialized) {
            mediaPlayer.setOnSeekCompleteListener {
                it.setOnSeekCompleteListener(null)
                syncPlayerIndex()
                updatePlayStatus()
            }
            mediaPlayer.seekTo(pos)
        }
    }

    override fun setVolume(volume: Int) {
        if (isPlaying()) {
            fun volFromPercent(percent: Int): Float {
                if (percent == 100) return 1f
                return (1 - (ln((101 - percent).toFloat()) / ln(101f)))
            }
            val new = volFromPercent(volume)
            mediaPlayer.setVolume(new, new)
        }
    }

    override fun isPlaying() = if (::mediaPlayer.isInitialized) mediaPlayer.isPlaying else false

    override fun getCurrentMusic() = currentMusic

    override fun getCurrentList() = currentList

    private fun registerActionReceiver() {
        playerReceiver = PlayerActionReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(Intent.ACTION_MEDIA_BUTTON)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
        try {
            ContextCompat.registerReceiver(
                playerService.applicationContext,
                playerReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            LocalBroadcastManager.getInstance(playerService.applicationContext)
                .registerReceiver(playerReceiver, intentFilter)
        }
    }

    private fun unregisterActionReceiver() {
        try {
            if (isPlayServiceInitialized) playerService.applicationContext.unregisterReceiver(playerReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            LocalBroadcastManager.getInstance(playerService).unregisterReceiver(playerReceiver)
        }
    }

    private fun openOrCloseAudioEffectAction(event: String) {
        playerService.sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, playerService.packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        )
    }

    private fun initializeAudioFocusRequestCompat() {
        val audioAttributes = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .build()
        audioFocusRequestCompat =
            AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .build()
    }

    private fun resumePlayer() {
        requestAudioFocus()
        play()
        updatePlayStatus()
        playerService.startForeground()
    }

    private fun pausePlayer() {
        pause()
        playerService.isNotificationShowing = false
        updatePlayStatus()
        playerService.notificationManager.run {
            updatePlayPauseAction()
            updateNotification()
        }
    }

    private fun hasFocus() =
        audioFocusState == AudioManager.AUDIOFOCUS_GAIN || audioFocusState != AudioManager.AUDIOFOCUS_REQUEST_FAILED

    private fun configurePlayerState() {
        when (audioFocusState) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                stopPlaybackService(stopPlayback = true, fromUser = false, fromFocus = true)
                pausePlayer()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                stopPlaybackService(stopPlayback = true, fromUser = false, fromFocus = true)
                pausePlayer()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK)
            else -> {
                if (restoreVolume) {
                    restoreVolume = false
                    mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL)
                }
                // If we were playing when we lost focus, we need to resume playing.
                if (playOnFocusGain) {
                    resumePlayer()
                    playOnFocusGain = false
                }
            }
        }
    }

    private fun updatePlayStatus(updateUI: Boolean = true) {
        val playState = if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder().setActions(mediaSessionActions)
            .setState(playState, mediaPlayer.currentPosition.toLong(), 1.0f)
            .build()
        playerService.getMediaSession().setPlaybackState(playbackState)
        if (updateUI && isPlayerListenerInitialized) listener.onPlayStateChanged()
    }

    private fun updatePlayMode() {
        if (isPlayerListenerInitialized) listener.onPlayModeChanged(isRepeatEnabled, isShuffleEnabled)
    }

    fun pause() {
        with(mediaPlayer) {
            try {
                if (isPlaying) pause()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun play() {
        with(mediaPlayer) {
            try {
                start()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
}