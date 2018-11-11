package com.lfork.phonelimitadvanced.limit

import android.util.Log

/**
 *
 * Created by 98620 on 2018/11/2.
 */
object PhoneLimitController {

    var startMachineTimeMillis = 0L

    const val CMD_START_LIMIT =
            "mount -o rw,remount /system;" +
                    "rm -rf /system/etc/tmp;" +
                    "mkdir -m 777 /system/etc/tmp;" +
                    "mv /system/etc/data /system/etc/tmp/data;" +
                    "mv /system/etc/wifi /system/etc/tmp/wifi;"

    //file 644 dir755
    const val CMD_CLOSE_LIMIT =
            "mount -o rw,remount /system;" +
                    "mv /system/etc/tmp/data /system/etc/data;" +
                    "mv /system/etc/tmp/wifi /system/etc/wifi;"
    private const val TAG = "ShellTest"

    var limitedTimeSeconds = 0L

    var autoLockThread: Thread? = null;

    var limited = false

    @Synchronized
    fun startLimit(limitTimeMinutes: Long): Boolean {

        if (limited) {
            return false
        }
        limitedTimeSeconds = limitTimeMinutes * 60
        startMachineTimeMillis = System.currentTimeMillis()
        Log.d(TAG + "3", RootShell.execRootCmd(CMD_START_LIMIT))
        limited = true

//        refreshNetwork();
        return true
        //startAutoUnlock()
    }

//    private fun refreshNetwork() {
//        RootShell.setAirPlaneMode(false)
////        RootShell.setAirPlaneMode(true)
//    }


    /**
     * 只能通过自动解锁的方式来解锁，这个函数也不是直接解锁的，而是通过发送中断命令给自动解锁的进程，让其提前解锁
     */
    fun closeLimit() {
        autoLockThread?.interrupt()
        limited = false
//        refreshNetwork();
    }

    const val AUTO_UNLOCKED = 1

    const val FORCE_UNLOCKED = 2

    fun startAutoUnlock():Int {

        var lockType = AUTO_UNLOCKED

        try {
            autoLockThread = Thread.currentThread()
            Thread.sleep(limitedTimeSeconds * 1000)
            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "自动解锁成功")
        } catch (e: InterruptedException) {
            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "提前解锁成功")
            lockType = FORCE_UNLOCKED
        }
        autoLockThread = null;
        limitedTimeSeconds = 0L
        startMachineTimeMillis = 0L

        return lockType
    }

    fun getRemainTimeSeconds(): Long {
        if (limitedTimeSeconds > 0) {
            return limitedTimeSeconds - (System.currentTimeMillis() - startMachineTimeMillis) / 1000
        } else {
            return 0
        }
    }

}