package com.lfork.phonelimitadvanced.data.limittask

import com.lfork.phonelimitadvanced.limit.LimitTaskConfig

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/10 14:00
 */
object LimitTaskRepository {

    private val cacheTasks = ArrayList<LimitTaskConfig>()

    fun addTask(taskConfig: LimitTaskConfig) {
        cacheTasks.add(taskConfig)
    }

    fun updateLimitTask(taskConfig: LimitTaskConfig) {
        //在内存里面是已经更新了的
    }

    fun deleteTask(limitTaskConfig: LimitTaskConfig) {

        val target = cacheTasks.find { it.id == limitTaskConfig.id }
        cacheTasks.remove(target)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            cacheTasks.removeIf { it.id == limitTaskConfig.id }
//        } else {
//
//        }

    }

    fun getTasks(): ArrayList<LimitTaskConfig> {
        return cacheTasks
    }
}