package com.saidim.nawa.view.models

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.blankj.utilcode.util.Utils

data class SelectionTypeModel(
    val titleRes: Int,
    val iconRes: Int,
) {
    fun getTitle() = try { Utils.getApp().getString(titleRes) } catch(_: Exception) { "" }
    fun getDrawable() = try { Utils.getApp().getDrawable(iconRes) } catch (_: Exception) { ColorDrawable(Color.CYAN) }
}