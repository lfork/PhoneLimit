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
class FloatingLauncherLimitTask: FloatingLimitTask() {

    override fun onHomeKeyClicked() {
        //什么也不做
    }

    override fun closeLimit() {
        mContext?.clearDefaultLauncher()
        super.closeLimit()
    }
}