package com.lfork.phonelimitadvanced.utils.useless

import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 *
 * Created by 98620 on 2018/11/11.
 */
object SystemToggle {

//    fun Context.openAirModeSettings() {
//        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
//        startActivity(intent)
//    }

    fun openAirModeSettings(context: Context) {
        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        context.startActivity(intent)
    }
}