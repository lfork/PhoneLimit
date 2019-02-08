package com.lfork.phonelimitadvanced.limit

import java.io.Serializable
import java.util.*

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:54
 */
data class LimitTaskConfig(
        var startTime: Calendar = GregorianCalendar(),
        var startTimeLong: Long = -1,
        var limitTimeSeconds: Long = -1,
        var periodMillis: Long = -1,
        var isImmediatelyExecuted: Boolean = false,
        var limitModel:Int = LimitTaskConfig.LIMIT_MODEL_HEAVY1
):Serializable{
    companion object {

        //_APP_JUMP
        const val LIMIT_MODEL_LIGHT = 0

        //_LAUNCHER_LIMIT
        const val LIMIT_MODEL_HEAVY1 = 1

        //_FLOATING_LIMIT
        const val LIMIT_MODEL_HEAVY2 = 2

        /**
         * 在小米等有可以关闭应用程序的手机助手APP的机型上适用
         * _LAUNCHER_FLOATING_MODEL
         */
        const val LIMIT_MODEL_ULTIMATE = 3

        const val LIMIT_MODEL_ROOT = 4
    }


}