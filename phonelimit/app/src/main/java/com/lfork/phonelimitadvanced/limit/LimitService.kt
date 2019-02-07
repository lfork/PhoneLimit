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
import com.lfork.phonelimitadvanced.limit.task.LauncherLimitTask
import com.lfork.phonelimitadvanced.limit.task.TimedLimitTask
import com.lfork.phonelimitadvanced.main.MainActivity

/**
 * 模拟CS模式,Activity作为客户端，Service作为服务端
 * activity 需要在服务结束(通过binder来通知activity)后关闭服务。
 */
class LimitService : Service() {

    private var listener: StateListener? = null

    private val stateBinder = LimitBinder()

    private lateinit var limitTimer: LimitTimer

    private lateinit var limitTaskExecutor: LimitExecutor

    private lateinit var timedTaskExecutor: LimitExecutor

    private var notification:Notification?=null

    private val timerListener = object : LimitTimer.TimeListener {

        override fun onClosedInAdvance(remainTimeSeconds: Long) {
            limitTaskExecutor.close()
            listener?.onLimitFinished()
        }

        override fun onCompleted() {
            //计时器结束时前需要先关闭限制服务，再通知用户
            limitTaskExecutor.close()
            LimitApplication.isOnLimitation = false
            listener?.onLimitFinished()
            saveRemainTime(0)
            clearStartTime()
        }

        override fun onRemainTimeRefreshed(remainTimeSeconds: Long) {
            listener?.updateRemainTime(remainTimeSeconds)
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

    override fun onCreate() {
        super.onCreate()
        //判断是否有未执行完的任务->继续执行限制任务  把判断剩余限制任务的逻辑放到服务里面
        checkAndRecoveryLimitTask()

        //判断是否有定时服务->开启定时服务的监听

        //通知用户 显示通知(开启前台服务)
        showNotification()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val command = intent?.getIntExtra("limit_command", -1)
        if (command == COMMAND_START_LIMIT){
            startLimitTask(intent)
        } else if (command == COMMAND_START_TIMED_TASK){
            startTimedTask()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    @Synchronized
    private fun startLimitTask(intent: Intent?,_limitTimeSeconds:Long=0) {
        //如果限制已开启，那么直接返回
        if (LimitApplication.isOnLimitation) {
            listener?.onLimitStarted()
            return
        }

        val limitTimeSeconds:Long

        limitTimeSeconds = if (_limitTimeSeconds > 0){
            _limitTimeSeconds
        } else{
            intent!!.getLongExtra("limit_time", 0L);
        }
        limitTaskExecutor = LimitExecutor(this, LauncherLimitTask())
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val startTime =  sp.getLong("start_time", System.currentTimeMillis())
        limitTimer = LimitTimer(limitTimeSeconds, timerListener, startTime)

        //计时器开启前需要先开启限制服务
        //需要先开 limitTaskExecutor ，因为如果时间很短，然后先开的 limitTimer，可能会导致在 limitTaskExecutor 开启之前时间就结束了，然后等下
        //就会执行 limitTaskExecutor，此时就没有人能关闭 limitTaskExecutor 了
        if (limitTaskExecutor.start() && limitTimer.start()) {
            LimitApplication.isOnLimitation = true
        }

        listener?.onLimitStarted()
        saveStartTime(startTime)
    }

    private fun startTimedTask(){
        if (LimitApplication.isDoingTimedTask){
            return
        }
        LimitApplication.isDoingTimedTask = true
        timedTaskExecutor = LimitExecutor(this,TimedLimitTask())
    }

    private fun closeTimedTask(){

    }

    private fun closeLimitTask(){
        limitTaskExecutor.close()
    }

    override fun onBind(intent: Intent): IBinder? {
        return stateBinder
    }


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
                    .setContentTitle("PhoneLimit 运行中")
                    .setContentText("在未开启限制的状态下可以随时关闭PhoneLimit")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pi)
                    .build()
            startForeground(2, notification)
        } else {
            notification = NotificationCompat.Builder(this)
                    .setContentTitle("PhoneLimit 运行中")
                    .setContentText("在未开启限制的状态下可以随时关闭PhoneLimit")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .build()
            startForeground(1, notification)
        }
    }


    private fun checkAndRecoveryLimitTask() {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val remainTimeSeconds = sp.getLong("remain_time_seconds", 0)

        if (remainTimeSeconds > 1) {
            startLimitTask(null,remainTimeSeconds)
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

        fun updateRemainTime(timeSeconds: Long)

        fun onLimitStarted()

        fun onLimitFinished()
    }

    companion object {
        const val COMMAND_START_LIMIT = 0

        /**
         * 开启定时任务
         */
        const val COMMAND_START_TIMED_TASK = 1

    }
}
