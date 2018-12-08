package com.lfork.phonelimitadvanced

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lfork.phonelimitadvanced.base.Config
import com.lfork.phonelimitadvanced.base.thread.MyThreadFactory
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.data.common.db.LimitDatabase
import com.lfork.phonelimitadvanced.utils.LinuxShell
import com.lfork.phonelimitadvanced.utils.getAppIcon
import com.lfork.phonelimitadvanced.utils.getAppName
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

        var isHuawei = false

        var isRooted = false

        var isOnLimitation = false

        var haveRemainTime = false

        lateinit var App: LimitApplication


        /**
         * 大于0的话说明正在开启当中，但是还没有完全开启
         */
        var tempInputTimeMinute = -1L

        private var latestLauncherAppInfo: List<String>? = null

        private var whiteNameApps: ArrayList<AppInfo>? = null

        private var appInfoList = ArrayList<AppInfo>()

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
    }

    @Synchronized
    fun addWhiteList(appInfo: AppInfo) {
        whiteNameApps?.add(appInfo)
        //更新缓存，把数据写到文件

        val appNameSet = HashSet<String?>()
        whiteNameApps?.forEach {
            appNameSet.add(it.packageName)
        }

        val editor = getSharedPreferences("white_list", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.putStringSet("white_list", appNameSet.toSet())
        editor.apply()
    }

    @Synchronized
    fun deleteWhiteList(appInfo: AppInfo) {
        whiteNameApps?.remove(appInfo)
        //更新缓存，把数据写到文件

        val appNameSet = HashSet<String?>()
        whiteNameApps?.forEach {
            appNameSet.add(it.packageName)
        }

        val editor = getSharedPreferences("white_list", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.putStringSet("white_list", appNameSet.toSet())
        editor.apply()
    }



    @Synchronized
    fun getWhiteNameAppsInfo(): ArrayList<AppInfo> {
        if (whiteNameApps == null) {
            whiteNameApps = ArrayList()

            val sp = getSharedPreferences("white_list", Context.MODE_PRIVATE)

            val appNameSet = sp.getStringSet("white_list", null)

            appNameSet?.iterator()?.forEach {
                whiteNameApps!!.add(
                    AppInfo(
                        getAppName(this@LimitApplication, it),
                        it,
                        getAppIcon(this@LimitApplication, it)
                    )
                )
            }
        }
        return whiteNameApps!!
    }


    fun getAllAppsInfo(): ArrayList<AppInfo> {

        if (appInfoList.size > 1) {
            return appInfoList
        }

        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.action = Intent.ACTION_MAIN
        val manager = packageManager
        val appResolveList = manager.queryIntentActivities(intent, 0)

        appResolveList?.forEach {
            val appInfo = AppInfo(
                getAppName(this@LimitApplication, it.activityInfo.packageName),
                it.activityInfo.packageName,
                getAppIcon(this@LimitApplication, it.activityInfo.packageName)
            )
            appInfoList.add(appInfo)
        }

        return appInfoList

    }


    fun getLauncherApps(): List<String>? {
        if (latestLauncherAppInfo != null) {
            return latestLauncherAppInfo
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



}