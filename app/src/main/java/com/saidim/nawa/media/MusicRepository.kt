package com.saidim.nawa.media

import com.blankj.utilcode.util.SPUtils
import com.saidim.nawa.Constants.MUSIC_ID
import com.saidim.nawa.Constants.getAlbumCoverPath
import com.saidim.nawa.media.local.LocalDataSource
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayHistory
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.media.remote.RemoteDataSource
import com.saidim.nawa.media.remote.music.MusicDetailResult
import com.saidim.nawa.media.remote.search.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class MusicRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IMusicRepository {
    private val TAG = "MusicRepository"

    override fun getLastPlayedMusic() = flow {
        val musicId = SPUtils.getInstance().getString(MUSIC_ID, "-1").toLong()
        if (musicId == -1L) error("No music Stored!")
        else emit(localDataSource.getMusic(musicId) ?: error("Didn't found music: $musicId"))
    }

    override fun saveLastPlayedMusic(music: Music) = SPUtils.getInstance().put(MUSIC_ID, music.id.toString())

    override fun getMusicList() = localDataSource.getMusicList()

    override fun removeMusicFromDevice(music: Music) = localDataSource.removeMusic(music).flowOn(dispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMusicLyrics(music: Music) =
        if (music.mediaId.isEmpty()) remoteDataSource.searchMusic(music).filter { it.isSuccess }
            .flatMapConcat { result -> localDataSource.syncWithRemote(music, result.getOrNull() as Song) }
            .flatMapConcat { remoteDataSource.getLyrics(music) }
            .flatMapConcat { localDataSource.saveMusicLyrics(music, it.lrc.lyric) }
            .flatMapConcat { localDataSource.getLyrics(music) }.flowOn(dispatcher)
        else if (localDataSource.isMusicLyricsExist(music)) localDataSource.getLyrics(music).flowOn(dispatcher)
        else remoteDataSource.getLyrics(music)
            .flatMapConcat { result -> localDataSource.saveMusicLyrics(music, result.lrc.lyric) }
            .flatMapConcat { localDataSource.getLyrics(music) }.flowOn(dispatcher)

    override fun getFavoriteMusicList() = localDataSource.getFavoriteMusicList().flowOn(dispatcher)

    override fun getPlayLists() = localDataSource.getAllPlayLists().flowOn(dispatcher)

    override fun alterPlayList(playList: PlayList) = localDataSource.alterPlayList(playList).flowOn(dispatcher)

    override fun addPlayList(playList: PlayList) = localDataSource.addPlayList(playList).flowOn(dispatcher)

    override fun removePlayList(playList: PlayList) = localDataSource.deletePlayList(playList).flowOn(dispatcher)

    override fun getRecentPlayList() = localDataSource.getRecentPlayed().flowOn(dispatcher)

    override fun addRecent(playHistory: PlayHistory) = localDataSource.addRecent(playHistory).flowOn(dispatcher)

    override fun getAlbumInfo(music: Music) = remoteDataSource.getAlbum(music).flowOn(dispatcher)

    override fun getArtistInfo(music: Music) = remoteDataSource.getArtist(music).flowOn(dispatcher)

    override fun getMusicVideo(music: Music) = remoteDataSource.getMv(music).flowOn(dispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAlbumCover(music: Music) =
        if (music.mediaId.isEmpty()) remoteDataSource.searchMusic(music).filter { it.isSuccess }
            .flatMapConcat { localDataSource.syncWithRemote(music, it.getOrNull() as Song) }
            .flatMapConcat { remoteDataSource.getMusicDetail(music) }
            .flatMapConcat { remoteDataSource.downloadAlbumCover(music, it.getOrNull() as MusicDetailResult) }
            .flatMapConcat { localDataSource.getMusicAlbumCover(music) }.flowOn(dispatcher)
        else if (File(getAlbumCoverPath(music)).exists()) localDataSource.getMusicAlbumCover(music).flowOn(dispatcher)
        else remoteDataSource.getMusicDetail(music)
            .flatMapConcat { remoteDataSource.downloadAlbumCover(music, it.getOrNull() as MusicDetailResult) }
            .flatMapConcat { localDataSource.getMusicAlbumCover(music) }

}