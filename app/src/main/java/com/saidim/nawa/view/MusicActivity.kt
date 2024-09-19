package com.saidim.nawa.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
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
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.createDirectories()
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        title = getString(R.string.music)
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
        viewModel.isPermissionGranted.observe(this) {
            LogUtil.d(TAG, "isPermissionGranted: $it")
            binding.fragmentList.visibility = if (it) View.VISIBLE else View.GONE
            binding.greeting.visibility = if (it) View.GONE else View.VISIBLE
            if (it) viewModel.loadMusic()
        }
    }

    fun grantPermissions(view: View) = initPermission(permissions)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(-1)) viewModel.permissionGranted(false)
        else viewModel.permissionGranted(true)
    }
}