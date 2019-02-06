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

    fun doLimit()

    fun closeLimit()

}