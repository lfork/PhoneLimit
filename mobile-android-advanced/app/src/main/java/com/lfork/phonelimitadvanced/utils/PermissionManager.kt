package com.lfork.phonelimitadvanced.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import com.lfork.phonelimitadvanced.main.focus.FakeHomeActivity
import com.lfork.phonelimitadvanced.utils.LinuxShell.execCommand
import com.lfork.phonelimitadvanced.widget.DialogPermission
import java.io.DataOutputStream
import java.util.ArrayList


/**
 *
 * Created by 98620 on 2018/10/30.
 */

object PermissionManager{
    /**
     * 查看设备是否已经Root
     * @return true is rooted else false
     */
    fun isRooted(): Boolean {
        return execCommand("echo root", true, false).result == 0
    }

    /**
     * @return true succeed else false
     */
    fun requestRootPermission(pkgCodePath: String): Boolean {
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
    fun isGrantedRootPermission(): Boolean {
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

    fun Context.requestFloatingWindowPermission() {
        //权限申请
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    fun Fragment.requestFloatingWindowPermission() {
       context?.requestFloatingWindowPermission()
    }


    fun Context.isGrantedWindowPermission(): Boolean {
        //权限申请
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!Settings.canDrawOverlays(this)) {
                return false
            }
        }
        return true
    }


    fun Fragment.isGrantedWindowPermission(): Boolean {
        return context!!.isGrantedWindowPermission()
    }

    fun Context.clearDefaultLauncher() {
        val context = this
        val packageManager = context.packageManager
        val componentName = ComponentName(context, FakeHomeActivity::class.java)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )


//        val selector = Intent(Intent.ACTION_MAIN)
//        selector.addCategory(Intent.CATEGORY_HOME)
//        context.startActivity(selector)

//        packageManager.setComponentEnabledSetting(
//            componentName,
//            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//            PackageManager.DONT_KILL_APP
//        )

    }

    fun Fragment.clearDefaultLauncher() {
        context?.clearDefaultLauncherFake()
    }


    fun Context.clearDefaultLauncherFake() {
        val context = this
        val packageManager = context.packageManager
        val componentName = ComponentName(context, FakeHomeActivity::class.java)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        Log.d("清除桌面测试","成功")
    }


    fun Fragment.clearDefaultLauncherFake() {
        context?.clearDefaultLauncherFake()
    }




    /**
     * Returns whether the launcher which running on the device is importance foreground.
     *
     * @return True if the importance of the launcher process is [android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND].
     */
    fun Context.isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)//Intent.ACTION_VIEW
        intent.addCategory("android.intent.category.HOME")
        intent.addCategory("android.intent.category.DEFAULT")
        val pm = packageManager
        val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return packageName == info.activityInfo.packageName
    }


    fun Fragment.isDefaultLauncher(): Boolean {
       return context!!.isDefaultLauncher()
    }

    /**
     * 判断是否已经获取 有权查看使用情况的应用程序 权限
     *
     * @param context
     * @return
     */
    fun Context.isGrantedStatAccessPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                val packageManager =packageManager
                val info = packageManager.getApplicationInfo(packageName, 0)
                val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName)
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    info.uid,
                    info.packageName
                ) == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        } else {
            true
        }
    }

    fun Fragment.isGrantedStatAccessPermission(): Boolean {
        return context!!.isGrantedStatAccessPermission()
    }


    fun Activity.requestStateUsagePermission(requestCode: Int) {
        val dialog = DialogPermission(this)
        dialog.show()
        dialog.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS) ,requestCode)
        }
    }

    fun Fragment.requestStateUsagePermission(requestCode: Int) {
       activity!!.requestStateUsagePermission(requestCode)
    }



    /**
     * Return PackageManager.
     *
     * @param context A Context of the application package implementing this class.
     * @return a PackageManager instance.
     */
    private fun getActivityManager(context: Context): ActivityManager {
        return context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    }

    /**
     * Returns a list of launcher that are running on the device.
     *
     * @param context A Context of the application package implementing this class.
     * @return A list which contains all the launcher package appName.If there are no launcher, an empty
     * list is returned.
     */
    private fun getLaunchers(context: Context): List<String> {
        val packageNames = ArrayList<String>()
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo
            if (activityInfo != null) {
                packageNames.add(resolveInfo.activityInfo.processName)
                packageNames.add(resolveInfo.activityInfo.packageName)
            }
        }
        return packageNames
    }
}