package com.saidim.nawa.view.fragments

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.base.utils.LocalMediaUtils.getAlbumArtBitmap
import com.saidim.nawa.databinding.FragmentListBinding
import com.saidim.nawa.databinding.ItemSongBinding
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.view.models.SelectionTypeModel
import com.saidim.nawa.view.viewModels.ListViewModel

class ListFragment(private val type: SelectionTypeModel) : BaseFragment() {
    override val binding: FragmentListBinding by lazy { FragmentListBinding.inflate(layoutInflater) }
    private val viewModel: ListViewModel by viewModels()

    private val adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<Music, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.layout_item_song

            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                val albumCover = getAlbumArtBitmap(item.path)
                binding.song = item
                Glide.with(requireContext()).load(albumCover).into(binding.albumImage)
            }
        }
    }

    override fun initView() {
        activity.setTitle(type.getTitle())
        activity.actionBar?.setHomeButtonEnabled(true)
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider)
        drawable?.let { divider.setDrawable(it) }
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        adapter.data = viewModel.getMusics()
    }

    override fun observe() {}
}