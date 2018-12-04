package com.lfork.phonelimitadvanced

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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

        private var launcherAppInfo: List<PackageInfo>? = null

    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BAND:" +android.os.Build.BRAND + "  MANUFACTURER:"+ android.os.Build.MANUFACTURER)
        getLauncherApps()
    }

    fun saveWhiteList(){
    }

    fun loadWhiteList(){
    }

    /**
     * 获取到桌面的应用程序
     */

    private fun getLauncherApps() {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.action = Intent.ACTION_MAIN

        val manager = packageManager
        launcherAppInfo = manager.getInstalledPackages(android.content.pm.PackageManager.GET_ACTIVITIES)
        Log.d(TAG, "Activities" + launcherAppInfo)

    }


}