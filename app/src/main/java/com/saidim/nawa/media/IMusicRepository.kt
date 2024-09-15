package com.saidim.nawa.media

import android.graphics.Bitmap
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayHistory
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.media.remote.lyrics.Lyric
import kotlinx.coroutines.flow.Flow

interface IMusicRepository {
    fun getLastPlayedMusic(): Flow<Music>
    fun saveLastPlayedMusic(music: Music)
    fun getMusicList(): Flow<List<Music>>
    fun removeMusicFromDevice(music: Music): Flow<Result<Any>>
    fun getMusicLyrics(music: Music): Flow<List<Lyric>>
    fun getFavoriteMusicList(): Flow<PlayList>
    fun getPlayLists(): Flow<List<PlayList>>
    fun alterPlayList(playList: PlayList): Flow<Result<Any>>
    fun addPlayList(playList: PlayList): Flow<Result<Any>>
    fun removePlayList(playList: PlayList): Flow<Result<Any>>
    fun getRecentPlayList(): Flow<List<PlayHistory>>
    fun addRecent(playHistory: PlayHistory): Flow<Result<Any>>
    fun getAlbumInfo(music: Music): Flow<Result<Any>>
    fun getArtistInfo(music: Music): Flow<Result<Any>>
    fun getMusicVideo(music: Music): Flow<Result<Any>>
    fun getAlbumCover(music: Music): Flow<Bitmap>
}