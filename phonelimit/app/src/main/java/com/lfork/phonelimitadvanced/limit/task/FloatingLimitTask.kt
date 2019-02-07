package com.lfork.phonelimitadvanced.limit.task

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.MainHandler

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:35
 *
 * 只做悬浮窗
 *
 */
class FloatingLimitTask : BaseLimitTask() {

    private var wmParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null
    private var mWindowView: View? = null
    private var remainTimeTV: TextView? = null
//
//    private val handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            val data = msg.data.getString("data")
//            remainTimeTV?.setText(data)
//            mWindowManager?.updateViewLayout(mWindowView, wmParams)
//        }
//    }

    override fun initLimit(context: Context) {
        super.initLimit(context)
        mContext = context
        initWindowParams()
        initView()
//        addWindowView()
    }

    private fun initWindowParams() {
        if (mContext == null) {
            return
        }

        mWindowManager = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wmParams = WindowManager.LayoutParams()
        // 更多type：https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#TYPE_PHONE
        //        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams?.let {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                it.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                it.type = WindowManager.LayoutParams.TYPE_PHONE
            }

            it.format = PixelFormat.TRANSLUCENT
            // 更多falgs:https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_NOT_FOCUSABLE
            //wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            it.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            it.gravity = Gravity.START or Gravity.TOP
            it.width = WindowManager.LayoutParams.MATCH_PARENT
            it.height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    private fun initView() {
        mWindowView = LayoutInflater.from(mContext).inflate(R.layout.item_window_floating, null)
        remainTimeTV = mWindowView?.findViewById(R.id.remainTime)
    }

    var viewIsAdded = false

    @Synchronized
    private fun addWindowView() {
        if (viewIsAdded) {
            return
        }
        viewIsAdded = true
        MainHandler.getInstance().post {
            mWindowManager?.addView(mWindowView, wmParams)
        }

    }

    private fun removeWindow() {
        MainHandler.getInstance().post {
            mWindowManager?.removeView(mWindowView)
        }
    }

    override fun doLimit() {
        addWindowView()
        super.doLimit()
    }

    override fun closeLimit() {
        removeWindow()
        mContext = null
    }

}