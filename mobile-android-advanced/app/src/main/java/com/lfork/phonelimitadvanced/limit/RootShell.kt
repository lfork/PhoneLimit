package com.lfork.phonelimitadvanced.limit

import android.util.Log
import com.lfork.phonelimitadvanced.util.LinuxShell
import java.io.*

/**
 *
 * Created by 98620 on 2018/10/30.
 */
object RootShell {

    const val MOUNT_READ_WRITE = "mount -o rw,remount /system"

    const val MOVE_NET_FILE=
        """
            mount -o rw,remount /system

        """

    private const val TAG = "ShellTest"


    /**
     * root 后 shell命令的正确打开方式
     */
    // 执行命令并且输出结果
    @Synchronized
    fun execRootCmd(cmd: String): String {
        val result = LinuxShell.execCommand(cmd, true)
        return "code:${result.result}  errorMsg:${result.errorMsg}  successMsg:${result.successMsg}"
    }

    fun setAirPlaneMode(enable: Boolean) {
        val mode = if (enable) 1 else 0
        val cmd = "settings put global airplane_mode_on $mode"
        try {
            Runtime.getRuntime().exec(cmd)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }



}