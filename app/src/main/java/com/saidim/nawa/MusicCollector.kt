package com.saidim.nawa

import com.saidim.nawa.view.enums.ItemType
import com.saidim.nawa.view.models.items.AlbumItem
import com.saidim.nawa.view.models.items.ArtistItem
import com.saidim.nawa.view.models.items.MusicItem
import com.saidim.nawa.view.models.items.PlayListItem
import com.saidim.nawa.view.models.lists.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MusicCollector {
    private val repository = ServiceLocator.getRepository()

    val musicList = MusicList()
    val albumList = AlbumList()
    val artistList = ArtistList()
    val playLists = PlayLists()
    val recentList = RecentList()

    suspend fun initData() {
        coroutineScope {
            repository.loadMusics()
            launch(Dispatchers.Default) {
                val musics = ServiceLocator.getRepository().getMusicList()
                musics.forEach { t ->
                    val musicItem = MusicItem(t)
                    musicList.data.add(musicItem)
                    if (artistList.map.contains(t.album)) artistList.map[t.album]!!.data.add(musicItem)
                    else artistList.map.put(t.artist, ArtistItem().apply { data.add(musicItem) })
                    if (albumList.map.contains(t.album)) albumList.map[t.album]!!.data.add(musicItem)
                    else albumList.map.put(t.artist, AlbumItem().apply { data.add(musicItem) })
                }
                artistList.map.forEach { t, u -> artistList.data.add(u) }
                albumList.map.forEach { t, u -> albumList.data.add(u) }
                LogUtil.d("artistList size: " + albumList.data.size)
                LogUtil.d("albumList size: " + albumList.data.size)
            }
            launch(Dispatchers.Default) {
                val localRecents = repository.getRecentList()
                val localMusics = repository.getMusicList()
                val localLists = repository.getPlayList()

                localRecents.forEach { recent ->
                    val typeName = if (recent.id.contains('-')) recent.id.substring(recent.id.indexOf('-')) else ""
                    val type = ItemType.valueOf(typeName)
                    val id = if (recent.id.contains('-')) recent.id.substring(
                        recent.id.indexOf('-') + 1,
                        recent.id.length
                    ) else ""
                    when (type) {
                        ItemType.MUSIC -> localMusics.find { it.id.toString() == id }?.let { MusicItem(it) }
                        ItemType.PLAY_LIST -> localLists.find { it.id.toString() == id }?.let { PlayListItem(it) }
                        ItemType.ARTIST -> artistList.data.find { item -> item.id == id }
                        ItemType.ALBUM -> albumList.data.find { item -> item.id == id }
                        else -> null
                    }?.let { item -> recentList.data.add(item) }
                }
                LogUtil.d("recentList size: " + recentList.data.size)
            }
            launch(Dispatchers.Default) {
                val localLists = repository.getPlayList()
                localLists.forEach { t ->
                    val listItem = PlayListItem(t).apply {
                        data.addAll(t.getList().map { music -> MusicItem(music) })
                        title = t.name
                    }
                    playLists.data.add(listItem)
                }
                LogUtil.d("playLists size: " + recentList.data.size)
            }
        }
    }

    companion object {
        private var INSTANCE: MusicCollector? = null
        fun getInstance(): MusicCollector {
            if (INSTANCE == null) {
                INSTANCE = MusicCollector()
            }
            return INSTANCE!!
        }
    }
}