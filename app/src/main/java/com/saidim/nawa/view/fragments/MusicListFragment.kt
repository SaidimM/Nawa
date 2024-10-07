package com.saidim.nawa.view.fragments

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.base.utils.LocalMediaUtils.getAlbumArtBitmap
import com.saidim.nawa.databinding.FragmentMusicListBinding
import com.saidim.nawa.databinding.LayoutItemArtistBinding
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

    private val songAdapter: BaseRecyclerViewAdapter<Music, LayoutItemArtistBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<Music, LayoutItemArtistBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_artist

            override fun onBindItem(binding: LayoutItemArtistBinding, item: Music, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                val albumCover = getAlbumArtBitmap(item.path)
                Glide.with(requireContext()).load(albumCover).into(binding.songImage)
            }
        }
    }

    private val recentAdapter: BaseRecyclerViewAdapter<RecentModel, LayoutItemArtistBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<RecentModel, LayoutItemArtistBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_artist

            override fun onBindItem(binding: LayoutItemArtistBinding, item: RecentModel, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                Glide.with(requireContext()).load(item.image).into(binding.songImage)
            }
        }
    }

    private val playListAdapter: BaseRecyclerViewAdapter<PlayList, LayoutItemArtistBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<PlayList, LayoutItemArtistBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_artist

            override fun onBindItem(binding: LayoutItemArtistBinding, item: PlayList, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.name
                Glide.with(requireContext()).load(item.cover).into(binding.songImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observe()
    }

    private fun initRecyclerView() {

    }

    private fun observe() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {}
        state.musics.observe(viewLifecycleOwner) { }
        state.progress.observe(viewLifecycleOwner) {
            SnackbarUtils.with(requireView()).setAction("progress: ${it * 100}%") { }
        }
    }
}