package com.lfork.phonelimitadvanced.limitcore.task

 import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.AppConstants
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.MainHandler
import com.lfork.phonelimitadvanced.utils.Constants


/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/07 17:35
 *
 * 只做悬浮窗
 *
 */
open class FloatingLimitTask : BaseLimitTask(), RecentlyReceiver.SystemKeyListener {


    companion object {
        var isOnRecentApps = false
    }

    private var wmParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null
    private var mWindowView: View? = null
    private var tips: TextView? = null


    var mReceiver: RecentlyReceiver? = null


    override fun initLimit(context: Context) {
        super.initLimit(context)
        mContext = context
        initWindowParams()
        initView()
        mReceiver = RecentlyReceiver()
        mReceiver?.registerKeyListener(this)
        mContext!!.registerReceiver(mReceiver, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
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
            it.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            it.gravity = Gravity.START or Gravity.TOP
            it.width = WindowManager.LayoutParams.MATCH_PARENT
            it.height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    private fun initView() {
        mWindowView = LayoutInflater.from(mContext).inflate(R.layout.item_window_tips, null)
        tips = mWindowView?.findViewById(R.id.tv_windows_tips)
        tips?.setOnClickListener {
            val intent = Intent(mContext!!, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
        }
    }

    var viewIsAdded = false

    @Synchronized
    private fun addWindowView() {

        if (mWindowView!!.isAttachedToWindow) {
            return
        }
        if (viewIsAdded) {
            return
        }
        viewIsAdded = true
        MainHandler.getInstance().post {
            if (mWindowView!!.isAttachedToWindow) {
                return@post
            }
            mWindowManager?.addView(mWindowView, wmParams)
        }

    }

    private fun removeWindow() {
        if (viewIsAdded) {
            MainHandler.getInstance().post {
                if (mWindowView!!.isAttachedToWindow) {
                    mWindowManager?.removeView(mWindowView)
                    viewIsAdded = false
                }
            }
        }
    }

    @Synchronized
    override fun doLimit(): Boolean {


        if (mContext == null) {
            return false
        }

        //获取栈顶app的包名
        val packageName = getTopRunningApp(
            mContext!!,
            mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        )

        if (packageName.isEmpty()) {
            return false
        }

        Log.d("当前包名", packageName + "  ")


        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (AppInfoRepository.whiteNameList.contains(packageName) || Constants.SPECIAL_WHITE_NAME_LIST.contains(
                packageName
            )
        ) {
            if (!isOnRecentApps){
                removeWindow()
            }
            return false
        }
        addWindowView()
        val intent = Intent(mContext!!, MainActivity::class.java)
        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName)
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext!!.startActivity(intent)
        return true
    }


    override fun onRecentAppsClicked() {
        isOnRecentApps = true
        addWindowView()
    }

    override fun onHomeKeyClicked() {
        isOnRecentApps = true
        addWindowView()
    }


    override fun closeLimit() {
        mContext?.unregisterReceiver(mReceiver)
        mReceiver?.unregisterKeyListener()
        removeWindow()
        mContext = null
    }

}