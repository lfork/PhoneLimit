package com.lfork.phonelimitadvanced

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import java.io.DataOutputStream

/**
 *
 * Created by 98620 on 2018/10/30.
 */
object PermissionManager {

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    fun getRootPermission(pkgCodePath: String): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = "chmod 777 $pkgCodePath"
            process = Runtime.getRuntime().exec("su") //切换到root帐号
            os = DataOutputStream(process!!.outputStream)

            os.writeChars(cmd + "\n")
            os.writeChars("exit\n")

            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            return false
        } finally {
            try {
                os?.close()
                process!!.destroy()
            } catch (e: Exception) {
            }

        }
        return true
    }

    @Synchronized
    fun getRootAhth(): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process!!.outputStream)
            os.writeBytes("exit\n")
            os.flush()
            val exitValue = process.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.message)
            return false
        } finally {
            try {
                os?.close()
                process!!.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun requestStoragePermission(context: Context, requestCode: Int, activity: Activity) {
        if (!isGrantedStoragePermission(context)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode
            )
        }
    }

    fun isGrantedStoragePermission(context: Context): Boolean {
        val checkCallPhonePermission =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return checkCallPhonePermission == PackageManager.PERMISSION_GRANTED

    }
}