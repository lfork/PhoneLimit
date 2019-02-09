package com.lfork.phonelimitadvanced.limit.task

import android.content.Context
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.LimitApplication.Companion.defaultLimitModel
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isRooted
import com.lfork.phonelimitadvanced.limit.LimitTaskConfig.Companion.LIMIT_MODEL_ROOT
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
        if (isRooted && defaultLimitModel == LIMIT_MODEL_ROOT) {
            val launchers = LimitApplication.App.getLauncherApps()
            launchers?.forEach {
               val result =  RootShell.execRootCmd("pm unhide $it")
                Log.d("Rootshell", result)
            }
        }
    }

    override fun initLimit(context: Context) {
        super.initLimit(context)
        if (isRooted && defaultLimitModel == LIMIT_MODEL_ROOT) {
            LimitApplication.App.getLauncherApps()?.forEach {
                RootShell.execRootCmd("pm hide $it")
            }
        }
    }

}