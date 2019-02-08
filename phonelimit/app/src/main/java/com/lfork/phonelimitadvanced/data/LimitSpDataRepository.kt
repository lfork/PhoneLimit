package com.lfork.phonelimitadvanced.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import java.util.ArrayList

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

fun Context.isFirstOpen(): Boolean {
    val setting = getSharedPreferences("env", Context.MODE_PRIVATE)
    val isFirst = setting.getBoolean("FIRST", true)
    return if (isFirst) {
        setting.edit().putBoolean("FIRST", false).apply()
        true
    } else {
        false
    }
}


fun Context.getSpLauncherApps(): List<String>? {

    val tempSet = getSharedPreferences(
        "LimitStatus",
        Context.MODE_PRIVATE
    ).getStringSet("launchers", null)


    if (tempSet != null) {
        val tempArray = ArrayList<String>()
        tempSet.iterator().forEach {
            tempArray.add(it)
        }
        Log.d(
            LimitApplication.TAG,
            "Activities $tempArray"
        )
        return tempArray
    }

    return null
}

fun Context.saveLauncherApps(launchers: List<String>) {
    getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).edit()
        .putStringSet("launchers", launchers.toSet()).apply()

}