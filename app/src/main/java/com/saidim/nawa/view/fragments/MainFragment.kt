package com.saidim.nawa.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.base.utils.LocalMediaUtils.getAlbumArtBitmap
import com.saidim.nawa.databinding.FragmentMainBinding
import com.saidim.nawa.databinding.LayoutItemPlayListBinding
import com.saidim.nawa.databinding.LayoutItemRecentBinding
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.view.models.items.RecentItem
import com.saidim.nawa.view.viewModels.MainViewModel
import com.saidim.nawa.view.viewModels.MusicViewModel

class MainFragment : BaseFragment() {
    override val binding: FragmentMainBinding by lazy { FragmentMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels()
    private val state: MusicViewModel by viewModels()

    private val recentAdapter: BaseRecyclerViewAdapter<RecentItem, LayoutItemRecentBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<RecentItem, LayoutItemRecentBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_recent

            override fun onBindItem(binding: LayoutItemRecentBinding, item: RecentItem, position: Int) {
                binding.songImage.background = null
                binding.artistName.text = item.title
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
                val cover = if (item.cover.isEmpty()) getAlbumArtBitmap(item.getList()[0].path) else item.cover
                Glide.with(requireContext()).load(cover).into(binding.songImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        observe()
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun initData() {
        state.getMusic()
        viewModel.loadPlayLists()
    }

    private fun initRecyclerView() {
        binding.recentList.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.recentList.adapter = recentAdapter
        binding.playList.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.playList.adapter = playListAdapter
    }

    override fun observe() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {}
        state.progress.observe(viewLifecycleOwner) {
            SnackbarUtils.with(requireView()).setAction("progress: ${it * 100}%") { }
        }
        viewModel.playLists.observe(viewLifecycleOwner) { playListAdapter.data = it }
    }
}