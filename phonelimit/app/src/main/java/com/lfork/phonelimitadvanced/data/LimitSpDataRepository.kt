package com.lfork.phonelimitadvanced.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.limitcore.LimitModelType
import java.util.ArrayList

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 11:14
 */

fun Context.saveBio(bio:String) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putString("motto", bio)
        .apply()
}


fun Context.getBio() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getString(
    "motto",
    getString(R.string.default_motto)
)


fun Context.saveUsername(username:String) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putString("username", username)
        .apply()
}


fun Context.getUsername() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getString(
    "username",
    ""
)



fun Context.saveUserLoginStatus(isLogin:Boolean) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("is_login", isLogin)
        .apply()
}


fun Context.getUserLoginStatus() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getBoolean(
    "is_login",
    false
)



fun Context.saveUserAvatarIndex(avatarIndex:Int) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putInt("avatar_index", avatarIndex)
        .apply()
}


fun Context.getUserAvatarIndex() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getInt(
    "avatar_index",
    -1
)


fun Context.saveUserPassword(password:String) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putString("password", password)
        .apply()
}


fun Context.getUserPassword() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getString(
    "password",
    ""
)

fun Context.saveUserEmail(email:String) {
    getSharedPreferences("user_info", Context.MODE_PRIVATE)
        .edit()
        .putString("email", email)
        .apply()
}


fun Context.getUserEmail() = getSharedPreferences("user_info", Context.MODE_PRIVATE).getString(
    "email",
    ""
)



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