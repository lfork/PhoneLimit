package com.lfork.phonelimitadvanced.limit.task

import android.content.Context
import com.lfork.phonelimitadvanced.limit.LimitTask
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 21:35
 */
class UltimateFloatingLauncherLimitTask:LimitTask {

    private lateinit var floatingLimitTask:FloatingLimitTask

    override fun initLimit(context: Context) {
        mContext = context
        floatingLimitTask = FloatingLimitTask()
        floatingLimitTask.initLimit(context)
    }

    override fun doLimit() {
        floatingLimitTask.doLimit()
    }

    private var mContext: Context? = null

    override fun closeLimit() {
        mContext?.clearDefaultLauncher()
        mContext = null
        floatingLimitTask.closeLimit()
    }
}