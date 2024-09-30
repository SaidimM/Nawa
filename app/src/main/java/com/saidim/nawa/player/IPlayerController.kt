package com.saidim.nawa.player

import com.saidim.nawa.media.local.bean.Music

interface IPlayerController {
    fun initPlayerService(service: PlayerService)
    fun initMediaPlayer(music: Music? = null, needReset: Boolean = false)
    fun updateCurrentMusic(music: Music, musics: List<Music> = listOf())
    fun updateCurrentList(musics: List<Music>)

    fun syncPlayerIndex()
    fun stopSyncPlayerIndex()

    fun stopPlaybackService(stopPlayback: Boolean, fromUser: Boolean, fromFocus: Boolean)

    fun requestAudioFocus()
    fun looseAudioFocus()

    fun playOrPause(forcePause: Boolean = false)
    fun playNext()
    fun playPrevious()
    fun releasePlayer()

    fun setRepeatEnabled(enabled: Boolean = false)
    fun setShuffleEnabled(enabled: Boolean = false)

    fun seekTo(pos: Int)
    fun setVolume(volume: Int)

    fun isPlaying(): Boolean
    fun getCurrentMusic(): Music?
    fun getCurrentList(): List<Music>
}