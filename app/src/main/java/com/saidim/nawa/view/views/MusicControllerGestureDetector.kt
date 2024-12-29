package com.saidim.nawa.view.views

import LogUtil
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ScreenUtils
import com.saidim.nawa.Constants.bezierInterpolator
import com.saidim.nawa.base.utils.ViewUtils.dp
import com.saidim.nawa.view.enums.ControllerState
import com.saidim.nawa.view.viewModels.MusicViewModel

@SuppressLint("ClickableViewAccessibility")
class MusicControllerGestureDetector(context: Context) : SimpleOnGestureListener() {
    private val TAG = "onTouchEvent"
    private var controllerAnimator = ValueAnimator()
    private val collapsedHeight = 88.dp
    private val deltaHeight = ScreenUtils.getScreenHeight() - collapsedHeight
    private val gestureDetector = GestureDetector(context, this)
    private var controllerState: ControllerState = ControllerState.HIDDEN
    private var controllerOffset: Float = 0f

    private var onSingleTapListener: (() -> Boolean) = {
        when (controllerState) {
            ControllerState.HIDDEN -> expandController()
            ControllerState.SHOWING -> false
            ControllerState.COLLAPSING -> false
            ControllerState.COLLAPSED -> expandController()
            ControllerState.EXPENDING -> false
            ControllerState.EXPENDED -> false
        }
    }
    var onStateChangedListener: (ControllerState) -> Unit = {}
    var onOffsetChangedListener: (Float) -> Unit = {}

    fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val isEventConsumed =  gestureDetector.onTouchEvent(event)
        val isActionUp = event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_OUTSIDE
        val isScrolling = controllerState == ControllerState.EXPENDING || controllerState == ControllerState.COLLAPSING
        return if (!isEventConsumed && isScrolling && isActionUp) onActionUp()
        else isEventConsumed
    }

    private fun updateOffset(offset: Float) {
        if (offset < 0 || offset > 1) return
        onOffsetChangedListener(offset)
    }

    private fun updateState(state: ControllerState) {
        if (state == controllerState) return
        onStateChangedListener(state)
    }

    override fun onSingleTapUp(e: MotionEvent) = onSingleTapListener.invoke()

    override fun onDown(e: MotionEvent): Boolean {
        controllerAnimator.cancel()
        LogUtil.d(TAG, "onDown, rawY: ${e.rawY}")
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (controllerState == ControllerState.EXPENDED) updateState(ControllerState.COLLAPSING)
        else if (controllerState == ControllerState.COLLAPSED) updateState(ControllerState.EXPENDING)
        val deltaOffset = (e1!!.y - e2.y) / deltaHeight
        LogUtil.i(TAG, "deltaHeight: $deltaHeight")
        LogUtil.i(TAG, "e1.y: ${e1.y}, e2.y: ${e2.y}, deltaOffset: $deltaOffset")
        updateOffset(controllerOffset + deltaOffset)
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        LogUtil.i(TAG, "onFling, e1: ${e1!!.y}, e2: ${e2.y}, velocityY: $velocityY")
        val isScrollUp = if (e2.y - e1.y < 0) true else false
        return if (isScrollUp) expandController()
        else collapseController()
    }

    private fun onActionUp(): Boolean {
        if (controllerState == ControllerState.EXPENDING) expandController()
        else if (controllerState == ControllerState.COLLAPSING) collapseController()
        return false
    }

    fun collapseController(): Boolean {
        if (controllerState == ControllerState.COLLAPSED) return false
        controllerAnimator.cancel()
        controllerAnimator = ObjectAnimator.ofFloat(controllerOffset, 0f).apply {
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { updateOffset(this.animatedValue as Float) }
            doOnEnd { updateState(ControllerState.COLLAPSED) }
            start()
        }
        return true
    }

    fun expandController(): Boolean {
        if (controllerState == ControllerState.EXPENDED) return false
        controllerAnimator.cancel()
        updateState(ControllerState.EXPENDING)
        controllerAnimator = ObjectAnimator.ofFloat(controllerOffset, 1f).apply {
            duration = 500
            interpolator = bezierInterpolator
            addUpdateListener { updateOffset(this.animatedValue as Float) }
            doOnEnd { updateState(ControllerState.EXPENDED) }
            start()
        }
        return true
    }
}