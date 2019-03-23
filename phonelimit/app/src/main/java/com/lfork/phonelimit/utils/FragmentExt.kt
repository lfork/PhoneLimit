package com.lfork.phonelimit.utils

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Build
import android.support.v4.app.Fragment

/**
 *
 * Created by 98620 on 2018/12/3.
 */

fun Fragment.runOnUiThread(action: () -> Unit) {
    activity?.runOnUiThread(action)
}

fun Fragment.bindService(
    service: Intent, conn: ServiceConnection,
    flags: Int
): Boolean {
    return activity!!.bindService(service, conn, flags)
}

fun Fragment.startService(service: Intent): ComponentName? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        return activity!!.startForegroundService(service)
    } else{
        return activity!!.startService(service)
    }


}

fun Fragment.unbindService(conn: ServiceConnection) {
    return activity!!.unbindService(conn)
}

fun Fragment.stopService(intent: Intent): Boolean {
    return activity!!.stopService(intent)
}

fun Fragment.getSharedPreferences(name: String, mode: Int): SharedPreferences {
    return activity!!.getSharedPreferences(name, mode)
}
