package com.lfork.phonelimit.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.support.v4.app.Fragment


/**
 *
 * Created by 98620 on 2018/12/2.
 */

fun Context.openDefaultAppsSetting() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        startActivity(intent)
    } else {
        val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
        startActivity(intent)
    }
}


fun Fragment.openDefaultAppsSetting() {
    context?.openDefaultAppsSetting()
}

