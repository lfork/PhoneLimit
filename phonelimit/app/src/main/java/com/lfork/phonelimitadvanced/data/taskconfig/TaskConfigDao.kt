package com.lfork.phonelimitadvanced.data.taskconfig

import android.arch.persistence.room.*
import java.util.*

@Dao
interface TaskConfigDao {
    @Query("SELECT * FROM taskConfig")
    fun getAll(): List<TaskConfig>

    @Query("SELECT * FROM taskConfig where id=:id")
    fun getTaskConfig(id:String): TaskConfig?

    //SQLite 没有单独的 Boolean 存储类。相反，布尔值被存储为整数 0（false）和 1（true）。
//    @Query("SELECT * FROM taskConfig WHERE is_in_white_name_list=1")
//    fun getWhiteNameApps(): List<TaskConfig>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    //如果返回值是0，那么就insert
    @Update
    fun update(vararg TaskConfig: TaskConfig):Int


    @Insert
    fun insert(TaskConfig: TaskConfig):Long


    @Delete
    fun delete(user: TaskConfig):Int

}