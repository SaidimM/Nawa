package com.saidim.nawa.view.fragments

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseFragment
import com.saidim.nawa.base.ui.pge.BaseRecyclerViewAdapter
import com.saidim.nawa.databinding.FragmentListBinding
import com.saidim.nawa.databinding.ItemSongBinding
import com.saidim.nawa.view.models.items.Item
import com.saidim.nawa.view.models.lists.IList
import com.saidim.nawa.view.viewModels.ListViewModel

class ListFragment(private val listModel: IList) : BaseFragment() {
    override val binding: FragmentListBinding by lazy { FragmentListBinding.inflate(layoutInflater) }
    private val viewModel: ListViewModel by viewModels()

    private val adapter: BaseRecyclerViewAdapter<Item, ItemSongBinding> by lazy {
        object :
            BaseRecyclerViewAdapter<Item, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_song

            override fun onBindItem(binding: ItemSongBinding, item: Item, position: Int) {
                binding.item = item
                Glide.with(requireContext()).load(item.image).into(binding.albumImage)
                binding.root.setOnClickListener { viewModel.onItemClicked(position) }
            }
        }
    }

    override fun initView() {
        activity.setTitle(listModel.title)
        activity.actionBar?.setHomeButtonEnabled(true)
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider)
        drawable?.let { divider.setDrawable(it) }
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        viewModel.list = listModel
        adapter.data = listModel.data
    }
}