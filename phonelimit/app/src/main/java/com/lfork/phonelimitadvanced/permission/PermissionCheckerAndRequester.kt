package com.lfork.phonelimitadvanced.permission

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/09 10:57
 */
interface PermissionCheckerAndRequester {
//    fun requestUsagePermission(): Boolean

    fun requestFloatingPermission():Boolean

    fun requestLauncherPermission():Boolean

    fun requestRootPermission():Boolean
}