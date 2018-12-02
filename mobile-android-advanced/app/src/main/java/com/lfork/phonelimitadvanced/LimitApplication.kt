package com.lfork.phonelimitadvanced

import android.app.Application
import android.util.Log

/**
 *
 * Created by 98620 on 2018/11/24.
 */
class LimitApplication : Application() {

    companion object {
        val TAG = "LimitApplication"

        var isHuawei = false

        var isRooted = false

        var isOnLimitation = false

        /**
         * 大于0的话说明正在开启当中，但是还没有完全开启
         */
        var tempInputTimeMinute = -1L

    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BAND:" +android.os.Build.BRAND + "  MANUFACTURER:"+ android.os.Build.MANUFACTURER)
    }

    fun saveWhiteList(){
    }

    fun loadWhiteList(){
    }


}