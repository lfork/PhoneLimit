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


    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BAND:" +android.os.Build.BRAND + "  MANUFACTURER:"+ android.os.Build.MANUFACTURER)
    }
}