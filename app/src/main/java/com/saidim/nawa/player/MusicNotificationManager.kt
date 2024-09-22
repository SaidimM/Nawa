package com.saidim.nawa.player


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.saidim.nawa.Constants
import com.saidim.nawa.Constants.NEXT_ACTION
import com.saidim.nawa.Constants.NOTIFICATION_CHANNEL_ID
import com.saidim.nawa.Constants.NOTIFICATION_ID
import com.saidim.nawa.Constants.NOTIFICATION_INTENT_REQUEST_CODE
import com.saidim.nawa.Constants.PLAY_PAUSE_ACTION
import com.saidim.nawa.Constants.PREV_ACTION
import com.saidim.nawa.R
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.models.NotificationAction
import com.saidim.nawa.view.MusicActivity
import com.saidim.nawa.view.utils.Theming
import com.saidim.nawa.view.utils.Versioning


class MusicNotificationManager(private val playerService: PlayerService) {

    private val mMediaPlayerHolder get() = MediaPlayerHolder.getInstance()

    //notification manager/builder
    private lateinit var mNotificationBuilder: NotificationCompat.Builder
    private val mNotificationManagerCompat get() = NotificationManagerCompat.from(playerService)

    private val mNotificationActions
        @SuppressLint("RestrictedApi")
        get() = mNotificationBuilder.mActions

    private fun getPendingIntent(playerAction: String): PendingIntent {
        val intent = Intent().apply {
            action = playerAction
            component = ComponentName(playerService, PlayerService::class.java)
        }
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Versioning.isMarshmallow()) {
            flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(playerService, NOTIFICATION_INTENT_REQUEST_CODE, intent, flags)
    }

    private val notificationActions: NotificationAction get() = ServiceLocator.getPreference().notificationActions

    fun createNotification(onCreated: (Notification) -> Unit) {

        mNotificationBuilder =
            NotificationCompat.Builder(playerService, NOTIFICATION_CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_CHANNEL_ID)
        }

        val openPlayerIntent = Intent(playerService, MusicActivity::class.java)
        openPlayerIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        var flags = 0
        if (Versioning.isMarshmallow()) flags = PendingIntent.FLAG_IMMUTABLE or 0
        val contentIntent = PendingIntent.getActivity(
            playerService, NOTIFICATION_INTENT_REQUEST_CODE,
            openPlayerIntent, flags
        )

        mNotificationBuilder
            .setContentIntent(contentIntent)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSilent(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setLargeIcon(null)
            .setOngoing(mMediaPlayerHolder.isPlaying)
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(getNotificationAction(notificationActions.first))
            .addAction(getNotificationAction(PREV_ACTION))
            .addAction(getNotificationAction(PLAY_PAUSE_ACTION))
            .addAction(getNotificationAction(NEXT_ACTION))
            .addAction(getNotificationAction(notificationActions.second))
            .setStyle(MediaStyle()
                .setMediaSession(playerService.getMediaSession().sessionToken)
                .setShowActionsInCompactView(1, 2, 3)
            )

        updateNotificationContent {
            onCreated(mNotificationBuilder.build())
        }
    }

    fun updateNotification() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationBuilder.setOngoing(mMediaPlayerHolder.isPlaying)
            updatePlayPauseAction()
            with(mNotificationManagerCompat) {
                notify(NOTIFICATION_ID, mNotificationBuilder.build())
            }
        }
    }

    fun cancelNotification() {
        with(mNotificationManagerCompat) {
            cancel(NOTIFICATION_ID)
        }
    }

    fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean) {
        if (::mNotificationBuilder.isInitialized) {
            if (!isAdditionalActionsChanged) {
                updateNotificationContent {
                    updateNotification()
                }
                return
            }
            mNotificationActions[0] =
                getNotificationAction(notificationActions.first)
            mNotificationActions[4] =
                getNotificationAction(notificationActions.second)
            updateNotification()
        }
    }

    fun updateNotificationContent(onDone: (() -> Unit)? = null) {
        mMediaPlayerHolder.getMediaMetadataCompat().run {
            mNotificationBuilder
                .setContentText(getText(MediaMetadataCompat.METADATA_KEY_ARTIST))
                .setContentTitle(getText(MediaMetadataCompat.METADATA_KEY_TITLE))
                .setSubText(getText(MediaMetadataCompat.METADATA_KEY_ALBUM))
                .setLargeIcon(getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
        }
        onDone?.invoke()
    }

    fun updatePlayPauseAction() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationActions[2] =
                getNotificationAction(Constants.PLAY_PAUSE_ACTION)
        }
    }

    fun updateRepeatIcon() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationActions[0] =
                getNotificationAction(Constants.REPEAT_ACTION)
            updateNotification()
        }
    }

    fun updateFavoriteIcon() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationActions[0] =
                getNotificationAction(Constants.FAVORITE_ACTION)
            updateNotification()
        }
    }

    private fun getNotificationAction(action: String): NotificationCompat.Action {
        val icon = Theming.getNotificationActionIcon(action, isNotification = true)
        return NotificationCompat.Action.Builder(icon, action, getPendingIntent(action)).build()
    }

    @TargetApi(Build.VERSION_CODES.S)
    fun createNotificationForError() {

        val notificationBuilder =
            NotificationCompat.Builder(playerService, Constants.NOTIFICATION_CHANNEL_ERROR_ID)

        createNotificationChannel(Constants.NOTIFICATION_CHANNEL_ERROR_ID)

        notificationBuilder.setSmallIcon(R.drawable.ic_repeat)
            .setSilent(true)
            .setContentTitle(playerService.getString(R.string.error_fs_not_allowed_sum))
            .setContentText(playerService.getString(R.string.error_fs_not_allowed))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(playerService.getString(R.string.error_fs_not_allowed)))
            .priority = NotificationCompat.PRIORITY_DEFAULT
        with(NotificationManagerCompat.from(playerService)) {
            notify(Constants.NOTIFICATION_ERROR_ID, notificationBuilder.build())
        }
    }

    @TargetApi(26)
    private fun createNotificationChannel(id: String) {
        val name = playerService.getString(R.string.app_name)
        val channel = NotificationChannelCompat.Builder(id, NotificationManager.IMPORTANCE_LOW)
            .setName(name)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .setShowBadge(false)
            .build()

        // Register the channel with the system
        mNotificationManagerCompat.createNotificationChannel(channel)
    }
}
