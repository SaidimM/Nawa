package com.saidim.nawa.view.viewModels

import androidx.lifecycle.ViewModel
import com.saidim.nawa.player.PlayerController
import com.saidim.nawa.view.models.lists.IList
import com.saidim.nawa.view.models.lists.MusicList

class ListViewModel: ViewModel() {
    lateinit var list: IList

    fun onItemClicked(index: Int) {
        LogUtil.d("list is MusicList: " + (list is MusicList))
        if (list is MusicList) {
            val musics = (list as MusicList).data.map { it.data }
            PlayerController.getInstance().startPlay(index, musics)
        }
    }
}