package com.lfork.phonelimitadvanced.utils.useless

import android.content.Context
import android.net.wifi.WifiManager

/**
 *
 * Created by 98620 on 2018/11/11.
 */
class WIFIUtil(context: Context) {
    // 定义WifiManager对象
    private var mWifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // 打开WIFI
    fun openWifi() {
        if (!mWifiManager.isWifiEnabled) {
            mWifiManager.isWifiEnabled = true
        }
    }

    // 断开当前网络
    fun disconnectWifi() {
        if (!mWifiManager.isWifiEnabled) {
            mWifiManager.disconnect()
        }
    }

    // 关闭WIFI
    fun closeWifi() {
        if (mWifiManager.isWifiEnabled) {
            mWifiManager.isWifiEnabled = false
        }
    }

    // 检查当前WIFI状态
    fun checkState(): Int {
        return mWifiManager.wifiState
    }

}