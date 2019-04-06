package com.lfork.phonelimit.utils

import android.app.Activity
import android.view.View


/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 21:02
 */
fun Activity.hideNavigationBar() {
    val decorView = getWindow().getDecorView()
    val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    decorView.setSystemUiVisibility(uiOptions)
}

fun Activity.showNavigationBar() {
    val decorView = getWindow().getDecorView()
    val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
    decorView.setSystemUiVisibility(uiOptions)
}

fun Activity.setStatusBarVisible(show: Boolean) {
    if (show) {
        var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiFlags = uiFlags or 0x00001000
        getWindow().getDecorView().setSystemUiVisibility(uiFlags)
    } else {
        var uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        uiFlags = uiFlags or 0x00001000
        getWindow().getDecorView().setSystemUiVisibility(uiFlags)
    }
}

fun Activity.setSystemUIVisible(show: Boolean) {
    if (show) {
        var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiFlags = uiFlags or 0x00001000
        getWindow().getDecorView().setSystemUiVisibility(uiFlags)
    } else {
        var uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        uiFlags = uiFlags or 0x00001000
        getWindow().getDecorView().setSystemUiVisibility(uiFlags)
    }
}