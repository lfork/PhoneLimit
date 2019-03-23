package com.lfork.phonelimit.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast

/**
 * Created by 98620 on 2018/4/14.
 */


object ToastUtil {

    val MAIN_THREAD = "main"

    fun showLong(context: Context?, content: String) {
        if (MAIN_THREAD != Thread.currentThread().name) {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
        }
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    fun showShort(context: Context?, content: String) {
        //不能在非主线程里面直接Toast   Can't create handler inside thread that has not called Looper.prepare()
        if (MAIN_THREAD != Thread.currentThread().name) {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
        }
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun Context.showLongTips(content: String){
        //不能在非主线程里面直接Toast   Can't create handler inside thread that has not called Looper.prepare()
        if (MAIN_THREAD != Thread.currentThread().name) {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
        }
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

}
