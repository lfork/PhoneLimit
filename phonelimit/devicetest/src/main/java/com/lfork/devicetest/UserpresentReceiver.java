package com.lfork.devicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserpresentReceiver extends BroadcastReceiver {
    private String TAG="UserpresentReceiver";  

    @Override  
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();  
        Log.e(TAG, "action="+action);
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {  
            Log.e(TAG, "竟然可以解锁");  
        }  
    }  
}  