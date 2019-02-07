package com.lfork.phonelimitadvanced.utils

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
        val result = LinuxShell.execCommand(cmd, true)
        return "code:${result.result}  errorMsg:${result.errorMsg}  successMsg:${result.successMsg}"
    }

}