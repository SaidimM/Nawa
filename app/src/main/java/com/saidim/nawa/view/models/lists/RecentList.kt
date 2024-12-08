package com.saidim.nawa.view.models.lists

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.App.Companion.getString
import com.saidim.nawa.MusicCollector
import com.saidim.nawa.R
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Recent
import com.saidim.nawa.view.enums.ItemType
import com.saidim.nawa.view.enums.SortMode
import com.saidim.nawa.view.models.items.Item
import com.saidim.nawa.view.models.items.MusicItem
import com.saidim.nawa.view.models.items.PlayListItem
import com.saidim.nawa.view.models.items.RecentItem

class RecentList : IList {
    override val data = mutableListOf<Item>()
    override val title: String = getString(R.string.text_recent)
    override val subtitle: String = ""
    override val display: Drawable = ColorDrawable(Color.CYAN)
    override var sortMode = SortMode.NAME
}