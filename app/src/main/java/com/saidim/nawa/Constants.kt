package com.saidim.nawa

import android.support.v4.media.session.PlaybackStateCompat
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.base.utils.EaseCubicInterpolator

object Constants {
    val ALBUM_COVER_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/album/"
    val LYRIC_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/lyric/"

    val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)

    const val SEEKBAR_UPDATE_INTERVAL = 1000L

    const val MUSIC_ID = "MUSIC_ID"

    // volume
    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    const val VOLUME_DUCK = 0.2f

    // The volume we set the media player when we have audio focus.
    const val VOLUME_NORMAL = 1.0f

    // We don't have audio focus, can't play
    const val AUDIO_FOCUS_FAILED = -1

    // We don't have audio focus, and can't duck (play at a low volume)
    const val AUDIO_NO_FOCUS_NO_DUCK = 0

    // We don't have focus, but can duck (play at a low volume)
    const val AUDIO_NO_FOCUS_CAN_DUCK = 1

    // We have full audio focus
    const val AUDIO_FOCUSED = 2

    // We don't have focus, but we can resume
    const val AUDIO_FOCUS_LOSS_TRANSIENT = 3

    // The headset connection states (0,1)
    const val HEADSET_DISCONNECTED = 0
    const val HEADSET_CONNECTED = 1

    const val MAIN_FRAGMENT = 0
    const val MUSIC_LIST_FRAGMENT = 1

    const val NOTIFICATION_CHANNEL_ID: String = "NOTIFICATION_CHANNEL_ID"
    const val CLOSE_ACTION: String = "CLOSE_ACTION"
    const val PRE_ACTION: String = "PRE_ACTION"
    const val NEXT_ACTION: String = "NEXT_ACTION"
    const val PLAY_PAUSE_ACTION: String = "PLAY_PAUSE_ACTION"
    const val NOTIFICATION_INTENT_REQUEST_CODE = 100

    const val NOTIFICATION_CHANNEL_ERROR_ID = "CHANNEL_ERROR"
    const val NOTIFICATION_ID = 101
    const val NOTIFICATION_ERROR_ID = 102
    const val FAST_FORWARD_ACTION = "FAST_FORWARD_GO"
    const val PREV_ACTION = "PREV_ACTION"
    const val REWIND_ACTION = "REWIND_ACTION"
    const val REPEAT_ACTION = "REPEAT_ACTION"
    const val FAVORITE_ACTION = "FAVORITE_ACTION"
    const val FAVORITE_POSITION_ACTION = "FAVORITE_POSITION_GO"

    const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2588
    const val LAUNCHED_BY_TILE = "LAUNCHED_BY_TILE"

    // active fragments
    const val ARTISTS_TAB = "ARTISTS_TAB"
    const val ALBUM_TAB = "ALBUM_TAB"
    const val SONGS_TAB = "SONGS_TAB"
    const val FOLDERS_TAB = "FOLDERS_TAB"
    const val SETTINGS_TAB = "SETTINGS_TAB"

    val DEFAULT_ACTIVE_FRAGMENTS =
        listOf(ARTISTS_TAB, ALBUM_TAB, SONGS_TAB, FOLDERS_TAB, SETTINGS_TAB)

    // launched by, used to determine which MusicContainerListFragment is instantiated by the ViewPager
    const val ARTIST_VIEW = "0"
    const val ALBUM_VIEW = "1"
    const val FOLDER_VIEW = "2"

    const val RESTORE_FRAGMENT = "RESTORE_FRAGMENT"

    // playback speed options
    const val PLAYBACK_SPEED_ONE_ONLY = "0"

    // on list ended option
    const val CONTINUE = "0"

    // song visualization options
    const val FN = "1"

    // sorting
    const val DEFAULT_SORTING = 0
    const val ASCENDING_SORTING = 1
    const val DESCENDING_SORTING = 2
    const val TRACK_SORTING = 3
    const val TRACK_SORTING_INVERTED = 4
    const val DATE_ADDED_SORTING = 5
    const val DATE_ADDED_SORTING_INV = 6
    const val ARTIST_SORTING = 7
    const val ARTIST_SORTING_INV = 8
    const val ALBUM_SORTING = 9
    const val ALBUM_SORTING_INV = 10

    // error tags
    const val TAG_NO_PERMISSION = "NO_PERMISSION"
    const val TAG_NO_MUSIC = "NO_MUSIC"
    const val TAG_NO_MUSIC_INTENT = "NO_MUSIC_INTENT"
    const val TAG_SD_NOT_READY = "SD_NOT_READY"

    // fragments tags
    const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT"
    const val ERROR_FRAGMENT_TAG = "ERROR_FRAGMENT"

    // Player playing statuses
    const val PLAYING = PlaybackStateCompat.STATE_PLAYING
    const val PAUSED = PlaybackStateCompat.STATE_PAUSED
    const val RESUMED = PlaybackStateCompat.STATE_NONE

    fun getAlbumCoverPath(music: Music) = "$ALBUM_COVER_DIR${music.id}.jpg"

    fun getLyricPath(music: Music) = "$LYRIC_DIR${music.id}.txt"
}