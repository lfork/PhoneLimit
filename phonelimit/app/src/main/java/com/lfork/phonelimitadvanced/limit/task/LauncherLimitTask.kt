package com.lfork.phonelimitadvanced.limit.task

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.limit.LimitTask
import com.lfork.phonelimitadvanced.utils.RootShell
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/06 17:00
 *
 * 桌面限制+ 悬浮窗
 */
class LauncherLimitTask : SimpleLimitTask() {

    override fun initLimit(context: Context) {
        super.initLimit(context)
        if (LimitApplication.isRooted) {
            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }
    }

    override fun closeLimit() {
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }
        }
        mContext!!.clearDefaultLauncher()
        mContext = null
    }

}