package com.lfork.phonelimitadvanced

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import com.lfork.phonelimitadvanced.limit.RootShell
import com.lfork.phonelimitadvanced.utils.LinuxShell
import com.lfork.phonelimitadvanced.utils.getSharedPreferences


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

        var haveRemainTime = false

        lateinit var App: LimitApplication


        /**
         * 大于0的话说明正在开启当中，但是还没有完全开启
         */
        var tempInputTimeMinute = -1L

        private var launcherAppInfo: List<String>? = null

        /**
         * 获取到桌面的应用程序
         */


    }


    override fun onCreate() {
        super.onCreate()

        App = this
        Log.d(
            TAG,
            "BAND:" + android.os.Build.BRAND + "  MANUFACTURER:" + android.os.Build.MANUFACTURER
        )

    }

    fun saveWhiteList() {
    }

    fun loadWhiteList() {
    }

    fun getLauncherApps(): List<String>? {
        if (launcherAppInfo != null) {
            return launcherAppInfo
        }

        if (isRooted) {
            if (haveRemainTime) {
                val tempSet = getSharedPreferences(
                    "LimitStatus",
                    Context.MODE_PRIVATE
                ).getStringSet("launchers", null)
                val tempArray = ArrayList<String>()
                tempSet?.iterator()?.forEach {
                    tempArray.add(it)
                }
                launcherAppInfo = tempArray
            } else {
                val resultStr = StringBuffer()

                val result = LinuxShell.execCommand(
                    " pm list package | grep -E 'home|launcher|miuilite'",
                    true
                )


                resultStr.append(result.successMsg)

                var launchers = result.successMsg.replace("package:", "").split('\n')
                launchers = launchers.subList(0, launchers.size - 1)

                Log.d(
                    TAG,
                    "Activities $launchers"
                )
                launcherAppInfo = launchers

                getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).edit()
                    .putStringSet("launchers", launchers.toSet()).apply()

            }
        }

        return launcherAppInfo;
    }


}