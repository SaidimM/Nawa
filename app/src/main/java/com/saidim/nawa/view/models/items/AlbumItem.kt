package com.saidim.nawa.view.models.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.saidim.nawa.media.local.bean.Music

class AlbumItem() : Item {
    var id: String = ""
    override val title: String = ""
    override val image: Drawable = ColorDrawable(Color.CYAN)
    override val data = mutableListOf<MusicItem>()
}
