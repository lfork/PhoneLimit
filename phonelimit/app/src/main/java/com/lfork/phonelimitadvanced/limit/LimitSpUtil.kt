package com.lfork.phonelimitadvanced.limit

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 11:14
 */

fun Context.saveRemainTime(remainTimeSeconds: Long) {
    val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putLong("remain_time_seconds", remainTimeSeconds)
    editor.apply()
}

fun Context.saveStartTime(startTime: Long) {
    val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putLong("start_time", startTime)
    editor.apply()
}

fun Context.clearStartTime() {
    val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.remove("start_time")
    editor.apply()
}