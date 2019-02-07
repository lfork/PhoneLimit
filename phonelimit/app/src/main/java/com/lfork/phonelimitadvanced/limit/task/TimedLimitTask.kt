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
 * 通过限制桌面入口的方式来实现限制功能
 */
class TimedLimitTask: LimitTask {

    private var mContext :Context? = null

    override fun initLimit(context: Context) {
        mContext = context
    }

    override fun doLimit() {

        if (mContext == null){
            return
        }

    }


    override fun closeLimit() {
        mContext = null
    }
}