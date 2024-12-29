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
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

class Preference {
    private val sp = Utils.getApp().getSharedPreferences("nawa", Context.MODE_PRIVATE)
    private val mMoshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

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

    var latestPlayedSong: Music?
        get() = getObjectForClass("to_restore_song_pref", Music::class.java)
        set(value) = putObjectForClass("to_restore_song_pref", value, Music::class.java)

    var isCovers: Boolean
        get() = sp.getBoolean("covers_pref", false)
        set(value) = sp.edit { putBoolean("covers_pref", value) }

    var songsVisualization
        get() = sp.getString("song_visual_pref", Constants.FN)
        set(value) = sp.edit { putString("song_visual_pref", value.toString()) }

    var continueOnEnd
        get() = sp.getBoolean("continue_on_end_pref", true)
        set(value) = sp.edit { putBoolean("continue_on_end_pref", value) }

    var hasCompletedPlayback
        get() = sp.getBoolean("has_completed_playback_pref", false)
        set(value) = sp.edit { putBoolean("has_completed_playback_pref", value) }


    var enableDisplayArtistsInMainWindow
        get() = sp.getBoolean("enableDisplayArtistsInMainWindow", false)
        set(value) = sp.edit { putBoolean("enableDisplayArtistsInMainWindow", value) }

    var enableDisplayRecentInMainWindow
        get() = sp.getBoolean("enableDisplayRecentInMainWindow", false)
        set(value) = sp.edit { putBoolean("enableDisplayRecentInMainWindow", value) }

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
        return sp.getString(key, null)?.let { json ->
            runCatching { mMoshi.adapter(clazz).fromJson(json) }.getOrNull()
        }
    }
}