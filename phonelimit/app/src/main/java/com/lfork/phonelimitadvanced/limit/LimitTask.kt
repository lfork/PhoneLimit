package com.lfork.phonelimitadvanced.limit

import android.content.Context

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/06 16:55
 */
interface LimitTask {

    fun initLimit(context: Context)

    /**
     * @return true表示限制执行成功 ，false表示不限制
     */
    fun doLimit():Boolean

    fun closeLimit()

//    /**
//     * 验证任务的时间是否有效，没有的话就放弃执行
//     */
//    fun taskIsValid()

}