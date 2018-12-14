package com.lfork.phonelimitadvanced.limit

import java.lang.Exception

/**
 * 倒计时的计时器,只能使用一次。
 * Created by 98620 on 2018/12/14.
 */
class Timer(val timeSeconds:Long, private val listener:TimeListener) {

    private var remainTimeSeconds:Long = timeSeconds

    private var isActive = false

    /**
     * 表示生命周期结束了，这个计时器不可用了
     */
   var isEnd = false


    private lateinit var timerThread:Thread

    /**
     * started succeed->true  , else false
     */
    fun start():Boolean{
        if (isEnd){
            throw TimerEndException()
        }

        if (isActive) {
            return false
        }
        isActive = true

        val timerTask = Runnable {

            try {
                while (remainTimeSeconds > 0){
                    listener.onRemainTimeRefreshed(remainTimeSeconds)
                    Thread.sleep(1000)
                    remainTimeSeconds--
                }
                listener.onCompleted()
            } catch (e:Exception){
                listener.onInterpreted(remainTimeSeconds)
            }

            isActive = false
            isEnd = true

        }
        timerThread = Thread(timerTask)
        timerThread.name = "倒计时线程" + System.currentTimeMillis()
        timerThread.start()

        return true
    }

    /**
     * 打断计时器，提前结束
     */
    fun interrupt() {

        if (isEnd){
            throw TimerEndException()
        }

        if (!isActive) {
            return
        }
        timerThread.interrupt()
    }

    interface TimeListener{

        fun onInterpreted(remainTimeSeconds:Long)

        fun onCompleted()

        fun onRemainTimeRefreshed(remainTimeSeconds:Long)

    }

    class TimerEndException : Exception() {
        override val message: String?
            get() = super.message +  "计时器声明周期已经结束了，不能再用了"
    }
}