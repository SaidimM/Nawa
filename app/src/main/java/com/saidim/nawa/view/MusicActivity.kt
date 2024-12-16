package com.saidim.nawa.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils
import com.saidim.nawa.base.ui.pge.BaseActivity
import com.saidim.nawa.databinding.ActivityMusicBinding
import com.saidim.nawa.view.viewModels.MusicViewModel
import com.saidim.nawa.view.views.MusicActivityControllerDispatcher
import com.saidim.nawa.view.views.MusicControllerGestureDetector

class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by viewModels()
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val gestureDetector by lazy { MusicControllerGestureDetector(this, viewModel) }
    private val dispatcher by lazy { MusicActivityControllerDispatcher(binding, viewModel) }
    private val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
        observe()
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.cardView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
        gestureDetector.onSingleTapListener = { gestureDetector.expandController() }
        viewModel.fragmentCallback = { navigateFragment(it) }
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    fun observe() {
        viewModel.controllerState.observe(this) { dispatcher.changeControllerState(it) }
        viewModel.controllerOffset.observe(this) { dispatcher.changeControllerOffset(it) }
    }

    private fun navigateFragment(fragment: Fragment) {
        LogUtils.d("navigateFragment, " + fragment.javaClass.simpleName)
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentList.id, fragment)
            .apply { addToBackStack(null) }
            .commit()
    }

    fun grantPermissions(view: View) = initPermission(permissions)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}