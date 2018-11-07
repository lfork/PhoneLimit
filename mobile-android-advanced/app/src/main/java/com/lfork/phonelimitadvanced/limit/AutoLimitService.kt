package com.lfork.phonelimitadvanced.limit

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.util.ToastUtil


class AutoLimitService : Service() {

    override fun onCreate() {
        super.onCreate()
        //使用前台服务 防止被系统回收 状态栏会显示一个通知
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
                .build()
            startForeground(2, notification)

        } else {
            val intent = Intent(this, MainActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, 0)
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

        ToastUtil.showLong(applicationContext, "限制已开启")


    }


    var isLimited = false;

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand:  executed " + Thread.currentThread().name)
        //        让服务运行完毕后自动停止
        //        1、开一个线程
        if (!isLimited) {
            PhoneLimitController.startLimit()
            isLimited = true
            Thread(Runnable {
                //2、调用 stopSelf()；  //关闭一个服务：调用stopSelf() 或者stopService(stopIntent);
                PhoneLimitController.startAutoUnlock()
                stopSelf()
                isLimited = false
            }).start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ToastUtil.showLong(applicationContext, "限制已解除")
    }


    companion object {
        private val TAG = "MyService"
    }
}
