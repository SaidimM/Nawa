package com.saidim.nawa.view.viewModels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saidim.nawa.MusicCollector
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.view.enums.ControllerState
import com.saidim.nawa.view.enums.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    private val TAG = "MusicViewModel"

    private val repository = ServiceLocator.getRepository()
    private val musicPlayer = ServiceLocator.getPlayer()

    var fragmentCallback: (Fragment) -> Unit = {}

    private var _playState = MutableLiveData<PlayState>()
    val playState: LiveData<PlayState> = _playState

    private var _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    private var _controllerOffset = MutableLiveData(0f)
    val controllerOffset: LiveData<Float> = _controllerOffset

    private var _controllerState = MutableLiveData(ControllerState.HIDDEN)
    val controllerState: LiveData<ControllerState> = _controllerState

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.Default) {
            MusicCollector.getInstance().initData()
        }
    }

    fun getLastPlayedMusic() {

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