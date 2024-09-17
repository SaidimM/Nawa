package com.saidim.nawa.media.local.bean

import com.saidim.nawa.media.local.enums.SortType


data class AlbumSortModel(
    var sortType: SortType = SortType.CREATED,
    var isDescending: Boolean = true,
    var splitByTime: Boolean = true
)