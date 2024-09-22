package com.saidim.nawa.media.pref

import android.content.Context
import androidx.core.content.edit
import com.blankj.utilcode.util.Utils
import com.iven.musicplayergo.models.Sorting
import com.saidim.nawa.Constants
import com.saidim.nawa.Constants.CLOSE_ACTION
import com.saidim.nawa.Constants.REPEAT_ACTION
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.models.NotificationAction
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class Preference {
    private val sp = Utils.getApp().getSharedPreferences("nawa", Context.MODE_PRIVATE)
    private val mMoshi = Moshi.Builder().build()

    // active fragments type
    private val typeActiveTabs = Types.newParameterizedType(List::class.java, String::class.java)

    // favorites is a list of Music
    private val typeFavorites = Types.newParameterizedType(List::class.java, Music::class.java)

    // sortings is a list of Sorting
    private val typeSorting = Types.newParameterizedType(List::class.java, Sorting::class.java)

    var isFocusEnabled
        get() = sp.getBoolean("focus_pref", true)
        set(value) = sp.edit { putBoolean("focus_pref", value) }

    var notificationActions: NotificationAction
        get() = getObjectForType("notification_actions_pref", NotificationAction::class.java)
            ?: NotificationAction(REPEAT_ACTION, CLOSE_ACTION)
        set(value) = putObjectForType("notification_actions_pref", value, NotificationAction::class.java)


    // Retrieve object from the Preferences using Moshi
    private fun <T : Any> putObjectForType(key: String, value: T?, type: Type) {
        val json = mMoshi.adapter<T>(type).toJson(value)
        sp.edit { putString(key, json) }
    }

    var latestVolume: Int
        get() = sp.getInt("latest_volume_pref", 100)
        set(value) = sp.edit { putInt("latest_volume_pref", value) }

    var latestPlaybackSpeed: Float
        get() = sp.getFloat("latest_playback_vel_pref", 1.0F)
        set(value) = sp.edit { putFloat("latest_playback_vel_pref", value) }

    var latestPlayedSong: Music?
        get() = getObjectForClass("to_restore_song_pref", Music::class.java)
        set(value) = putObjectForClass("to_restore_song_pref", value, Music::class.java)

    var favorites: List<Music>?
        get() = getObjectForType("favorite_songs_pref", typeFavorites)
        set(value) = putObjectForType("favorite_songs_pref", value, typeFavorites)

    var queue: List<Music>?
        get() = getObjectForType("queue_songs_pref", typeFavorites)
        set(value) = putObjectForType("queue_songs_pref", value, typeFavorites)

    var isQueue: Music?
        get() = getObjectForClass("is_queue_pref", Music::class.java)
        set(value) = putObjectForClass("is_queue_pref", value, Music::class.java)

    var theme
        get() = sp.getString("theme_pref", "theme_pref_auto")
        set(value) = sp.edit { putString("theme_pref", value) }

    var isBlackTheme: Boolean
        get() = sp.getBoolean("theme_pref_black", false)
        set(value) = sp.edit { putBoolean("theme_pref_black", value) }

    var accent
        get() = sp.getInt("color_primary_pref", 3)
        set(value) = sp.edit { putInt("color_primary_pref", value) }

    var activeTabsDef: List<String>
        get() = getObjectForType("active_tabs_def_pref", typeActiveTabs)
            ?: Constants.DEFAULT_ACTIVE_FRAGMENTS
        set(value) = putObjectForType("active_tabs_def_pref", value, typeActiveTabs)

    var activeTabs: List<String>
        get() = getObjectForType("active_tabs_pref", typeActiveTabs)
            ?: Constants.DEFAULT_ACTIVE_FRAGMENTS
        set(value) = putObjectForType("active_tabs_pref", value, typeActiveTabs)

    var onListEnded
        get() = sp.getString("on_list_ended_pref", Constants.CONTINUE)
        set(value) = sp.edit { putString("on_list_ended_pref", value) }

    var isCovers: Boolean
        get() = sp.getBoolean("covers_pref", false)
        set(value) = sp.edit { putBoolean("covers_pref", value) }

    var songsVisualization
        get() = sp.getString("song_visual_pref", Constants.FN)
        set(value) = sp.edit { putString("song_visual_pref", value.toString()) }

    var artistsSorting
        get() = sp.getInt("sorting_artists_pref", Constants.ASCENDING_SORTING)
        set(value) = sp.edit { putInt("sorting_artists_pref", value) }

    var foldersSorting
        get() = sp.getInt("sorting_folder_details_pref", Constants.DEFAULT_SORTING)
        set(value) = sp.edit { putInt("sorting_folder_details_pref", value) }

    var albumsSorting
        get() = sp.getInt("sorting_album_details_pref", Constants.DEFAULT_SORTING)
        set(value) = sp.edit { putInt("sorting_album_details_pref", value) }

    var allMusicSorting
        get() = sp.getInt("sorting_all_music_tab_pref", Constants.DEFAULT_SORTING)
        set(value) = sp.edit { putInt("sorting_all_music_tab_pref", value) }

    var filters: Set<String>?
        get() = sp.getStringSet("strings_filter_pref", setOf())
        set(value) = sp.edit { putStringSet("strings_filter_pref", value) }

    var fastSeekingStep
        get() = sp.getInt("fast_seeking_pref", 5)
        set(value) = sp.edit { putInt("fast_seeking_pref", value) }

    var isEqForced
        get() = sp.getBoolean("eq_pref", false)
        set(value) = sp.edit { putBoolean("eq_pref", value) }

    var isPreciseVolumeEnabled
        get() = sp.getBoolean("precise_volume_pref", true)
        set(value) = sp.edit { putBoolean("precise_volume_pref", value) }

    var isHeadsetPlugEnabled
        get() = sp.getBoolean("headsets_pref", true)
        set(value) = sp.edit { putBoolean("headsets_pref", value) }

    var playbackSpeedMode
        get() = sp.getString("playback_vel_pref", Constants.PLAYBACK_SPEED_ONE_ONLY)
        set(value) = sp.edit { putString("playback_vel_pref", value) }

    var isAnimations
        get() = sp.getBoolean("anim_pref", true)
        set(value) = sp.edit { putBoolean("anim_pref", value) }

    var continueOnEnd
        get() = sp.getBoolean("continue_on_end_pref", true)
        set(value) = sp.edit { putBoolean("continue_on_end_pref", value) }

    var hasCompletedPlayback
        get() = sp.getBoolean("has_completed_playback_pref", false)
        set(value) = sp.edit { putBoolean("has_completed_playback_pref", value) }

    var locale
        get() = sp.getString("locale_pref", null)
        set(value) = sp.edit { putString("locale_pref", value) }

    var isAskForRemoval: Boolean
        get() = sp.getBoolean("ask_confirmation_pref", true)
        set(value) = sp.edit { putBoolean("ask_confirmation_pref", value) }

    var lockRotation: Boolean
        get() = sp.getBoolean("rotation_pref", false)
        set(value) = sp.edit { putBoolean("rotation_pref", value) }

    var isSetDefSorting: Boolean
        get() = sp.getBoolean("ask_sorting_pref", true)
        set(value) = sp.edit { putBoolean("ask_sorting_pref", value) }

    private fun <T : Any> getObjectForType(key: String, type: Type): T? {
        val json = sp.getString(key, null)
        return if (json == null) {
            null
        } else {
            try {
                mMoshi.adapter<T>(type).fromJson(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Saves object into the Preferences using Moshi
    private fun <T : Any> putObjectForClass(key: String, value: T?, clazz: Class<T>) {
        val json = mMoshi.adapter(clazz).toJson(value)
        sp.edit { putString(key, json) }
    }

    private fun <T : Any> getObjectForClass(key: String, clazz: Class<T>): T? {
        val json = sp.getString(key, null)
        return if (json == null) {
            null
        } else {
            try {
                mMoshi.adapter(clazz).fromJson(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}