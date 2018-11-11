package com.lfork.phonelimitadvanced.limit

/**
 *
 * Created by 98620 on 2018/11/11.
 */
interface LimitStateListener {

    fun remainTimeRefreshed(timeSeconds:Long)

    fun autoUnlocked(msg:String)

    fun forceUnlocked(msg:String)

    fun onLimitStarted()

    fun onLimitFinished()
}