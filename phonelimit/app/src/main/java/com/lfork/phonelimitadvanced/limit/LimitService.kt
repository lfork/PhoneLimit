package com.lfork.phonelimitadvanced.limit

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.MainActivity

/**
 * 模拟CS模式,Activity作为客户端，Service作为服务端
 * activity 需要在服务结束(通过binder来通知activity)后关闭服务。
 */
class LimitService : Service() {

    private var listener: StateListener? = null

    private val stateBinder = LimitBinder()

    private lateinit var timer: Timer

    private lateinit var executor: Executor

    private var notification:Notification?=null

    private val timerListener = object : Timer.TimeListener {

        override fun onClosedInAdvance(remainTimeSeconds: Long) {
            executor.close()
            listener?.onLimitFinished()
        }


        override fun onCompleted() {
            //计时器结束时前需要先关闭限制服务，再通知用户
            executor.close()
            LimitApplication.isOnLimitation = false
            listener?.onLimitFinished()
            saveRemainTime(0)
            clearStartTime()
        }

        override fun onRemainTimeRefreshed(remainTimeSeconds: Long) {
            listener?.remainTimeRefreshed(remainTimeSeconds)
            saveRemainTime(remainTimeSeconds)

        }
    }

    private fun saveRemainTime(remainTimeSeconds: Long) {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putLong("remain_time_seconds", remainTimeSeconds)
        editor.apply()
    }

    private fun saveStartTime(startTime: Long) {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)

        val editor = sp.edit()
        editor.putLong("start_time", startTime)


        editor.apply()
    }

    private fun clearStartTime() {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)

        val editor = sp.edit()
        editor.remove("start_time")
        editor.apply()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //如果限制已开启，那么直接返回
        if (LimitApplication.isOnLimitation) {
            listener?.onLimitStarted()
            return super.onStartCommand(intent, flags, startId)
        }

        val limitTimeSeconds = intent!!.getLongExtra("limit_time", 0L);
        executor = Executor(this)
        val startTime = intent.getLongExtra("start_time", System.currentTimeMillis());
        timer = Timer(limitTimeSeconds, timerListener, startTime)

        //计时器开启前需要先开启限制服务
        //需要先开executor ，因为如果时间很短，然后先开的timer，可能会导致在executor开启之前时间就结束了，然后等下
        //就会执行executor，此时就没有人能关闭executor了
        if (executor.start() && timer.start()) {
            LimitApplication.isOnLimitation = true
        }
        //通知用户 显示通知(开启前台服务)
        showNotification()
        listener?.onLimitStarted()
        saveStartTime(startTime)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return stateBinder
    }


    /**
     * 关闭服务后通知自动就关闭了，所以这里就不需要再写关闭通知的函数了
     */
    private fun showNotification() {
        //使用前台服务 防止被系统回收 状态栏会显示一个通知

        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.lfork.phonelimit"
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
            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification1)
                    .setContentTitle("限制已开启")
                    .setContentText("专心搞事情吧，不要玩儿手机了")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pi)
                    .build()
            startForeground(2, notification)
        } else {

            notification = NotificationCompat.Builder(this)
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

    /**
     * 将限制状态同步给Activity
     */
    inner class LimitBinder internal constructor() : Binder() {
        fun setLimitStateListener(limitStateListener: StateListener) {
            listener = limitStateListener
        }
    }

    interface StateListener {

        fun remainTimeRefreshed(timeSeconds: Long)

        fun onLimitStarted()

        fun onLimitFinished()
    }
}
