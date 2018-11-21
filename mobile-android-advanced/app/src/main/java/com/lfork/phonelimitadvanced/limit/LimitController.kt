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
    const val CMD_SECURITY_BACKUP = "asd"

    /**
     * 紧急恢复命令(默认每月只有三次机会)：防止因为各种意外的原因出现的正常解锁失败的情况
     */
    const val CMD_SECURITY_RECOVERY = "asd"

//    private const val CMD_START_LIMIT =
//        "mount -o rw,remount /system;" +
//                "rm -rf /system/etc/tmp;" +
//                "mkdir -m 777 /system/etc/tmp;" +
//                "mv /system/etc/data /system/etc/tmp/data;" +
//                "mv /system/etc/wifi /system/etc/tmp/wifi;"
//
//    //file 644 dir755
//    private const val CMD_CLOSE_LIMIT =
//        "mount -o rw,remount /system;" +
//                "mv /system/etc/tmp/data /system/etc/data;" +
//                "mv /system/etc/tmp/wifi /system/etc/wifi;"

    private const val CMD_START_LIMIT ="iptables -P OUTPUT DROP"
//        "mount -o rw,remount /vendor;" +
//                "rm -rf /vendor/etc/tmp;" +
//                "mkdir -m 777 /vendor/etc/tmp;" +
//                "mv /vendor/etc/data /vendor/etc/tmp/data;" +
//                "mv /vendor/etc/wifi /vendor/etc/tmp/wifi;"

    //file 644 dir755
    private const val CMD_CLOSE_LIMIT = "iptables -P OUTPUT ACCEPT"
//        "mount -o rw,remount /vendor;" +
//                "mv /vendor/etc/tmp/data /vendor/etc/data;" +
//                "mv /vendor/etc/tmp/wifi /vendor/etc/wifi;"

    private const val TAG = "ShellTest"

    var limitTimeSeconds = 0L

    var autoLockThread: Thread? = null;

    var limited = false

    @Synchronized
    fun startLimit(limitTimeSeconds: Long): Boolean {
        if (limited) {
            return false
        }
        this.limitTimeSeconds = limitTimeSeconds
        startMachineTimeMillis = System.currentTimeMillis()
//        Log.d(TAG + "3", RootShell.execRootCmd(CMD_START_LIMIT))
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

    fun startAutoUnlock(): Int {

        var lockType = AUTO_UNLOCKED

        try {
            autoLockThread = Thread.currentThread()
            Thread.sleep(limitTimeSeconds * 1000)
//            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "自动解锁成功")
        } catch (e: InterruptedException) {
//            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "提前解锁成功")
            lockType = FORCE_UNLOCKED
        }
        autoLockThread = null;
        limitTimeSeconds = 0L
        startMachineTimeMillis = 0L

        return lockType
    }

    fun getRemainTimeSeconds(): Long {
        if (limitTimeSeconds > 0) {
            return limitTimeSeconds - (System.currentTimeMillis() - startMachineTimeMillis) / 1000
        } else {
            return 0
        }
    }

}