package com.saidim.nawa.media.remote.artist

data class BMusic(
    val bitrate: Int,
    val dfsId: Int,
    val extension: String,
    val id: Long,
    val name: Any,
    val playTime: Int,
    val size: Int,
    val sr: Int,
    val volumeDelta: Double
)