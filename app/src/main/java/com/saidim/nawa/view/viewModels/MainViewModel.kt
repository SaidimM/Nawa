package com.saidim.nawa.view.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.media.remote.mv.MusicVideoResult
import com.saidim.nawa.view.models.lists.MusicList
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    companion object {
        const val TAG = "MusicListViewModel"
    }

    private val musicList = MusicList()

    private val repository = ServiceLocator.getRepository()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    private var _playLists: MutableLiveData<List<PlayList>> = MutableLiveData()
    val playLists: LiveData<List<PlayList>> = _playLists

    fun loadPlayLists() {
        viewModelScope.launch {
            repository.getPlayLists().collect {
                _playLists.postValue(it)
            }
        }
    }
}