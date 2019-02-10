package com.lfork.phonelimitadvanced.limit

import kotlinx.android.synthetic.main.item_timed_task.view.*
import java.io.Serializable
import java.util.*
import java.util.concurrent.ScheduledFuture

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:54
 */
data class LimitTaskConfig(

    /**
     * 默认为25分钟
     */
    var limitTimeSeconds: Long = 25 * 60,

    /**
     * 默认是创建这个对象时的时间
     */
    var startTime: Calendar = GregorianCalendar(),

    var cycleModel: Int = CYCLE_MODEL_DAILY,

    var limitModel: Int = LIMIT_MODEL_LIGHT
) : Serializable {

    var taskName:String?=null

    var id: UUID = UUID.randomUUID()

    /**
     * false代表定时任务，true表示立即执行的任务
     */
    var isImmediatelyExecuted: Boolean = false

//    var taskController: ScheduledFuture<*>?=null

    var isActive = true


    fun getStarTimeStr():String{
        val minuteStr = if (startTime.get(Calendar.MINUTE) < 10) {
            "0${startTime.get(Calendar.MINUTE)}"
        } else {
           startTime.get(Calendar.MINUTE).toString()
        }

        return startTime.get(Calendar.HOUR_OF_DAY).toString() + ":" +   minuteStr
    }

    companion object {

        //_APP_JUMP
        const val LIMIT_MODEL_LIGHT = 0

        //_LAUNCHER_LIMIT
//        const val LIMIT_MODEL_LAUNCHER = 1

        //_FLOATING_LIMIT
        const val LIMIT_MODEL_FLOATING = 1

        /**
         * 在小米等有可以关闭应用程序的手机助手APP的机型上适用
         * _LAUNCHER_FLOATING_MODEL
         */
        const val LIMIT_MODEL_ULTIMATE = 2

        const val LIMIT_MODEL_ROOT = 3

        const val CYCLE_MODEL_NO_CYCLE = 0

        const val CYCLE_MODEL_DAILY = 1

        const val CYCLE_MODEL_WEEKLY = 2
    }


}