package com.lfork.phonelimitadvanced

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lfork.phonelimitadvanced.base.Config
import com.lfork.phonelimitadvanced.base.thread.MyThreadFactory
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.common.db.LimitDatabase
import com.lfork.phonelimitadvanced.data.urlinfo.UrlInfoRepository
import com.lfork.phonelimitadvanced.utils.Constants.DEFAULT_WHITE_NAME_LIST
import com.lfork.phonelimitadvanced.utils.LinuxShell
import com.lfork.phonelimitadvanced.utils.getAppIcon
import com.lfork.phonelimitadvanced.utils.getAppName
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 *
 * Created by 98620 on 2018/11/24.
 */
class LimitApplication : Application() {

    companion object {
        val TAG = "LimitApplication"

        var isFloatingWindowMode = false

        var isRooted = false

        var isOnLimitation = false

        var isFirstOpen = false

        lateinit var App: LimitApplication

        private var latestLauncherAppInfo: List<String>? = null


        private var appInfoList = Collections.synchronizedList(ArrayList<AppInfo>());

        /**
         * 获取到桌面的应用程序
         */

        /**
         * 用于异步任务的线程池
         */
        var appFixedThreadPool: ExecutorService? = null
            private set

        fun executeAsyncDataTask(r: () -> Unit) {
            appFixedThreadPool?.execute(r)
        }

    }


    override fun onCreate() {
        super.onCreate()

        App = this
        isFirstOpen = isFirstOpen()
        Log.d(
            TAG,
            "BAND:" + android.os.Build.BRAND + "  MANUFACTURER:" + android.os.Build.MANUFACTURER
        )
        initThreadPool()
        initDataBase()



    }

    private fun initThreadPool() {
        val namedThreadFactory = MyThreadFactory("异步任务线程池")
        appFixedThreadPool = ThreadPoolExecutor(
            Config.BASE_THREAD_POOL_SIZE,
            Config.BASE_THREAD_POOL_SIZE * 2,
            0L,
            TimeUnit.MICROSECONDS,
            LinkedBlockingDeque(),
            namedThreadFactory
        )
    }


    private fun initDataBase() {
        LimitDatabase.initDataBase(this)
        UrlInfoRepository.initUrlData()
    }


    fun getOrInitAllAppsInfo(): MutableList<AppInfo>? {

        if (appInfoList.size > 1) {
            return appInfoList
        }

        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.action = Intent.ACTION_MAIN
        val manager = packageManager
        val appResolveList = manager.queryIntentActivities(intent, 0)

        if (isFirstOpen) {

            appResolveList?.forEach {
                val appInfo = AppInfo(
                    getAppName(this@LimitApplication, it.activityInfo.packageName),
                    it.activityInfo.packageName,
                    getAppIcon(this@LimitApplication, it.activityInfo.packageName)
                )

                val pkgName = appInfo.packageName

                if (pkgName.substring(pkgName.lastIndexOf('.') + 1) in DEFAULT_WHITE_NAME_LIST) {
                    appInfo.isInWhiteNameList = true
                }
                appInfoList.add(appInfo)
            }
        } else {
            appResolveList?.forEach {
                val appInfo = AppInfo(
                    getAppName(this@LimitApplication, it.activityInfo.packageName),
                    it.activityInfo.packageName,
                    getAppIcon(this@LimitApplication, it.activityInfo.packageName)
                )

                appInfoList.add(appInfo)
            }
        }



        return appInfoList

    }


    fun getLauncherApps(): List<String>? {
        if (latestLauncherAppInfo != null) {
            return latestLauncherAppInfo
        }

        if (isRooted) {

            val tempSet = getSharedPreferences(
                "LimitStatus",
                Context.MODE_PRIVATE
            ).getStringSet("launchers", null)


            if (tempSet != null) {
                val tempArray = ArrayList<String>()
                tempSet.iterator().forEach {
                    tempArray.add(it)
                }
                Log.d(
                    TAG,
                    "Activities $tempArray"
                )

                latestLauncherAppInfo = tempArray

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
                latestLauncherAppInfo = launchers

                getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).edit()
                    .putStringSet("launchers", launchers.toSet()).apply()

            }

        }

        return latestLauncherAppInfo;
    }

    private fun isFirstOpen(): Boolean {
        val setting = getSharedPreferences("env", Context.MODE_PRIVATE)
        val isFirst = setting.getBoolean("FIRST", true)
        return if (isFirst) {
            setting.edit().putBoolean("FIRST", false).apply()
            true
        } else {
            false
        }
    }


}