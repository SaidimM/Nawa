package com.saidim.nawa.player

interface PlayerControllerListener {
    fun onIndexChanged(index: Int)
    fun onPlayStateChanged()
    fun onPlayModeChanged(isRepeatEnabled: Boolean, isShuffleEnabled: Boolean)
    fun onClose()
}