package com.saidim.nawa.view.models.lists

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.App.Companion.getString
import com.saidim.nawa.view.enums.SortMode
import com.saidim.nawa.R
import com.saidim.nawa.view.models.items.PlayListItem

class PlayLists: IList {
    override val data = mutableListOf<PlayListItem>()
    override val title: String = getString(R.string.text_play_list)
    override val subtitle: String = ""
    override val display: Drawable = ColorDrawable(Color.CYAN)
    override var sortMode: SortMode = SortMode.NAME
}