package com.saidim.nawa.view.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.base.utils.ViewUtils.loadAlbumCover
import com.saidim.nawa.databinding.FragmentMusicListBinding
import com.saidim.nawa.databinding.ItemSongBinding
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.view.viewModels.MusicListViewModel
import com.saidim.nawa.view.viewModels.MusicViewModel

class MusicListFragment : BaseFragment() {
    override val binding: FragmentMusicListBinding by lazy { FragmentMusicListBinding.inflate(layoutInflater) }

    private val viewModel: MusicListViewModel by lazy { getFragmentScopeViewModel(MusicListViewModel::class.java) }
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private lateinit var adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observe()
    }

    private fun initRecyclerView() {
        adapter = object : BaseRecyclerViewAdapter<Music, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_song
            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                binding.song = item
                binding.albumImage.background = null
                binding.mv.visibility = if (item.mvId == 0) View.GONE else View.VISIBLE
                binding.mv.setOnClickListener { viewModel.getMv(item) }
                binding.root.setOnClickListener { state.playMusic(position) }
                lifecycleScope.launchWhenCreated {
                    loadAlbumCover(item) { Glide.with(requireContext()).load(it).into(binding.albumImage) }
                }
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observe() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {
        }
        state.musics.observe(viewLifecycleOwner) {
            adapter.data = it
        }
        state.progress.observe(viewLifecycleOwner) {
            SnackbarUtils.with(requireView()).setAction("progress: ${it * 100}%") { }
        }
    }
}