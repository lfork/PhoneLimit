package com.lfork.phonelimitadvanced.base

/**
 *
 * Created by 98620 on 2018/10/30.
 */
interface CallBack<T> {
    fun succeed(result: T)

    fun failed(log:String)
}