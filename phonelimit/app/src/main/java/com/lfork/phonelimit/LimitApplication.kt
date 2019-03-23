package com.lfork.phonelimit

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.base.Config
import com.lfork.phonelimit.base.thread.MyThreadFactory
import com.lfork.phonelimit.data.*
import com.lfork.phonelimit.data.appinfo.AppInfo
import com.lfork.phonelimit.data.urlinfo.UrlInfoRepository
import com.lfork.phonelimit.data.taskconfig.TaskConfig
import com.lfork.phonelimit.utils.Constants.DEFAULT_WHITE_NAME_LIST
import com.lfork.phonelimit.utils.LinuxShell
import com.lfork.phonelimit.utils.getAppIcon
import com.lfork.phonelimit.utils.getAppName
import com.simple.spiderman.SpiderMan
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 *
 * Created by 98620 on 2018/11/24.
 */
class LimitApplication : Application() {


    /**
     * 全局变量
     */
    companion object {
        val TAG = "LimitApplication"


        var isRooted = false
            get() {
                return App.getRootStatus()
            }
            set(value) {
                field = value
                App.saveRootStatus(field)
            }

        var isOnLimitation = false
        set(value) {
            field = value
            if (field){
                limitSwitchListeners.forEach {
                    it.onStartLimit()
                }
            } else{
                limitSwitchListeners.forEach {
                    it.onCloseLimit()
                }
            }

        }

        var isTimedTaskRunning = false


        private val limitSwitchListeners = ArrayList<LimitSwitchListener>()

        fun registerSwitchListener(limitSwitchListener: LimitSwitchListener){
            limitSwitchListeners.add(limitSwitchListener)
        }

        fun unregisterSwitchListener(limitSwitchListener: LimitSwitchListener){
            limitSwitchListeners.remove(limitSwitchListener)
        }


        var isFirstOpen = false
        lateinit var App: LimitApplication

        var defaultLimitModel = TaskConfig.LIMIT_MODEL_LIGHT
            get() {
//                if (::App.isInitialized) {
                field = App.getDefaultLimitModel()
//                }


                return field
            }
            set(value) {
//                if (::App.isInitialized) {
                App.saveDefaultLimitModel(value)
//                }
                field = value
            }


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

        var mHandler = Handler(Looper.getMainLooper()) {
            Log.d("mHandler", "ee")
            false
        }
    }




    /**
     *
     */
    interface LimitSwitchListener {
        fun onStartLimit()

        fun onCloseLimit()
    }

    override fun onCreate() {
        super.onCreate()
        App = this
        SpiderMan.init(this).setTheme(R.style.SpiderManTheme_Dark)
        initConfig()
        isFirstOpen = isFirstOpen()
        Log.d(
            TAG,
            "BAND:" + android.os.Build.BRAND + "  MANUFACTURER:" + android.os.Build.MANUFACTURER
        )
        initThreadPool()
        initDataBase()
        MainHandler.getInstance()
        ToastUtils.init(this)
    }


    private fun initConfig() {
        defaultLimitModel = getDefaultLimitModel()
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
            val tempData = getSpLauncherApps()

            if (tempData != null) {
                latestLauncherAppInfo = tempData
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
                saveLauncherApps(launchers)
            }

        }

        return latestLauncherAppInfo;
    }


}