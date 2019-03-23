package com.lfork.phonelimit.limitcore.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RecentlyReceiver : BroadcastReceiver() {

    var systemKeyListener :SystemKeyListener?=null

    override fun onReceive(context: Context, intent: Intent) {
        val reason = intent.getStringExtra("reason")
        if (reason != null) {
            when (reason) {
                "homekey"//Home键
                -> {
                    Log.d("反应速度测试", "homekey")
                    systemKeyListener?.onHomeKeyClicked()
                }
                "recentapps"//最近任务键
                -> {
                    systemKeyListener?.onRecentAppsClicked()
                    Log.d("反应速度测试", "recentapps")
                }
                "assist"//长按Home键

                -> {
                    Log.d("反应速度测试", "assist")
                    systemKeyListener?.onRecentAppsClicked()
                }
                else -> {
                }
            }
        }
    }

    interface SystemKeyListener{
        fun onRecentAppsClicked()

        fun onHomeKeyClicked()

    }

    fun registerKeyListener(listener :SystemKeyListener){
        systemKeyListener = listener
    }

    fun unregisterKeyListener(){
        systemKeyListener = null
    }
}