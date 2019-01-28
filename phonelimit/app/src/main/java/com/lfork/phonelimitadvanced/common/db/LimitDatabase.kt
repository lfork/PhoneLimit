package com.lfork.phonelimitadvanced.common.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import android.arch.persistence.room.Room
import android.content.Context
import com.lfork.phonelimitadvanced.data.urlinfo.UrlInfo


@Database(entities = [AppInfo::class, UrlInfo::class], version = 2)
abstract class LimitDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao
    abstract fun urlInfoDao(): UrlInfoDao

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
                                //添加下面这一行
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