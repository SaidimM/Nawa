package com.saidim.nawa.media.remote.music

data class MusicDetailResult(
    val code: Int = 0,
    val equalizers: Equalizers = Equalizers(),
    val songs: List<Song> = listOf()
)