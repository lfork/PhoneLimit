package com.lfork.phonelimitadvanced.limit

import android.util.Log
import com.lfork.phonelimitadvanced.CallBack
import java.io.*

/**
 *
 * Created by 98620 on 2018/10/30.
 */
object PLShell {


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
    fun execRootCmd(cmd: String): String {
        var result = ""
        var dos: DataOutputStream? = null
        var dis: DataInputStream? = null

        try {
            val rootShell = Runtime.getRuntime().exec("su")// 经过Root处理的android系统即有su命令
            dos = DataOutputStream(rootShell.outputStream)
            dis = DataInputStream(rootShell.inputStream)

            Log.i(TAG, cmd)
            dos.writeBytes(cmd + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String? = dis.readLine()
            while ((line) != null) {
                Log.d(TAG, line)
                result += line + "\n"

                line = dis.readLine()
            }
            rootShell.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (dis != null) {
                try {
                    dis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return result
    }

    /**
     * 异步执行命令
     */
    fun asyncExecRootCmd(cmd: String, callBack: CallBack<String>) {
        val rootShell = Runtime.getRuntime().exec("su")// 经过Root处理的android系统即有su命令
        val dos = DataOutputStream(rootShell.outputStream)
        val dis = DataInputStream(rootShell.inputStream)
        var result = ""
        Log.d(TAG + "2", "开始执行shell命令")
        try {
            //处理shell输出的信息
            Log.i(TAG + "1.0", cmd)
            dos.writeBytes(cmd + "\n")
            dos.flush()
//                dos.writeBytes("exit\n")
//                dos.flush()
            var line: String? = dis.readLine()
            while ((line) != null) {
                Log.d("TAG", line)
                result += line + "\n"
                line = dis.readLine()
            }
            Log.d(TAG + "2", "执行shell命令结束")
            callBack.succeed(result)
            rootShell.waitFor()


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
//                if (dos != null) {
//                    try {
//                        dos.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//                if (dis != null) {
//                    try {
//                        dis.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
        }


    }


}