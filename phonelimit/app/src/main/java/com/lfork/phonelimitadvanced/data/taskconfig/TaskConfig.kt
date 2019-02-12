package com.lfork.phonelimitadvanced.data.taskconfig

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable
import java.lang.Exception
import java.util.*

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:54
 */
@Entity
data class TaskConfig(
    /**
     * 默认为25分钟
     */
    var limitTimeSeconds: Long = 25 * 60,

    var cycleModel: Int = CYCLE_MODEL_DAILY,

    var limitModel: Int = LIMIT_MODEL_LIGHT
) : Serializable {

    //0~23
    var startTimeHourOfDay: Int = -1

    //0~59
    var startTimeMinute: Int = -1

    //1~7
    var startTimeDayOfWeek: Int = -1

    @Ignore
    var startTimeMillisForUnfinishedTask: Long = -1;


    var taskName: String? = null

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    /**
     * false代表定时任务，true表示立即执行的任务
     */
    var isImmediatelyExecuted: Boolean = false

//    var taskController: ScheduledFuture<*>?=null

    var isActive = true

    var tips: String? = "权限不足，执行失败"

    fun getStarTimeStr(needWeek: Boolean = false): String {


        if (startTimeMinute == -1 || startTimeHourOfDay == -1) {
            val time = GregorianCalendar()
            val minute = time.get(Calendar.MINUTE)
            val hour = time.get(Calendar.HOUR_OF_DAY)

            val minuteStr = if (minute < 10) {
                "0$minute"
            } else {
                minute.toString()
            }

            return hour.toString() + ":" + minuteStr
        }

        val minuteStr = if (startTimeMinute < 10) {
            "0$startTimeMinute"
        } else {
            startTimeMinute.toString()
        }
        if (needWeek) {


            val week = when (startTimeDayOfWeek) {
                1 -> " 周日"
                2 -> " 周一"
                3 -> " 周二"
                4 -> " 周三"
                5 -> " 周四"
                6 -> " 周五"
                7 -> " 周六"
                else -> ""
            }

            return startTimeHourOfDay.toString() + ":" + minuteStr + week
        }

        return startTimeHourOfDay.toString() + ":" + minuteStr
    }


    fun getLimitTimeStr() =
        when {
            limitTimeSeconds > 60 * 60 ->
                "${limitTimeSeconds / 3600}小时${(limitTimeSeconds % 3600) / 60}分${limitTimeSeconds % 60}秒"
            limitTimeSeconds > 60 -> {
                val result: String = if (limitTimeSeconds % 60 == 0L) {
                    "${limitTimeSeconds / 60}分"
                } else {
                    "${limitTimeSeconds / 60}分${limitTimeSeconds % 60}秒"
                }
                result
            }
            else -> "${limitTimeSeconds}秒"
        }

    /**
     * @return 获取任务第一次执行的时间millis
     */
    fun getStartTimeMillisForTimedTask(): Long {

        if (startTimeHourOfDay == -1 || startTimeMinute == -1) {
            throw Exception("startTimeHourOfDay , startTimeMinute must be initialized.")
        }

        if (cycleModel == CYCLE_MODEL_WEEKLY && startTimeDayOfWeek == -1) {
            throw Exception("startTimeDayOfWeek must be initialized with CYCLE_MODEL_WEEKLY")
        }

        //今年当月
        val startTime: Calendar = GregorianCalendar()
        val currentTimeMillis = startTime.timeInMillis

//        val testTime = System.currentTimeMillis()

//        println("当前时间1:$testTime  当前时间2:$currentTimeMillis 手动设置的时间 ${startTime.time}, millis${startTime.timeInMillis}")

        startTime.set(Calendar.HOUR_OF_DAY, startTimeHourOfDay)
        startTime.set(Calendar.MINUTE, startTimeMinute)
        startTime.set(Calendar.SECOND, 0)
        var time: Long = -1

        if (cycleModel == TaskConfig.CYCLE_MODEL_DAILY
            || cycleModel == TaskConfig.CYCLE_MODEL_NO_CYCLE
        ) {
            if (startTime.timeInMillis > currentTimeMillis) {
                //今天
                time = startTime.timeInMillis
            } else {
                //明天
                time = startTime.timeInMillis + 24 * 60 * 60 * 1000
            }
        } else if (cycleModel == CYCLE_MODEL_WEEKLY) {
            //第一天是sunday
            //获取今天在本周中是第几天
            val dayOfWeek = startTime.get(Calendar.DAY_OF_WEEK)

            startTime.set(Calendar.DAY_OF_WEEK, startTimeDayOfWeek)
            if (dayOfWeek == startTimeDayOfWeek) {
                if (startTime.timeInMillis > currentTimeMillis) {
                    //今天
                    time = startTime.timeInMillis
                } else {
                    //明天
                    time = startTime.timeInMillis + 24 * 60 * 60 * 1000
                }
            } else if (dayOfWeek > startTimeDayOfWeek) {
                time = startTime.timeInMillis + 7 * 24 * 60 * 60 * 1000
            } else if (dayOfWeek < startTimeDayOfWeek) {
                time = startTime.timeInMillis
            }
        }

        return time
    }

    /**
     * 默认是创建这个对象时的时间
     */
//    var startTimeMillis: Long = startTime.timeInMillis
//        set(value) {
//            startTime.timeInMillis = value
//        }

    companion object {

        //_APP_JUMP
        const val LIMIT_MODEL_LIGHT = 0

//        const val LIMIT_MODEL_LAUNCHER = 1

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