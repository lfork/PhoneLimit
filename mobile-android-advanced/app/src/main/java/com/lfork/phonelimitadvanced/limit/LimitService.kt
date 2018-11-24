package com.lfork.phonelimitadvanced.limit

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.limit.LimitTimeController.AUTO_UNLOCKED
import com.lfork.phonelimitadvanced.limit.LimitTimeController.FORCE_UNLOCKED
import com.lfork.phonelimitadvanced.main.MainActivity
import java.util.*

class LimitService : Service() {

    var listener: LimitStateListener? = null

    private val stateBinder = StateBinder()

    var threadIsTerminate = false //是否开启循环

    private var activityManager: ActivityManager? = null

    override fun onCreate() {
        super.onCreate()
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        threadIsTerminate = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val limitTimeSeconds = intent!!.getLongExtra("limit_time", 0L);
        if (LimitTimeController.startLimit(limitTimeSeconds)) {
            showNotification()
            startAutoUnlock()
            startStateCheck()
            listener?.onLimitStarted()
        } else {
            listener?.onLimitStarted()
        }

        checkData()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return stateBinder
    }

    /**
     * 将限制状态同步给MainActivity
     */
    inner class StateBinder internal constructor() : Binder() {
        fun getLimitService(): LimitService {
            return this@LimitService
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadIsTerminate = false
        LimitTimeController.closeLimit()
        listener?.onLimitFinished()
        listener = null
    }

    /**
     * 关闭服务后通知自动就关闭了，所以这里就不需要再写关闭通知的函数了
     */
    private fun showNotification() {
        //使用前台服务 防止被系统回收 状态栏会显示一个通知

        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
            val channelName = "Phone Limit"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("限制已开启")
                    .setContentText("专心搞事情吧，不要玩儿手机了")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pi)
                    .build()
            startForeground(2, notification)
        } else {

            val notification = NotificationCompat.Builder(this)
                    .setContentTitle("限制已开启")
                    .setContentText("专心搞事情吧，不要玩儿手机了")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .build()
            startForeground(1, notification)
        }
    }


    private fun startAutoUnlock() {
        Thread(Runnable {
            val unlockType = LimitTimeController.startAutoUnlock()
            if (unlockType == AUTO_UNLOCKED) {
                listener?.autoUnlocked("自动解锁成功")
            } else if (unlockType == FORCE_UNLOCKED) {
                listener?.forceUnlocked("强制解锁成功")
            }

            threadIsTerminate = false
            listener?.onLimitFinished()
        }).start()
    }

    private fun startStateCheck() {
        Thread {
            var remainTime = LimitTimeController.getRemainTimeSeconds()
            Log.d("timeTest", "开始状态刷新,剩余时间${remainTime}秒")
            while (remainTime > 0) {
                listener?.remainTimeRefreshed(remainTime)
                Thread.sleep(999)
                remainTime = LimitTimeController.getRemainTimeSeconds()
            }
        }.start()
    }

    private fun checkData() {
        Thread {
            while (threadIsTerminate) {
                //获取栈顶app的包名
                val packageName = getLauncherTopApp(this@LimitService, activityManager!!)

                //判断包名打开解锁页面
                if (!TextUtils.isEmpty(packageName)) {
                    if (!inWhiteList(packageName)) {
                        passwordLock(packageName)
                    }
                }
                try {
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 白名单
     */
    private fun inWhiteList(packageName: String): Boolean {
        return (packageName == AppConstants.APP_PACKAGE_NAME
                || packageName == "net.oneplus.launcher")
    }

    private fun getLauncherTopApp(context: Context, activityManager: ActivityManager): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val appTasks = activityManager.getRunningTasks(1)
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks[0].topActivity.packageName
            }
        } else {
            //5.0以后需要用这方法
            val sUsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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

    /**
     * 获得属于桌面的应用的应用包名称
     */
    private fun getHomes(): List<String> {
        val names = ArrayList<String>()
        val packageManager = this.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (ri in resolveInfo) {
            names.add(ri.activityInfo.packageName)
        }
        return names
    }

    /**
     * 转到解锁界面
     */
    private fun passwordLock(packageName: String) {
        //如果是华为的话，这里就需要显示悬浮窗
        //否则就进行跳转

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    companion object {

    }
}
