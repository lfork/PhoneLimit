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


        /**
         * 用户是否已经完全授权
         */
        var isPermitted = false
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BAND:" +android.os.Build.BRAND + "  MANUFACTURER:"+ android.os.Build.MANUFACTURER)
    }
}