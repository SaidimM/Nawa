package com.saidim.nawa.view.models.lists

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.view.enums.SortMode
import com.saidim.nawa.view.models.items.MusicItem

class MusicList : IList {

    override var data = mutableListOf<MusicItem>()
    override var title: String = "Library"
    override var subtitle: String = ""
    override var display: Drawable = ColorDrawable(Color.CYAN)
    override var sortMode: SortMode = SortMode.NAME
}