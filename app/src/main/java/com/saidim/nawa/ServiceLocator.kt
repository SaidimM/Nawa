package com.saidim.nawa

import com.saidim.nawa.media.IMusicRepository
import com.saidim.nawa.media.MusicRepository
import com.saidim.nawa.media.local.LocalDataSource
import com.saidim.nawa.media.local.database.NawaDatabase
import com.saidim.nawa.media.pref.Preference
import com.saidim.nawa.media.remote.RemoteDataSource
import com.saidim.nawa.player.IPlayerController
import com.saidim.nawa.player.PlayerController

object ServiceLocator {
    private val nawaRepository: IMusicRepository by lazy {
        val database = NawaDatabase.getInstance()
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()
        MusicRepository(localDataSource, remoteDataSource)
    }
    private val nawaPreference: Preference by lazy { Preference() }
    private val nawaPayer: IPlayerController by lazy { PlayerController.getInstance() }

    fun getRepository(): IMusicRepository = nawaRepository

    fun getPreference() = nawaPreference

    fun getPlayer() = nawaPayer
}