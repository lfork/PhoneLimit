package com.lfork.phonelimitadvanced.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.limitcore.LimitModelType
import java.util.ArrayList

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 11:14
 */


fun Context.saveSettingsIndexTipsSwitch(isOpen: Boolean) {
    getSharedPreferences("settings", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("isOpen", isOpen)
        .apply()
}


fun Context.getSettingsIndexTipsSwitch() = getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(
    "isOpen",
    true
)


fun Context.saveRootStatus(isRooted: Boolean) {
    getSharedPreferences("env", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("isRooted", isRooted)
        .apply()
}


fun Context.getRootStatus() = getSharedPreferences("env", Context.MODE_PRIVATE).getBoolean(
    "isRooted",
    false
)


fun Context.saveRemainTime(remainTimeSeconds: Long) {
    val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putLong("remain_time_seconds", remainTimeSeconds)
    editor.apply()
}


fun Context.getRemainTime() = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).getLong(
    "remain_time_seconds",
    0
)

fun Context.saveStartTime(startTime: Long) {
    getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        .edit()
        .putLong("start_time", startTime)
        .apply()
}

fun Context.getStartTime() = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).getLong(
    "start_time",
    0
)

fun Context.saveLimitModel(limitModel: Int) {
    getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        .edit()
        .putInt("limit_model", limitModel)
        .apply()
}

fun Context.getLimitModel() = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE).getInt(
    "limit_model",
    0
)

fun Context.saveDefaultLimitModel(@LimitModelType limitModel: Int) {
    getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        .edit()
        .putInt("limit_model", limitModel)
        .apply()
}


fun Context.getDefaultLimitModel() =
    getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        .getInt(
            "limit_model",
            0
        )


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