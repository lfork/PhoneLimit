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
        var limitTimeSeconds: Long = -1,
        var periodMillis: Long = -1,
        var isImmediatelyExecuted: Boolean = false,
        var limitModel:Int = LimitTaskConfig.LIMIT_MODEL_HEAVY_LAUNCHER_LIMIT
):Serializable{
    companion object {
        const val LIMIT_MODEL_LIGHT_APP_JUMP = 0

        const val LIMIT_MODEL_HEAVY_LAUNCHER_LIMIT = 1

        /**
         * 在小米等有可以关闭应用程序的手机助手APP的机型上适用
         */
        const val LIMIT_MODEL_ULTIMATE_LAUNCHER_FLOATING_MODEL = 2

        const val LIMIT_MODEL_ROOT_MODEL = 3
    }


}