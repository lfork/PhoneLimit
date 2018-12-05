package com.lfork.phonelimitadvanced;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 98620 on 2018/10/30.
 */
public class Test {
    private void setAirPlaneMode(boolean enable) {
        int mode = enable ? 1 : 0;
        String cmd = "settings put global airplane_mode_on " + mode;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



    /**
     * Return PackageManager.
     *
     * @param context A Context of the application package implementing this class.
     * @return a PackageManager instance.
     */
    public static ActivityManager getActivityManager(Context context){
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    }

    /**
     * Returns a list of launcher that are running on the device.
     *
     * @param context A Context of the application package implementing this class.
     * @return A list which contains all the launcher package name.If there are no launcher, an empty
     *         list is returned.
     */
    public static List<String> getLaunchers(Context context){
        List<String> packageNames = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for(ResolveInfo resolveInfo:resolveInfos){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if(activityInfo != null) {
                packageNames.add(resolveInfo.activityInfo.processName);
                packageNames.add(resolveInfo.activityInfo.packageName);
            }
        }
        return packageNames;
    }

    /**
     * Returns whether the launcher which running on the device is importance foreground.
     *
     * @param context A Context of the application package implementing this class.
     * @return True if the importance of the launcher process is {@link android.app.ActivityManager.RunningAppProcessInfo#IMPORTANCE_FOREGROUND}.
     *
     *
     */
    public static boolean isLauncherForeground(Context context){
        boolean isLauncherForeground = false;
        ActivityManager activityManager = getActivityManager(context);
        List<String> lanuchers = getLaunchers(context);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos =  activityManager.getRunningTasks(1);

        if(lanuchers.contains(runningTaskInfos.get(0).baseActivity.getPackageName())) {
            isLauncherForeground = true;
        }

        return isLauncherForeground;
    }

}
