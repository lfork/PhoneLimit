package com.lfork.phonelimitadvanced.permission

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.widget.FloatingPermissionDialog
import com.lfork.phonelimitadvanced.base.widget.UsagePermissionDialog
import com.lfork.phonelimitadvanced.main.focus.FocusFragment2
import com.lfork.phonelimitadvanced.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimitadvanced.permission.PermissionManager.isGrantedFloatingWindowPermission
import com.lfork.phonelimitadvanced.permission.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimitadvanced.permission.PermissionManager.requestFloatingWindowPermission
import com.lfork.phonelimitadvanced.utils.LockUtil
import com.lfork.phonelimitadvanced.utils.ToastUtil
import com.lfork.phonelimitadvanced.utils.openDefaultAppsSetting

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/10 16:46
 */


fun Fragment.checkAndRequestUsagePermission(): Boolean {
    if (context == null) {
        return false
    }

    return context!!.checkAndRequestUsagePermission()
}

fun Context.checkAndRequestUsagePermission(): Boolean {
    if (!isGrantedStatAccessPermission() && LockUtil.isNoOption(this)) {
        val dialog = UsagePermissionDialog(this)

        dialog.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        dialog.setOnCancelListener {
//            FocusFragment2.inputTimeMinuteCache = -1
        }
        dialog.show()
        return false
    }

    return true
}

fun Fragment.requestFloatingPermission(): Boolean {
    if (context == null) {
        return false
    }

    return context!!.requestFloatingPermission()
}


fun Context.requestFloatingPermission(): Boolean {

    if (!isGrantedFloatingWindowPermission()) {
        val dialog = FloatingPermissionDialog(this)
        dialog.setOnClickListener {
            requestFloatingWindowPermission()
        }
//        dialog.setOnCancelListener { FocusFragment2.inputTimeMinuteCache = -1 }
        dialog.show()
        return false
    }
    return true
}



fun Fragment.requestLauncherPermission(): Boolean {
    if (context == null){
        return false
    }

    return context!!.requestLauncherPermission()
}

fun Context.requestLauncherPermission(): Boolean {

    val dialog = AlertDialog.Builder(this).setTitle(R.string.tips_launcher_setting)
        .setPositiveButton(R.string.action_default_apps_setting) { dialog, id ->
            //去设置默认桌面
            openDefaultAppsSetting()
            dialog.dismiss()
        }.setNegativeButton(R.string.cancel) { dialog, id ->
//            FocusFragment2.inputTimeMinuteCache = -1
        }
        .setCancelable(false)
        .create()

    if (!isDefaultLauncher()) {

        if (!dialog.isShowing) {
            dialog.show()
        }
        if (!isDefaultLauncher()) {
            ToastUtil.showLong(this, getString(R.string.launcher_denied_tips))
            return false
        }
    }

    return true
}

fun Fragment.requestRootPermission(): Boolean {
    if (context == null){
        return false
    }

    return context!!.requestRootPermission()
}

fun Context.requestRootPermission(): Boolean {
    if (PermissionManager.isRooted()) {
        if (!PermissionManager.isGrantedRootPermission()) {
            PermissionManager.requestRootPermission(this.packageCodePath)

            if (!PermissionManager.isGrantedRootPermission()) {
                ToastUtil.showShort(this, getString(R.string.permission_denied_tips))
                LimitApplication.isRooted = false
                return false
            }
        }
    } else {
        Log.d(LimitApplication.TAG, "看来是没有ROOT")
        return false
    }
    LimitApplication.isRooted = true
    return true
}