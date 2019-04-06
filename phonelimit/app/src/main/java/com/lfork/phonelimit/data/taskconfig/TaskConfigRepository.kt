package com.lfork.phonelimit.data.taskconfig

import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.LimitDatabase
import java.util.*

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/10 14:00
 */
object TaskConfigRepository {

    var cacheTasks :ArrayList<TaskConfig>?=null

    private val taskConfigDao = LimitDatabase.getDataBase().taskConfigDao()

    fun addTask(taskConfig: TaskConfig):Boolean {

        if (cacheTasks?.size?:0>=5){
            return false
        }

        if (taskConfigDao.insert(taskConfig)>0){
            return true
        }

        return false
    }

    fun updateLimitTask(taskConfig: TaskConfig):Int {
        return taskConfigDao.update(taskConfig)
//        val target = cacheTasks.find { it.id == taskConfig.id }
    }

    fun updateLimitTask(taskConfig: TaskConfig,callback: DataCallback<String>) {
        LimitApplication.executeAsyncDataTask {
            if (taskConfigDao.update(taskConfig)> 0){
                callback.succeed("更新成功")
            } else{
                callback.failed(-1,"更新失败")
            }
        }

//        val target = cacheTasks.find { it.id == taskConfig.id }
    }

    fun deleteTask(taskConfig: TaskConfig) {
        cacheTasks?.remove(taskConfig)
        taskConfigDao.delete(taskConfig)
//        val target = cacheTasks.find { it.id == taskConfig.id }
//        cacheTasks.remove(target)
    }

    fun getTasks(): ArrayList<TaskConfig> {
        cacheTasks = taskConfigDao.getAll() as ArrayList<TaskConfig>
        return  cacheTasks!!
    }

    fun getTask(id:String):TaskConfig?{
        return taskConfigDao.getTaskConfig(id)
    }
}