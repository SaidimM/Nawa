package com.saidim.nawa.view.views

import LogUtil
import android.view.View
import com.saidim.nawa.base.utils.AnimationUtils.setListeners
import com.saidim.nawa.base.utils.ViewUtils.dp
import com.saidim.nawa.base.utils.ViewUtils.setHeight
import com.saidim.nawa.base.utils.ViewUtils.setMargins
import com.saidim.nawa.databinding.ActivityMusicBinding
import com.saidim.nawa.view.enums.ControllerState
class MusicActivityControllerDispatcher(
    private val binding: ActivityMusicBinding,
    private val gestureDetector: MusicControllerGestureDetector
) {
    private val TAG = "MusicActivityControllerDispatcher"
    private val startMargin = 8.dp

    init {
        gestureDetector.onStateChangedListener = { changeControllerState(it) }
        gestureDetector.onOffsetChangedListener = { changeControllerOffset(it) }
    }

    fun changeControllerOffset(offset: Float) {
        if (offset < 0) return
        val margin = (startMargin - startMargin * offset).toInt()
        val expendedHeight = binding.root.height
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    fun changeControllerState(state: ControllerState) {
        when (state) {
            ControllerState.SHOWING -> {
                LogUtil.d(TAG, "this state, state: $state")
                binding.cardView.animate().alphaBy(0f).alpha(1f).setDuration(1000)
                    .setListeners(onStart = { binding.cardView.visibility = View.VISIBLE }).start()
//                binding.fragmentList.setMargins(0, 0, 0, 104.dp)
            }

            ControllerState.HIDDEN -> binding.cardView.visibility = View.GONE

            else -> {}
        }
    }
}