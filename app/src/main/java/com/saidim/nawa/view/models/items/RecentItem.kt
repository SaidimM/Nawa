package com.saidim.nawa.view.models.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.media.local.bean.Recent

class RecentItem(private val recent: Recent) : Item {
    override val title: String = ""
    override val image: Drawable = ColorDrawable(Color.CYAN)
    override val data = recent
}