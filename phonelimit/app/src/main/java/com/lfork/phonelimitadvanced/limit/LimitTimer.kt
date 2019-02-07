package com.lfork.phonelimitadvanced.limit

import java.lang.Exception

/**
 * 倒计时的计时器,只能使用一次。
 * Created by 98620 on 2018/12/14.
 */
class LimitTimer(private val timeSeconds: Long, private val listener: TimeListener, private var starTimeMillis: Long) {

    private var remainTimeSeconds: Long = timeSeconds

    /**
     * 计时器是否已经激活
     */
    private var isActive = false

    /**
     * 表示生命周期结束了，这个计时器不可用了
     */
    private var isEnd = false


    private lateinit var timerThread: Thread

    /**
     * started succeed->true  , else false
     */
    fun start(): Boolean {
        if (isEnd) {
            throw TimerEndException()
        }

        if (isActive) {
            return false
        }
        isActive = true

        val timerTask = Runnable {

            try {
                while (remainTimeSeconds > 0) {
                    listener.onRemainTimeRefreshed(remainTimeSeconds)
                    Thread.sleep(1000)
                    remainTimeSeconds = timeSeconds - (System.currentTimeMillis() - starTimeMillis) / 1000
                }
                listener.onCompleted()
            } catch (e: Exception) {
                listener.onClosedInAdvance(remainTimeSeconds)
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
     * 关闭计时器（可以提前关闭）
     */
    fun close() {

        if (isEnd) {
            throw TimerEndException()
        }

        if (!isActive) {
            return
        }
        timerThread.interrupt()
    }

    interface TimeListener {

        /**
         * 提前被关闭
         */
        fun onClosedInAdvance(remainTimeSeconds: Long)

        fun onCompleted()

        fun onRemainTimeRefreshed(remainTimeSeconds: Long)

    }

    class TimerEndException : Exception() {
        override val message: String?
            get() = super.message + "计时器声明周期已经结束了，不能再用了"
    }
}