/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saidim.nawa.base.response.manager

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.Utils

/**
 * Create by KunMinX at 19/10/11
 */
class NetworkStateManager private constructor() : LifecycleEventObserver {
    private val mNetworkStateReceive = NetworkStateReceive()
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                Utils.getApp().applicationContext.registerReceiver(mNetworkStateReceive, filter)
            }
            Lifecycle.Event.ON_PAUSE -> {
                Utils.getApp().applicationContext.unregisterReceiver(mNetworkStateReceive)
            }
            else -> {}
        }
    }

    companion object {
        val instance = NetworkStateManager()
    }
}