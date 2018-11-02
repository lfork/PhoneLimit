package com.lfork.phonelimitadvanced.limit

import android.util.Log
import kotlinx.android.synthetic.main.main_act.*

/**
 *
 * Created by 98620 on 2018/11/2.
 */
object PhoneLimitController {

    var defaultMinuteTime = 180L

    const val CMD_START_LIMIT=
        "mount -o rw,remount /system;" +
                "rm -rf /system/etc/tmp;" +
                "mkdir -m 777 /system/etc/tmp;" +
                "mv /system/etc/data /system/etc/tmp/data;" +
                "mv /system/etc/wifi /system/etc/tmp/wifi;"

    //file 644 dir755
    const val CMD_CLOSE_LIMIT=
        "mount -o rw,remount /system;" +
                "mv /system/etc/tmp/data /system/etc/data;" +
                "mv /system/etc/tmp/wifi /system/etc/wifi;"
    private const val TAG = "ShellTest"

    fun startLimit(){
        Log.d(TAG+"3",PLShell.execRootCmd(CMD_START_LIMIT))
        //startAutoUnlock()
    }

    fun closeLimit(){
        Log.d(TAG+"4",PLShell.execRootCmd(CMD_CLOSE_LIMIT))
    }

    fun startAutoUnlock(minute: Long) {
            Thread.sleep(1000)
    }

    fun startAutoUnlock() {
        Thread.sleep(defaultMinuteTime*1000*60)
        closeLimit()

    }

}