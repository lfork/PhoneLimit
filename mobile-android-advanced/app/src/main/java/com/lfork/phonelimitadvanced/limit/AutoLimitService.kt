package com.lfork.phonelimitadvanced.limit

import android.app.DownloadManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.support.annotation.IntDef
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.MainActivity

class AutoLimitService : Service() {
    private val mBinder = DownloadBinder()

    internal inner class DownloadBinder : Binder() {

        val progress: Int
            get() {
                Log.d(TAG, "getProgress: executed")
                return 0
            }

        fun startDownload() {
            Log.d(TAG, "startDownload:  executed")
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: executed")
        super.onCreate()
        //使用前台服务 防止被系统回收 状态栏会显示一个通知
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand:  executed " + Thread.currentThread().name)
        //        让服务运行完毕后自动停止
        //        1、开一个线程
        Thread(Runnable {
            //2、调用 stopSelf()；  //关闭一个服务：调用stopSelf() 或者stopService(stopIntent);
            PhoneLimitController.startAutoUnlock()
            stopSelf()
        }).start()


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return mBinder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: executed")
        super.onDestroy()
    }

    companion object {
        private val TAG = "MyService"
    }
}
