package com.saidim.nawa.view.models.items

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.base.utils.LocalMediaUtils.getAlbumArtBitmap
import com.saidim.nawa.media.local.bean.Music

class MusicItem(music: Music): Item {
    override val title: String = music.name
    override val image: Drawable = BitmapDrawable(Utils.getApp().resources, getAlbumArtBitmap(music.path))
    override val data = music
}