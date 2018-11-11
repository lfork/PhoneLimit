package com.lfork.phonelimitadvanced.util

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

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