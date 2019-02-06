package com.lfork.phonelimitadvanced.limit

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/06 17:00
 *
 * 通过限制桌面入口的方式来实现限制功能
 */
class LauncherLimitTask:LimitTask {

    var mContext :Context? = null

    override fun initLimit(context: Context) {
        if (LimitApplication.isRooted) {
            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }

        mContext = context
    }

    override fun doLimit() {

        if (mContext == null){
            return
        }

        //获取栈顶app的包名
        val packageName = getLauncherTopApp(
            mContext!!,
            mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        )

        //判断包名打开解锁页面
        if (!TextUtils.isEmpty(packageName)) {
            if (AppInfoRepository.whiteNameList.contains(packageName)) {
               return
            }
        } else{
            return
        }
        try {
            Thread.sleep(300)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


        if (LimitApplication.isFloatingWindowMode) {

        } else {

            if (packageName == "com.android.settings"){
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext!!.startActivity(intent)
            }

            val intent = Intent(mContext!!, MainActivity::class.java)
            intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
            intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
        }
    }


    override fun closeLimit() {
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }
        }

        if (!LimitApplication.isFloatingWindowMode) {
            mContext!!.clearDefaultLauncher()
        }

        mContext = null
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