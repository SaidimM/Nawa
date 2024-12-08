package com.saidim.nawa.view.models.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.view.models.lists.PlayLists

class PlayListItem(private val playList: PlayList): Item {
    override var title: String = ""
    override var image: Drawable = ColorDrawable(Color.CYAN)
    override var data = arrayListOf<MusicItem>()
}