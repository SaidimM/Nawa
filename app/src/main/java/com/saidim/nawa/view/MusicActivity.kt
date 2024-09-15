package com.saidim.nawa.view

import android.annotation.SuppressLint
import android.os.Bundle
import com.saidim.nawa.R
import com.saidim.nawa.base.ui.pge.BaseActivity
import com.saidim.nawa.databinding.ActivityMusicBinding
import com.saidim.nawa.view.enums.ControllerState
import com.saidim.nawa.view.viewModels.MusicViewModel
import com.saidim.nawa.view.views.MusicActivityControllerDispatcher
import com.saidim.nawa.view.views.MusicControllerGestureDetector

class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val gestureDetector by lazy { MusicControllerGestureDetector(this, viewModel) }
    private val dispatcher by lazy { MusicActivityControllerDispatcher(binding, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        title = getString(R.string.music)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.cardView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
        gestureDetector.onSingleTapListener = { gestureDetector.expandController() }
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun observe() {
        viewModel.music.observe(this) { viewModel.updateControllerState(ControllerState.SHOWING) }
        viewModel.controllerState.observe(this) { dispatcher.changeControllerState(it) }
        viewModel.controllerOffset.observe(this) { dispatcher.changeControllerOffset(it) }
    }

}