package com.saidim.nawa.view.controller

import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.view.enums.PlayMode


interface IPlayerController {
    fun refreshPlayList(list: ArrayList<Music>)
    fun getPlayList(): ArrayList<Music>
    fun play(index: Int = -1)
    fun pause()
    fun playNext(music: Music)
    fun playLast(music: Music)
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setPlayMode(mode: PlayMode)
    fun getCurrentMusic(): Music
    fun recycle()
}