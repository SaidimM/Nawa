package com.saidim.nawa.view.models

import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayList

data class PlayListModel(
    val playList: PlayList,
    val lists: List<Music>
)
