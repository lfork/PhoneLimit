package com.lfork.phonelimitadvanced.data.common.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoDao
import android.os.AsyncTask
import android.arch.persistence.room.Room
import android.content.Context


@Database(entities = arrayOf(AppInfo::class), version = 1)
abstract class LimitDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao

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
                            LimitDatabase::class.java, "limit_db"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }

        fun getDataBase() = INSTANCE!!
    }


    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     *
     * If you want to populate the database only when the database is created for the 1st time,
     * override RoomDatabase.Callback()#onCreate
     */
//    private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            // If you want to keep the data through app restarts,
//            // comment out the following line.
//            PopulateDbAsync(INSTANCE!!).execute()
//        }
//    }

    /**
     * Populate the database in the background.
     * If you want to start with more words, just add them.
     */
    private class PopulateDbAsync internal constructor(db: LimitDatabase) :
        AsyncTask<Void, Void, Void>() {

        private val mDao: AppInfoDao

        init {
            mDao = db.appInfoDao()
        }

        override fun doInBackground(vararg params: Void): Void? {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
//            mDao.deleteAll()
//
//            var word = Word("Hello")
//            mDao.insert(word)
//            word = Word("World")
//            mDao.insert(word)
            return null
        }
    }

}