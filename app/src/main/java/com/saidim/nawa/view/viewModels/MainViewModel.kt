package com.saidim.nawa.view.viewModels

import LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.media.remote.mv.MusicVideoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    companion object {
        const val TAG = "MusicListViewModel"
    }

    private val repository = ServiceLocator.getRepository()
    var index: Int = 0

    private var _musicVideo: MutableLiveData<MusicVideoResult> = MutableLiveData()
    val musicVideo: LiveData<MusicVideoResult> = _musicVideo

    private var _playLists: MutableLiveData<List<PlayList>> = MutableLiveData()
    val playLists: LiveData<List<PlayList>> = _playLists

    fun getMv(music: Music) {
        viewModelScope.launch {
        }
    }

    fun loadPlayLists() {
        viewModelScope.launch {
            repository.getPlayLists().collect {
                _playLists.postValue(it)
            }
        }
    }

    fun createRandomPlayList() {
        LogUtil.d(TAG, "createRandomPlayList")
        val playlist = PlayList().apply {
            name = "list $id"
            description = "description"
            cover = ""
            createTime = System.currentTimeMillis()
            updateTime = System.currentTimeMillis()
            songList = ""
        }
        viewModelScope.launch {
            LogUtil.d(TAG, playlist.toString())
            val newList = repository.getMusicList().shuffled().take(5)
            playlist.setList(newList)
            repository.addPlayList(playList = playlist).flowOn(Dispatchers.IO).collect()
            LogUtil.d(TAG, playlist.toString())
            repository.getPlayLists().collect {
                LogUtil.d(TAG, it.toString())
                _playLists.postValue(it)
            }
        }
    }
}