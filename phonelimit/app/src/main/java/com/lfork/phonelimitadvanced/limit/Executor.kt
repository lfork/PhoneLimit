package com.lfork.phonelimitadvanced.limit

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncherFake

/**
 *
 * Created by 98620 on 2018/12/14.
 */
class Executor(var context: Context?) {

    lateinit var executorThread: Thread

    var isActive = false

    /**
     * 只能调用一次开始
     */
    fun start():Boolean {
        if (isActive || context == null) {
            return false
        }
        val executorTask = Runnable {
            isActive = true
            beforeLimitation()
            while (isActive) {
                //获取栈顶app的包名
                val packageName = getLauncherTopApp(
                    context!!,
                    context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                )

                //判断包名打开解锁页面
                if (!TextUtils.isEmpty(packageName)) {
                    if (!inWhiteList(packageName)) {
                        doLimit(packageName)
                    }
                }
                try {
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            releaseLimitation()
            onDestroy()
        }
        executorThread = Thread(executorTask)
        executorThread.name = "限制监督与执行线程"
        executorThread.start()

        return true
    }

    /**
     * 关闭Executor
     */
    fun close() {
        isActive = false

        //尽快结束线程
        executorThread.interrupt()
    }

    /**
     * 进行最后的资源释放
     */
    fun onDestroy(){
        context = null
    }

    /**
     * 这个主要是给root用户使用的
     */
    private fun beforeLimitation() {
        if (LimitApplication.isRooted) {
            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }
    }


    /**
     * 执行限制操作
     */
    private fun doLimit(packageName: String) {

        if (LimitApplication.isFloatingWindowMode) {

        } else {

            if (packageName == "com.android.settings"){
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context!!.startActivity(intent)
            }

            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
            intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
        }
    }

    /**
     * 结束限制：时间到了，然后可以选桌面了。
     * 因为Android的运行机制，结束限制需要服务端(Service)
     * 和客户端(Activity)先后调用
     * @see clearDefaultLauncher,
     * @see clearDefaultLauncherFake
     */
    private fun releaseLimitation() {
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }
        }

        if (!LimitApplication.isFloatingWindowMode) {
            context!!.clearDefaultLauncher()
        }
    }

    /**
     * 白名单
     */
    private fun inWhiteList(packageName: String): Boolean {
        return AppInfoRepository.whiteNameList.contains(packageName)
    }

    private fun getLauncherTopApp(context: Context, activityManager: ActivityManager): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val appTasks = activityManager.getRunningTasks(1)
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks[0].topActivity.packageName
            }
        } else {
            //5.0以后需要用这方法
            val sUsageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result
            }
        }
        return ""
    }
}