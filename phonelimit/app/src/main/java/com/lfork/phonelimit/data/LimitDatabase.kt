package com.lfork.phonelimit.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.lfork.phonelimit.data.appinfo.AppInfo
import android.arch.persistence.room.Room
import android.content.Context
import com.lfork.phonelimit.data.appinfo.AppInfoDao
import com.lfork.phonelimit.data.taskconfig.TaskConfig
import com.lfork.phonelimit.data.taskconfig.TaskConfigDao
import com.lfork.phonelimit.data.urlinfo.UrlInfoDao
import com.lfork.phonelimit.data.urlinfo.UrlInfo


@Database(entities = [AppInfo::class, UrlInfo::class,TaskConfig::class], version = 6)
abstract class LimitDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao

    abstract fun urlInfoDao(): UrlInfoDao

    abstract fun taskConfigDao(): TaskConfigDao

    companion object {
        // marking the instance as volatile to ensure atomic access to the variable
        @Volatile
        private var INSTANCE: LimitDatabase? = null

        /**
         * this function should be executed in application initialization
         */
        fun initDataBase(context: Context): LimitDatabase? {
            if (INSTANCE == null) {
                synchronized(LimitDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context,
                                LimitDatabase::class.java, "limit_db")
                                //添加下面这一行 ,强制升级数据库
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        fun getDataBase() = INSTANCE!!
    }
}