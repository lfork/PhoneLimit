package com.lfork.phonelimitadvanced

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import com.lfork.phonelimitadvanced.limit.RootShell
import com.lfork.phonelimitadvanced.utils.LinuxShell


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

        private var launcherAppInfo: List<String>? = null

        /**
         * 获取到桌面的应用程序
         */

        fun getLauncherApps(): List<String>? {

            if (launcherAppInfo != null) {
                return launcherAppInfo
            }

            if (isRooted) {
                val result = LinuxShell.execCommand(
                    " pm list package | grep  '.*launcher'",
                    true
                )
                var launchers = result.successMsg.replace("package:", "").split('\n')
                launchers = launchers.subList(0, launchers.size - 1)

                Log.d(
                    TAG,
                    "Activities $launchers"
                )
                launcherAppInfo = launchers

            }

            return launcherAppInfo;
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(
            TAG,
            "BAND:" + android.os.Build.BRAND + "  MANUFACTURER:" + android.os.Build.MANUFACTURER
        )

    }

    fun saveWhiteList() {
    }

    fun loadWhiteList() {
    }


}