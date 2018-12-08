package com.lfork.phonelimitadvanced.data.appinfo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.drawable.Drawable
import org.litepal.crud.DataSupport

/**
 *
 * @author 98620
 * @date 2018/12/7
 */
@Entity(tableName = "app_info")
data class AppInfo(
    @ColumnInfo(name = "app_name") var appName: String?,
    @PrimaryKey @ColumnInfo(name = "package_name") var packageName: String,
    @ColumnInfo(name = "is_in_white_name_list") var isInWhiteNameList: Boolean
)  {

    @Ignore
    var icon: Drawable? = null

    constructor() : this(null, "", false)

    constructor(appName: String?, packageName: String, appIcon: Drawable?) : this(
        appName,
        packageName,
        false
    ) {
        icon = appIcon
    }
}
