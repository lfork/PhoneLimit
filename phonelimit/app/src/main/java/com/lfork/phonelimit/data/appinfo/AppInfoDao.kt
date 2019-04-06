package com.lfork.phonelimit.data.appinfo

import android.arch.persistence.room.*

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM app_info")
    fun getAll(): List<AppInfo>

    @Query("SELECT * FROM app_info where package_name=:packageNameKey")
    fun getAppInfo(packageNameKey:String?): AppInfo?

    //SQLite 没有单独的 Boolean 存储类。相反，布尔值被存储为整数 0（false）和 1（true）。
    @Query("SELECT * FROM app_info WHERE is_in_white_name_list=1")
    fun getWhiteNameApps(): List<AppInfo>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    //如果返回值是0，那么就insert
    @Update
    fun update(vararg appInfo: AppInfo):Int


    @Insert
    fun insert(appInfo: AppInfo)


    @Delete
    fun delete(user: AppInfo)



}