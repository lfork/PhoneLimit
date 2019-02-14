package com.lfork.phonelimitadvanced.limitcore.task

import android.content.Context
import com.lfork.phonelimitadvanced.limitcore.LimitTask
import com.lfork.phonelimitadvanced.base.permission.PermissionManager.clearDefaultLauncher

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 21:35
 */
class FloatingLauncherLimitTask:LimitTask {

    private lateinit var floatingLimitTask:FloatingLimitTask

    override fun initLimit(context: Context) {
        mContext = context
        floatingLimitTask = FloatingLimitTask()
        floatingLimitTask.initLimit(context)
    }

    override fun doLimit() :Boolean{
        return floatingLimitTask.doLimit()
    }

    private var mContext: Context? = null

    override fun closeLimit() {
        mContext?.clearDefaultLauncher()
        mContext = null
        floatingLimitTask.closeLimit()
    }
}