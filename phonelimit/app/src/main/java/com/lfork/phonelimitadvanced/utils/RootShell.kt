package com.lfork.phonelimitadvanced.utils

import android.util.Log
import com.lfork.phonelimitadvanced.utils.LinuxShell
import java.io.*

/**
 *
 * Created by 98620 on 2018/10/30.
 */
object RootShell {

    /**
     * root 后 shell命令的正确打开方式 执行命令并且输出结果
     */
    @Synchronized
    fun execRootCmd(cmd: String): String {

        Log.d("Rootshell-cmd", cmd)
        val result = LinuxShell.execCommand(cmd, true)
        val resultStr = "code:${result.result}  errorMsg:${result.errorMsg}  successMsg:${result.successMsg}"

        Log.d("Rootshell-result", resultStr)
        return resultStr
    }

}