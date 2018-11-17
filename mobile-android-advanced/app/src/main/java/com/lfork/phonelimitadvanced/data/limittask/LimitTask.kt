package com.lfork.phonelimitadvanced.data.limittask

/**
 *
 * Created by 98620 on 2018/11/17.
 */
class LimitTask {
    var state = 0

    var limitTimeSeconds = 0

    companion object {
        const val STATE_FREE = 0
        const val STATE_LIMITED_AUTO = 1
        const val STATE_LIMITED_LOCKED = 2
    }

    /**
     * 自动保存任务的状态，防止任务丢失
     */
    var autoSaveTask = Runnable {  }
}