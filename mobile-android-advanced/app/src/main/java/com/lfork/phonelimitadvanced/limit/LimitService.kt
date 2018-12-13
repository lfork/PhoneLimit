package com.lfork.phonelimitadvanced.limit

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isFloatingWindowMode
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.limit.TimeController.Companion.AUTO_EXIT
import com.lfork.phonelimitadvanced.limit.TimeController.Companion.FORCE_EXIT
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher

class LimitService : Service() {

    var listener: LimitStateListener? = null

    private val stateBinder = LimitBinder()

    private var threadIsGoodRunning = true //服务是否在运行

    var serviceIsDestroyed = false

    var autoUnlockThread: Thread? = null

    var timeListenerThread: Thread? = null

    var limitExecutorThread: Thread? = null

    var timeController: TimeController? = null

    private var activityManager: ActivityManager? = null


    override fun onCreate() {
        super.onCreate()
        Log.d("异常重启测试2", " ${this} ${LimitApplication.isOnLimitation}")
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        timeController = TimeController()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("异常重启测试3", " ${this} ${LimitApplication.isOnLimitation}")
        val limitTimeSeconds = intent!!.getLongExtra("limit_time", 0L);
        if (timeController?.initTimer(limitTimeSeconds)!!) {
            beforeLimitation()
            showNotification()
            startLimitationListener()
            startTimeListener()
            startAutoUnlock()
            listener?.onLimitStarted()
        } else {
            listener?.onLimitStarted()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return stateBinder
    }

    /**
     * 将限制状态同步给MainActivity
     */
    inner class LimitBinder internal constructor() : Binder() {

        fun setLimitStateListener(limitStateListener: LimitStateListener) {
            listener = limitStateListener
        }

        fun forceEndLimitation() {
            forceEnd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadIsGoodRunning = false
        listener = null
        activityManager = null
        forceEnd()
        timeController = null
        serviceIsDestroyed = true
    }

    fun forceEnd() {
        timeController?.forceEndTimer()
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
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification1)
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
        autoUnlockThread = Thread(Runnable {
            //开启完成
            LimitApplication.isOnLimitation = true
            //计时器结束后会有返回值
            val exitType = timeController!!.startTimer()

            if (threadIsGoodRunning) {
                releaseLimitation()
                LimitApplication.isOnLimitation = false
                //TODO 【DEBUG】这行代码是没有执行的
                threadIsGoodRunning = false
                if (exitType == AUTO_EXIT) {
                    listener?.onUnlocked("自动解锁成功")
                } else if (exitType == FORCE_EXIT) {
                    listener?.onUnlocked("强制解锁成功")
                }
                listener?.onLimitFinished()
            }
        })

        autoUnlockThread?.start()
    }


    /**
     * 一个粗略的时间监听 不能作为限制结束的标识
     */
    //TODO  【DEBUG】这个线程被挂起了
    private fun startTimeListener() {
        timeListenerThread = Thread {
            var   remainTime = timeController?.getRemainTimeSeconds() ?: 0
            Log.d("timeTest", "开始状态刷新,剩余时间${remainTime}秒")
            while (remainTime > 0 && !serviceIsDestroyed) {
                Log.d("timeTest", "开始状态刷新,剩余时间${remainTime}秒")
                listener?.remainTimeRefreshed(remainTime)
                Thread.sleep(999)
                remainTime = timeController?.getRemainTimeSeconds() ?: 0

            }
        }

        timeListenerThread?.start()
    }

    //TODO  【DEBUG】这个线程还在运行
    private fun startLimitationListener() {
        limitExecutorThread = Thread {
            while (threadIsGoodRunning && !serviceIsDestroyed) {
                //获取栈顶app的包名
                val packageName = getLauncherTopApp(this@LimitService, activityManager!!)

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
        }

        limitExecutorThread?.start()
    }


    /**
     * 如果时间不动 -> 那么就重启程序
     */
    private fun deadDetect() {

    }

    /**
     * 这个主要是给root用户使用的
     */
    private fun beforeLimitation() {
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            Log.d("上锁测试", launchers.toString());
            launchers?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }
    }

    /**
     * 执行限制操作
     */
    private fun doLimit(packageName: String) {

        if (isFloatingWindowMode) {

        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
            intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    //已经到时间了，现在需要做一些真正的解除限制的操作
    /**
     * 结束限制：时间到了，然后可以选桌面了
     */
    private fun releaseLimitation() {
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            Log.d("解锁测试", launchers.toString());
            launchers?.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }
        }

        if (!LimitApplication.isFloatingWindowMode) {
            clearDefaultLauncher()
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
