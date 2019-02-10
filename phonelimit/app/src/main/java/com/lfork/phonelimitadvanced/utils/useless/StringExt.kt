package com.lfork.phonelimitadvanced.utils.useless

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/09 19:27
 */
fun secondsToFormatStr(timeSeconds: Long): String =
    when {
        timeSeconds > 60 * 60 ->
            "${timeSeconds / 3600}小时${(timeSeconds % 3600) / 60}分${timeSeconds % 60}秒"
        timeSeconds > 60 ->
            "${timeSeconds / 60}分${timeSeconds % 60}秒"
        else -> "${timeSeconds}秒"
    }


fun millisToMinutesStr(timeMillis: Long)=(timeMillis/1000/60).toString()