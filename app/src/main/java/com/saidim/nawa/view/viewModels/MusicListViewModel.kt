package com.saidim.nawa.view.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.remote.mv.MusicVideoResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicListViewModel : ViewModel() {

//    private val repository = ServiceLocator.provideMusicRepository()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    fun getMv(music: Music) {
        viewModelScope.launch {
        }
    }
}