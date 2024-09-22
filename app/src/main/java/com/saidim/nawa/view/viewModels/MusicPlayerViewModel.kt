package com.saidim.nawa.view.viewModels

import LogUtil
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.remote.lyrics.Lyric
import com.saidim.nawa.view.enums.PlayerViewState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MusicPlayerViewModel : ViewModel() {
    private val TAG = "MusicPlayerViewModel"

    private val repository = ServiceLocator.getRepository()

    private var _lyrics = MutableLiveData<ArrayList<Lyric>>()
    val lyrics: LiveData<ArrayList<Lyric>> = _lyrics

    private var _viewState = MutableLiveData<PlayerViewState>()
    val viewState: LiveData<PlayerViewState> = _viewState

    private var _albumCover = MutableLiveData<Bitmap>()
    val albumCover: LiveData<Bitmap> = _albumCover

    fun updateViewState() {
        when (viewState.value) {
            null -> _viewState.value = PlayerViewState.LYRICS
            PlayerViewState.ALBUM -> _viewState.value = PlayerViewState.LYRICS
            else -> _viewState.value = PlayerViewState.ALBUM
        }
    }

    fun getLyrics(music: Music) {
        viewModelScope.launch {
            repository.getMusicLyrics(music)
                .catch { LogUtil.e(TAG, "getLyrics: $it")}
                .collect { _lyrics.postValue(it as ArrayList<Lyric>) }
        }
    }

    fun getAlbumCover(music: Music) {
        viewModelScope.launch {
            repository.getAlbumCover(music)
                .catch {
                    LogUtil.e(TAG, "getAlbumCover: $it")
                    it.printStackTrace()
                }.collect { _albumCover.postValue(it) }
        }
    }
}