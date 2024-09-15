package com.saidim.nawa.media.remote.search

data class Result(
    val songCount: Int = 0,
    val songs: List<Song> = listOf()
)