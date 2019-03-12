package com.lfork.phonelimitadvanced.limitcore.task

import android.content.Context
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.LimitApplication.Companion.defaultLimitModel
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isRooted
import com.lfork.phonelimitadvanced.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_ROOT
import com.lfork.phonelimitadvanced.utils.RootShell

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 21:32
 */
class RootLimitTask : LauncherLimitTask() {

    /**
     * 防止在安全模式里面安装桌面程序 和 使用浏览器。
     */
    companion object {
        val appsNeedHide =
            arrayOf("com.android.settings","com.android.vending")//"com.android.chrome", "com.android.documentsui",

        val writeCommand = "mount -o rw,remount /system"

        //桌面、浏览器 、【分享功能比较弱，可以不用管】
        val specialDeviceCommandInitLimit = arrayOf(
//            "mv /system/app/QQBrowser/QQBrowser.apk /system/app/QQBrowser/QQBrowser",
            "pm hide net.oneplus.launcher",
            "mv /system/priv-app/OPLauncher2/OPLauncher2.apk /system/priv-app/OPLauncher2/OPLauncher2"
        )

        val specialDeviceCommandCloseLimit = arrayOf(
//            "mv /system/app/QQBrowser/QQBrowser.apk /system/app/QQBrowser/QQBrowser.apk",
            "mv /system/priv-app/OPLauncher2/OPLauncher2 /system/priv-app/OPLauncher2/OPLauncher2.apk",
            "pm unhide net.oneplus.launcher"
        )
    }


    override fun initLimit(context: Context) {
        super.initLimit(context)
        if (isRooted && defaultLimitModel == LIMIT_MODEL_ROOT) {

            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
            appsNeedHide.forEach {
                RootShell.execRootCmd("pm hide $it")
            }

            RootShell.execRootCmd(writeCommand)

            specialDeviceCommandInitLimit.forEach {
                val result = RootShell.execRootCmd(it)

            }
        }
    }


    override fun closeLimit() {
        super.closeLimit()
        if (isRooted && defaultLimitModel == LIMIT_MODEL_ROOT) {


            appsNeedHide.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }

            RootShell.execRootCmd(writeCommand)

            specialDeviceCommandCloseLimit.forEach {
                val result = RootShell.execRootCmd(it)

            }

            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
                val result = RootShell.execRootCmd("pm unhide $it")
            }
        }
    }


}