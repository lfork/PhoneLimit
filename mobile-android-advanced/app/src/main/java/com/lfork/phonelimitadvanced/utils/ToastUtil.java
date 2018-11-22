package com.lfork.phonelimitadvanced.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by 98620 on 2018/4/14.
 */


public class ToastUtil {

    public final static String MAIN_THREAD = "main";

    public static void showLong(Context context, String content){
        if (!MAIN_THREAD.equals(Thread.currentThread().getName())) {
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
        }
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, String content){
        //不能在非主线程里面直接Toast   Can't create handler inside thread that has not called Looper.prepare()
        if (!MAIN_THREAD.equals(Thread.currentThread().getName())) {
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
        }
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
