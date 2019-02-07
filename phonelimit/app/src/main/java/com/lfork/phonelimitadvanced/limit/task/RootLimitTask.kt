package com.lfork.phonelimitadvanced.limit.task

import android.content.Context
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.utils.RootShell

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 21:32
 */
class RootLimitTask :LauncherLimitTask(){
    override fun closeLimit() {
        super.closeLimit()
        if (LimitApplication.isRooted) {
            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
                RootShell.execRootCmd("pm unhide $it")
            }
        }
    }

    override fun initLimit(context: Context) {
        super.initLimit(context)
        if (LimitApplication.isRooted) {
            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }
    }

}