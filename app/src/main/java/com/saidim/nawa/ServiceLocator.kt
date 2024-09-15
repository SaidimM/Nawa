package com.saidim.nawa

import com.saidim.nawa.media.IMusicRepository
import com.saidim.nawa.media.MusicRepository
import com.saidim.nawa.media.local.LocalDataSource
import com.saidim.nawa.media.local.database.MusicDatabase
import com.saidim.nawa.media.remote.RemoteDataSource
import com.saidim.nawa.player.controller.IPlayerController
import com.saidim.nawa.player.controller.PlayerController

object ServiceLocator {
    private val repository: IMusicRepository? = null
    private val playerController: IPlayerController? = null

    private fun getMusicRepository(): IMusicRepository {
        val database = MusicDatabase.getInstance()
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()
        return MusicRepository(localDataSource, remoteDataSource)
    }

    fun provideMusicRepository(): IMusicRepository {
        return repository ?: getMusicRepository()
    }

    fun provideMusicPlayer(): IPlayerController {
        return playerController ?: PlayerController()
    }
}