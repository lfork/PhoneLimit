package com.lfork.phonelimitadvanced.limit

import android.util.Log

/**
 *
 * Created by 98620 on 2018/11/2.
 */
object LimitController {

    var startMachineTimeMillis = 0L

    /**
     * 备份相关文件防止出现意外
     */
    const val CMD_SECURITY_BACKUP ="asd"

    /**
     * 紧急恢复命令(默认每月只有三次机会)：防止因为各种意外的原因出现的正常解锁失败的情况
     */
    const val CMD_SECURITY_RECOVERY ="asd"

    private const val CMD_START_LIMIT =
            "mount -o rw,remount /system;" +
                    "rm -rf /system/vendor/tmp;" +
                    "mkdir -m 777 /system/vendor/tmp;" +
                    "mv /system/vendor/data /system/vendor/tmp/data;" +
                    "mv /system/vendor/wifi /system/vendor/tmp/wifi;"

    //file 644 dir755
    private const val CMD_CLOSE_LIMIT =
            "mount -o rw,remount /system;" +
                    "mv /system/vendor/tmp/data /system/vendor/data;" +
                    "mv /system/vendor/tmp/wifi /system/vendor/wifi;"
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
        return true
    }


    /**
     * 只能通过自动解锁的方式来解锁，这个函数也不是直接解锁的，而是通过发送中断命令给自动解锁的进程，让其提前解锁
     */
    fun closeLimit() {
        autoLockThread?.interrupt()
        limited = false
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