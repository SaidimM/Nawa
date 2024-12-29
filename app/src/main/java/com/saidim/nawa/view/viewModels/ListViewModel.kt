package com.saidim.nawa.view.viewModels

import androidx.lifecycle.ViewModel
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.player.PlayerController
import com.saidim.nawa.view.models.lists.IList
import com.saidim.nawa.view.models.lists.MusicList

class ListViewModel: ViewModel() {
    lateinit var list: IList
    private lateinit var musics: List<Music>

    fun onItemClicked(index: Int) {
        LogUtil.d("list is MusicList: " + (list is MusicList))
        if (::musics.isInitialized) {
            PlayerController.getInstance().startPlay(index, musics)
        } else {
            if (list is MusicList) {
                musics = (list as MusicList).data.map { it.data }
                PlayerController.getInstance().startPlay(index, musics)
            }
        }
    }
}