package com.lfork.phonelimitadvanced

import android.util.Log
import java.io.*

/**
 *
 * Created by 98620 on 2018/10/30.
 */
object PLShell {

    val rootShell = Runtime.getRuntime().exec("su")// 经过Root处理的android系统即有su命令
    val dos = DataOutputStream(rootShell.outputStream)
    val dis = DataInputStream(rootShell.inputStream)


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
                Log.d("result", line)
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
        var result = ""
        try {
            //处理shell输出的信息
            Thread(Runnable {
                Log.i(TAG+"1", cmd)
                dos.writeBytes(cmd + "\n")
                dos.flush()
//                dos.writeBytes("exit\n")
//                dos.flush()
                var line: String? = dis.readLine()
                while ((line) != null) {
                    Log.d("result", line)
                    result += line + "\n"
                    line = dis.readLine()
                }
                callBack.succeed(result)
                rootShell.waitFor()
            }).start()


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