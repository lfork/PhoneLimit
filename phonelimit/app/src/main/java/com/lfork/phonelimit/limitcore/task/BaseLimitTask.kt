package com.lfork.phonelimit.limitcore.task

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimit.base.AppConstants
import com.lfork.phonelimit.data.appinfo.AppInfoRepository
import com.lfork.phonelimit.limitcore.LimitTask
import com.lfork.phonelimit.view.main.MainActivity
import com.lfork.phonelimit.utils.Constants.SPECIAL_WHITE_NAME_LIST

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:34
 *
 * 只做跳转不做桌面限制
 */

open class BaseLimitTask : LimitTask {

    var mContext: Context? = null

    override fun initLimit(context: Context) {

        mContext = context
    }

    /**
     * 限制开启后，第一次[doLimit]需要立马启动到[MainActivity]，true表示已经启动成功。
     */
    protected var started = false

    override fun doLimit() :Boolean{
        if (!started){
            val intent = Intent(mContext!!, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
            started = true
            return true
        }

        if (mContext == null) {
            return false
        }

        //获取栈顶app的包名
        val packageName = getTopRunningApp(
                mContext!!,
                mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        )

//        Log.d("doLimit", "栈顶app的包名$packageName")

        //判断包名打开解锁页面
        if (TextUtils.isEmpty(packageName)) {
            return false
        }

        if (AppInfoRepository.whiteNameList.contains(packageName)) {
            return false
        }
        try {
            Thread.sleep(300)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


        if (packageName == "com.android.settings") {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
        }

        if (AppInfoRepository.whiteNameList.contains(packageName) || SPECIAL_WHITE_NAME_LIST.contains(packageName)) {
            return false
        }
        Log.d("doLimit", "执行限制")
        val intent = Intent(mContext!!, MainActivity::class.java)
        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext!!.startActivity(intent)
        return true
    }


    override fun closeLimit() {
        mContext = null
    }

   fun getTopRunningApp(context: Context, activityManager: ActivityManager): String {
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
            val beginTime = endTime - 500
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