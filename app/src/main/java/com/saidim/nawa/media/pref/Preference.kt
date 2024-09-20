package com.saidim.nawa.media.pref

import android.content.Context
import androidx.core.content.edit
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.Constants.CLOSE_ACTION
import com.saidim.nawa.Constants.REPEAT_ACTION
import com.saidim.nawa.media.models.NotificationAction
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class Preference {
    private val sp = Utils.getApp().getSharedPreferences("nawa", Context.MODE_PRIVATE)
    private val mMoshi = Moshi.Builder().build()

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