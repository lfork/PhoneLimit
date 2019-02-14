package com.lfork.phonelimitadvanced.limitcore.task

import com.lfork.phonelimitadvanced.base.permission.PermissionManager.clearDefaultLauncher

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/06 17:00
 *
 * 桌面限制+ 悬浮窗
 */
open class LauncherLimitTask : BaseLimitTask() {

    override fun closeLimit() {
        mContext!!.clearDefaultLauncher()
        super.closeLimit()
    }

}