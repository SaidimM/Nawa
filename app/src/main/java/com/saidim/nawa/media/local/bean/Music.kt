package com.saidim.nawa.media.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Music(
    @PrimaryKey
    var id: Long = 0,
    var name: String = "",
    var singer: String = "",
    var album: String = "",
    var albumId: Long = 0,
    var size: Long = 0L,
    var duration: Long = 0L,
    var path: String = "",
    var mediaId: String = "",
    var mediaArtistId: String = "",
    var mediaAlbumId: String = "",
    var mvId: Int = 0,
    var albumCoverBlurHash: String = "",
    var artist: String = "",
    var year: Int = 0,
    var track: Int = 0,
    var title: String = "",
    var displayName: String = "",
    var relativePath: String = "",
    var launchedBy: String = "",
    var startFrom: Int = 0,
    val dateAdded: Int = 0,
) : Serializable