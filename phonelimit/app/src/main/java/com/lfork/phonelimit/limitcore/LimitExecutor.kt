package com.lfork.phonelimit.limitcore

import android.content.Context
import com.lfork.phonelimit.base.permission.PermissionManager.clearDefaultLauncher
import com.lfork.phonelimit.base.permission.PermissionManager.clearDefaultLauncherFake

/**
 *
 * Created by 98620 on 2018/12/14.
 */
class LimitExecutor(var context: Context?, var limitTask: LimitTask?) {

    lateinit var executorThread: Thread

    var isActive = false

    /**
     * 只能调用一次开始
     */
    fun start(): Boolean {
        if (isActive || context == null) {
            return false
        }
        val executorTask = Runnable {
            isActive = true
            beforeLimitation()
            while (isActive) {
                limitTask?.doLimit()
            }
            releaseLimitation()
            onDestroy()
        }
        executorThread = Thread(executorTask)
        executorThread.name = "限制监督与执行线程"
        executorThread.start()

        return true
    }

    /**
     * 从外部关闭Executor
     */
    fun close() {
        if (!isActive){
            return
        }
        isActive = false

        //尽快结束线程
        executorThread.interrupt()
    }

    /**
     * 进行最后的资源释放
     */
    fun onDestroy() {
        context = null
        limitTask = null
    }

    /**
     * 这个主要是给root用户使用的
     */
    private fun beforeLimitation() {
        context?.let {
            limitTask?.initLimit(it)
        }
    }


    /**
     * 结束限制：时间到了，然后可以选桌面了。
     * 因为Android的运行机制，结束限制需要服务端(Service)
     * 和客户端(Activity)先后调用
     * @see clearDefaultLauncher,
     * @see clearDefaultLauncherFake
     */
    private fun releaseLimitation() {
        limitTask?.closeLimit()
    }


}