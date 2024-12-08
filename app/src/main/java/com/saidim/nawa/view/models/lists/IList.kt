package com.saidim.nawa.view.models.lists

import android.graphics.drawable.Drawable
import com.saidim.nawa.view.enums.SortMode
import com.saidim.nawa.view.models.items.Item

interface IList {
    val data: List<Item>

    val title: String
    val subtitle: String
    val display: Drawable

    var sortMode: SortMode
}
