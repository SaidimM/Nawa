package com.saidim.nawa

import com.saidim.nawa.media.IMusicRepository
import com.saidim.nawa.media.MusicRepository
import com.saidim.nawa.media.local.LocalDataSource
import com.saidim.nawa.media.local.database.NawaDatabase
import com.saidim.nawa.media.pref.Preference
import com.saidim.nawa.media.remote.RemoteDataSource
import com.saidim.nawa.view.controller.IPlayerController
import com.saidim.nawa.view.controller.PlayerController

object ServiceLocator {
    private val repository: IMusicRepository? = null
    private val playerController: IPlayerController? = null
    private val preference: Preference? = null

    private fun getMusicRepository(): IMusicRepository {
        val database = NawaDatabase.getInstance()
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()
        return MusicRepository(localDataSource, remoteDataSource)
    }

    fun provideMusicRepository() = repository ?: getMusicRepository()

    fun provideMusicPlayer() = playerController ?: PlayerController()

    fun provicePreference() = preference ?: Preference()
}