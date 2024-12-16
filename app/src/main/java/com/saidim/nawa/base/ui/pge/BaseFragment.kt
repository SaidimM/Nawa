package com.saidim.nawa.base.ui.pge

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saidim.nawa.base.BaseApplication
import com.saidim.nawa.base.bindings.DataBindingFragment

abstract class BaseFragment : DataBindingFragment() {
    protected val TAG = this.javaClass.simpleName
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null
    protected fun <T : ViewModel> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!!.get(modelClass)
    }

    protected fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(activity)
        }
        return mActivityProvider!!.get(modelClass)
    }

    protected fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider = getApplicationFactory(activity)?.let {
                ViewModelProvider(
                    activity.applicationContext as BaseApplication, it
                )
            }
        }
        return mApplicationProvider!![modelClass]
    }

    open fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory? {
        checkActivity(this)
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    open fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    open fun checkActivity(fragment: Fragment) {
        val activity = fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        observe()
    }

    open fun initView() {}

    open fun initData() {}

    open fun observe() {}
}