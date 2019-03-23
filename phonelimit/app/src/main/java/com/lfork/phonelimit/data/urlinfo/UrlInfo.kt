package com.lfork.phonelimit.data.urlinfo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 *
 * Created by 98620 on 2018/12/16.
 */
@Entity(tableName = "url_info")
data class UrlInfo @Ignore constructor(
        @PrimaryKey @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "name") var name: String? = null,
        @ColumnInfo(name = "is_active") var isActive: Boolean = true)
    : Comparable<UrlInfo> {

    constructor() : this("", null, false)

    override fun compareTo(other: UrlInfo): Int {
        return url.compareTo(other.url)
    }

}