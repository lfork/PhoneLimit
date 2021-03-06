package com.lfork.phonelimit.data.appinfo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.drawable.Drawable

/**
 *
 * @author 98620
 * @date 2018/12/7
 */
@Entity(tableName = "app_info")
data class AppInfo @Ignore constructor (
    @ColumnInfo(name = "app_name") var appName: String?,
    @PrimaryKey @ColumnInfo(name = "package_name") var packageName: String,
    @ColumnInfo(name = "is_in_white_name_list") var isInWhiteNameList: Boolean
) :Comparable<AppInfo> {

    override fun compareTo(other: AppInfo): Int {
        return appName!!.compareTo(other.appName!!)
    }

    @Ignore
    var icon: Drawable? = null

    constructor() : this(null, "", false)

    @Ignore
    constructor(appName: String?, packageName: String, appIcon: Drawable?) : this(
        appName,
        packageName,
        false
    ) {
        icon = appIcon
    }
}
