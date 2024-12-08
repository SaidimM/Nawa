package com.saidim.nawa.view.models.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

class ArtistItem : Item {
    var id: String = ""
    override var title: String = ""
    override var image: Drawable = ColorDrawable(Color.CYAN)
    override var data = mutableListOf<MusicItem>()
}
