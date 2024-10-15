package com.saidim.nawa.view.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.base.utils.LocalMediaUtils.getAlbumArtBitmap
import com.saidim.nawa.databinding.FragmentMusicListBinding
import com.saidim.nawa.databinding.LayoutItemArtistBinding
import com.saidim.nawa.databinding.LayoutItemPlayListBinding
import com.saidim.nawa.databinding.LayoutItemRecentBinding
import com.saidim.nawa.databinding.LayoutItemSongBinding
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.view.models.ArtistModel
import com.saidim.nawa.view.models.RecentModel
import com.saidim.nawa.view.viewModels.MusicListViewModel
import com.saidim.nawa.view.viewModels.MusicViewModel

class MusicListFragment : BaseFragment() {
    override val binding: FragmentMusicListBinding by lazy { FragmentMusicListBinding.inflate(layoutInflater) }

    private val viewModel: MusicListViewModel by lazy { getFragmentScopeViewModel(MusicListViewModel::class.java) }
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }

    private val artistAdapter: BaseRecyclerViewAdapter<ArtistModel, LayoutItemArtistBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<ArtistModel, LayoutItemArtistBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_artist

            override fun onBindItem(binding: LayoutItemArtistBinding, item: ArtistModel, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                Glide.with(requireContext()).load(item.imageUri).into(binding.songImage)
            }
        }
    }

    private val songAdapter: BaseRecyclerViewAdapter<Music, LayoutItemSongBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<Music, LayoutItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_song

            override fun onBindItem(binding: LayoutItemSongBinding, item: Music, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                val albumCover = getAlbumArtBitmap(item.path)
                Glide.with(requireContext()).load(albumCover).into(binding.songImage)
            }
        }
    }

    private val recentAdapter: BaseRecyclerViewAdapter<RecentModel, LayoutItemRecentBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<RecentModel, LayoutItemRecentBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_recent

            override fun onBindItem(binding: LayoutItemRecentBinding, item: RecentModel, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                Glide.with(requireContext()).load(item.image).into(binding.songImage)
            }
        }
    }

    private val playListAdapter: BaseRecyclerViewAdapter<PlayList, LayoutItemPlayListBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<PlayList, LayoutItemPlayListBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_play_list

            override fun onBindItem(binding: LayoutItemPlayListBinding, item: PlayList, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                Glide.with(requireContext()).load(item.cover).into(binding.songImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initView()
        initDate()
        observe()
    }

    private fun initView() {
        binding.playListCreate.setOnClickListener { viewModel.createRandomPlayList() }
    }

    private fun initDate() {
        state.getMusic()
        viewModel.loadPlayLists()
    }

    private fun initRecyclerView() {
        binding.artistsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.artistsList.adapter = artistAdapter
        binding.songsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.songsList.adapter = songAdapter
        binding.recentList.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.recentList.adapter = recentAdapter
        binding.playList.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.playList.adapter = playListAdapter
    }

    private fun observe() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {}
        state.musics.observe(viewLifecycleOwner) { songAdapter.data = it }
        state.progress.observe(viewLifecycleOwner) {
            SnackbarUtils.with(requireView()).setAction("progress: ${it * 100}%") { }
        }
        viewModel.playLists.observe(viewLifecycleOwner) { playListAdapter.data = it }
    }
}