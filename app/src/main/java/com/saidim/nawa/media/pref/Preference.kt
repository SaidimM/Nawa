package com.saidim.nawa.media.pref

import android.content.Context
import androidx.core.content.edit
import com.blankj.utilcode.util.Utils

class Preference {
    private val sharedPreferences = Utils.getApp().getSharedPreferences("nawa", Context.MODE_PRIVATE)

    var isFocusEnabled
        get() = sharedPreferences.getBoolean("focus_pref", true)
        set(value) = sharedPreferences.edit { putBoolean("focus_pref", value) }
}