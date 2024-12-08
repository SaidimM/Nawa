package com.saidim.nawa.view.models.lists

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.App.Companion.getString
import com.saidim.nawa.R
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.view.enums.SortMode
import com.saidim.nawa.view.models.items.ArtistItem

class ArtistList : IList {
    override val data = mutableListOf<ArtistItem>()
    val map = mutableMapOf<String, ArtistItem>()

    override val title: String = getString(R.string.text_artist)
    override val subtitle: String = ""
    override val display: Drawable = ColorDrawable(Color.CYAN)
    override var sortMode: SortMode = SortMode.NAME
}