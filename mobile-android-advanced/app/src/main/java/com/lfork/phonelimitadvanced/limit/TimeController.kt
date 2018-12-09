package com.lfork.phonelimitadvanced.limit

import android.util.Log

/**
 *
 * Created by 98620 on 2018/11/2.
 */
object TimeController {

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

    private const val CMD_START_LIMIT = "iptables -P OUTPUT DROP"
//        "mount -o rw,remount /vendor;" +
//                "rm -rf /vendor/etc/tmp;" +
//                "mkdir -m 777 /vendor/etc/tmp;" +
//                "mv /vendor/etc/data /vendor/etc/tmp/data;" +
//                "mv /vendor/etc/wifi /vendor/etc/tmp/wifi;"

    private const val CMD_HIDE_OTHER_LAUNCHER = "pm hide "

    private const val CMD_UNHIDE_OTHER_LAUNCHER = "pm unhide "
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

    var timeSeconds = 0L

    var autoEndThread: Thread? = null;

    var started = false

    /**
     * @return false表示已经初始化了
     */
    @Synchronized
    fun initTimer(timeSeconds: Long): Boolean {
        if (started) {
            return false
        }
        this.timeSeconds = timeSeconds
        startMachineTimeMillis = System.currentTimeMillis()
//        Log.d(TAG + "3", RootShell.execRootCmd(CMD_START_LIMIT))
        started = true
        return true
    }


    /**
     * 这个函数也不是直接解锁的，而是通过发送中断命令给自动解锁的进程，让其提前解锁
     */
    fun forceEndTimer() {
        autoEndThread?.interrupt()

    }

    const val AUTO_EXIT = 1

    const val FORCE_EXIT = 2

    fun startTimer(): Int {
        var lockType = AUTO_EXIT
        try {
            autoEndThread = Thread.currentThread()
            Thread.sleep(timeSeconds * 1000)
//            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "自动解锁成功")
        } catch (e: InterruptedException) {
//            Log.d(TAG + "4", RootShell.execRootCmd(CMD_CLOSE_LIMIT))
            Log.d(TAG, "提前解锁成功")
            lockType = FORCE_EXIT
        }
        timeIsUp()
        return lockType
    }


    /**
     * 到点了，进行状态设置
     */
    private fun timeIsUp(){
        autoEndThread = null;
        timeSeconds = 0L
        startMachineTimeMillis = 0L
        started = false
    }

    fun getRemainTimeSeconds(): Long {
        if (timeSeconds > 0) {
            return timeSeconds - (System.currentTimeMillis() - startMachineTimeMillis) / 1000
        } else {
            return 0
        }
    }

}