package com.lfork.phonelimit.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 *
 * Created by 98620 on 2018/12/7.
 */
/**
 * 根据包名和类名获取App的icon
 *
 * @param pkgName   包名
 * @param className 类名
 */
//fun getAppIcon(context: Context, pkgName: String, className: String): Drawable {
//    // 查询某个包名下的应用个数
//    val findList = MainDbHelper.getInstance().getAppContentDao().findAppsFromPackage(pkgName)
//    val drawable: Drawable
//    if (findList != null) {
//        if (findList!!.size == 1) {
//            drawable = getAppIcon(context, pkgName)
//            return drawable
//        } else if (findList!!.size > 1) {
//            val pm = context.packageManager
//            val resolveInfos = GetLaunchAppTool.getInstence(context).getLaunchApp()
//            if (resolveInfos != null && resolveInfos!!.size > 0) {
//                for (resolveInfo in resolveInfos!!) {
//
//                    val pkg = resolveInfo.activityInfo.packageName
//                    if (!TextUtils.isEmpty(pkg) && pkg == pkgName) {
//
//                        val clsName = resolveInfo.activityInfo.appName
//                        if (clsName == className) {
//                            drawable = resolveInfo.loadIcon(pm)
//                            return drawable
//                        }
//                    }
//                }
//            }
//        }
//    }

//    drawable = getAppIcon(context, pkgName)
//    return drawable
//}


/**
 * 根据包名获取App的Icon
 *
 * @param pkgName 包名
 */
fun getAppIcon(context: Context, pkgName: String?): Drawable? {
    try {
        if (null != pkgName) {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(pkgName, 0)
            return info.loadIcon(pm)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

/**
 * 根据包名获取App的名字
 *
 * @param pkgName 包名
 */
fun getAppName(context: Context, pkgName: String): String {
    val pm = context.packageManager
    try {
        val info = pm.getApplicationInfo(pkgName, 0)
        return info.loadLabel(pm).toString()

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return ""
}