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
import com.saidim.nawa.databinding.FragmentMainBinding
import com.saidim.nawa.databinding.ItemTypeSelectionBinding
import com.saidim.nawa.databinding.LayoutItemPlayListBinding
import com.saidim.nawa.databinding.LayoutItemRecentBinding
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.view.models.RecentModel
import com.saidim.nawa.view.models.SelectionTypeModel
import com.saidim.nawa.view.viewModels.MainViewModel
import com.saidim.nawa.view.viewModels.MusicViewModel

class MainFragment : BaseFragment() {
    override val binding: FragmentMainBinding by lazy { FragmentMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels()
    private val state: MusicViewModel by viewModels()

    private val typeAdapter: BaseRecyclerViewAdapter<SelectionTypeModel, ItemTypeSelectionBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<SelectionTypeModel, ItemTypeSelectionBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_type_selection

            override fun onBindItem(binding: ItemTypeSelectionBinding, item: SelectionTypeModel, position: Int) {
                binding.item = item
                binding.root.setOnClickListener { state.gotoFragment(ListFragment(item)) }
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
        typeAdapter.data = viewModel.selections
    }

    private fun initRecyclerView() {
        binding.selections.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.selections.adapter = typeAdapter
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