package com.saidim.nawa

import LogUtil
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.base.BaseApplication
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService


class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG, "onCreate")
        Utils.init(this)
        // SQLiteStudio Service
        SQLiteStudioService.instance().setPort(1998)
        SQLiteStudioService.instance().start(this)
    }

    companion object {
        private const val TAG = "Application"

        fun getString(id: Int) = try {
            Utils.getApp().getString(id)
        } catch (exception: Exception) {
            LogUtil.e(exception.message.toString())
            ""
        }
    }
}