package com.saidim.nawa.view

import LogUtil
import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.saidim.nawa.Constants
import com.saidim.nawa.base.ui.pge.BaseActivity
import com.saidim.nawa.databinding.ActivityMusicBinding
import com.saidim.nawa.view.enums.ControllerState
import com.saidim.nawa.view.fragments.ListFragment
import com.saidim.nawa.view.fragments.MainFragment
import com.saidim.nawa.view.viewModels.MusicViewModel
import com.saidim.nawa.view.views.MusicActivityControllerDispatcher
import com.saidim.nawa.view.views.MusicControllerGestureDetector

class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val gestureDetector by lazy { MusicControllerGestureDetector(this, viewModel) }
    private val dispatcher by lazy { MusicActivityControllerDispatcher(binding, viewModel) }
    private val permissions =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val mainFragment by lazy { MainFragment() }
    private val listFargemnt by lazy { ListFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    private fun initData() {
        viewModel.createDirectories()
        viewModel.getMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.cardView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
        binding.fragmentList.visibility = if (viewModel.isPermissionGranted.value == true) View.VISIBLE else View.GONE
        binding.greeting.visibility = if (viewModel.isPermissionGranted.value == true) View.GONE else View.VISIBLE
        gestureDetector.onSingleTapListener = { gestureDetector.expandController() }
    }

    private fun checkPermissions() {
        if (!isPermissionsGranted(permissions)) initPermission(permissions) else viewModel.permissionGranted(true)
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun observe() {
        viewModel.music.observe(this) { viewModel.updateControllerState(ControllerState.SHOWING) }
        viewModel.controllerState.observe(this) { dispatcher.changeControllerState(it) }
        viewModel.controllerOffset.observe(this) { dispatcher.changeControllerOffset(it) }
        viewModel.fragment.observe(this) { navigateFragment(it) }
        viewModel.isPermissionGranted.observe(this) {
            LogUtil.d(TAG, "isPermissionGranted: $it")
            initData()
            initView()
        }
    }

    private fun navigateFragment(name: Int) {
        val fragment = when (name) {
            Constants.MAIN_FRAGMENT -> mainFragment
            Constants.MUSIC_LIST_FRAGMENT -> listFargemnt
            else -> mainFragment
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragment.id, fragment)
            .apply { addToBackStack(null) }
            .commit()
    }

    fun grantPermissions(view: View) = initPermission(permissions)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(-1)) viewModel.permissionGranted(false)
        else viewModel.permissionGranted(true)
    }
}