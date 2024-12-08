package com.saidim.nawa.media.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores play lists, single played musics, artists, albums
 * Item types separated by type name
 * id: Concatenated by
 * {@link com.saidim.nawa.view.enums.ItemType}
 * - id
 */
@Entity
data class Recent(
    @PrimaryKey
    val id: String = "",
    val type: String = "",
    val time: String = ""
)
