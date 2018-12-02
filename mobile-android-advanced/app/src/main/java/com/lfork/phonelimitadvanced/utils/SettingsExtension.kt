package com.lfork.phonelimitadvanced.utils

import android.content.Context
import android.support.v4.content.ContextCompat.startActivity
import android.provider.Settings.ACTION_ADD_ACCOUNT
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText


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

