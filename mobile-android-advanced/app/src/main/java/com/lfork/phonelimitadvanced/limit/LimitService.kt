package com.lfork.phonelimitadvanced.limit

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.limit.LimitController.AUTO_UNLOCKED
import com.lfork.phonelimitadvanced.limit.LimitController.FORCE_UNLOCKED
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.util.ToastUtil

/**
 * 单任务服务
 */
class LimitService : Service() {

    var limitTime = 0L

    var listener: LimitStateListener? = null

    private val stateBinder = StateBinder()


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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        limitTime = intent.getLongExtra("limit_time", 0L);
        Log.d("timeTest", "限制时间为${limitTime}分")
        if (LimitController.startLimit(limitTime)) {
            showNotification()
            ToastUtil.showLong(applicationContext, "限制开启成功,请开关一次飞行模式，来使限制生效")
            startAutoUnlock()
            startStateCheck()
            listener?.onLimitStarted()
        } else {
            ToastUtil.showLong(applicationContext, "限制已开启")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return stateBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        LimitController.closeLimit()
        ToastUtil.showLong(applicationContext, "限制已解除，请开关一次飞行模式，来使限制生效")
    }


    private fun startAutoUnlock() {
        Thread(Runnable {
            val unlockType = LimitController.startAutoUnlock()
            if (unlockType == AUTO_UNLOCKED) {
                listener?.autoUnlocked("自动解锁成功")
            } else if (unlockType == FORCE_UNLOCKED) {
                listener?.forceUnlocked("强制解锁成功")
            }
            listener?.onLimitFinished()
        }).start()
    }

    private fun startStateCheck() {
        Thread {
            var remainTime = LimitController.getRemainTimeSeconds()
            Log.d("timeTest", "开始状态刷新,剩余时间${remainTime}秒")
            while (remainTime > 0) {
                listener?.remainTimeRefreshed(remainTime)
                Thread.sleep(999)
                remainTime = LimitController.getRemainTimeSeconds()
            }
        }.start()
    }

    /**
     * 将限制状态同步给MainActivity
     */
    inner class StateBinder internal constructor() : Binder() {
        fun getLimitService(): LimitService {
            return this@LimitService
        }
    }
}
