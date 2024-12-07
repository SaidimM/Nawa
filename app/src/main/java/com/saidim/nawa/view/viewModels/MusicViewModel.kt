package com.saidim.nawa.view.viewModels

import LogUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.Constants
import com.saidim.nawa.MusicCollector
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.view.enums.ControllerState
import com.saidim.nawa.view.enums.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = ServiceLocator.getRepository()
    private val musicPlayer = ServiceLocator.getPlayer()

    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    var fragmentCallback: (Fragment) -> Unit = {}

    fun permissionGranted(isGranted: Boolean) {
        _isPermissionGranted.postValue(isGranted)
    }

    fun createDirectories() {
        val albumDir = Constants.ALBUM_COVER_DIR
        val lyricDir = Constants.LYRIC_DIR
        if (File(albumDir).mkdirs()) LogUtil.d(TAG, "create album dir success")
        if (File(lyricDir).mkdirs()) LogUtil.d(TAG, "create lyric dir success")
    }

    private var _music = MutableLiveData<Music>()
    val music: LiveData<Music> = _music

    private var _playState = MutableLiveData<PlayState>()
    val playState: LiveData<PlayState> = _playState

    private var _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    private var _controllerOffset = MutableLiveData(0f)
    val controllerOffset : LiveData<Float> = _controllerOffset

    private var _controllerState = MutableLiveData(ControllerState.HIDDEN)
    val controllerState: LiveData<ControllerState> = _controllerState

    fun getMusic() {
        viewModelScope.launch(Dispatchers.Default) {
            MusicCollector.getInstance().initData()
        }
    }

    fun getLastPlayedMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLastPlayedMusic()
                .collect { _music.postValue(it) }
        }
    }

    fun updateControllerOffset(offset: Float) {
        _controllerOffset.value = offset
    }

    fun updateControllerState(state: ControllerState) {
        _controllerState.value = state
    }

    fun recyclePlayer() {
        musicPlayer.releasePlayer()
    }
}