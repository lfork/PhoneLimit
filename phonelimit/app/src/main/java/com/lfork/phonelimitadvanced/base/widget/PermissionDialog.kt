package com.lfork.phonelimitadvanced.base.widget

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.TextView

import com.lfork.phonelimitadvanced.R

/**
 * Created by xian on 2017/2/28.
 */

abstract class PermissionDialog(context: Context?) : BaseDialog(context) {

    private var mBtnPermission: TextView? = null
    private var mOnClickListener: (() -> Unit?)? = null


    override fun setWidthScale(): Float {
        return 0.9f
    }

    override fun setEnterAnim(): AnimatorSet? {
        return null
    }

    override fun setExitAnim(): AnimatorSet? {
        return null
    }

    override fun init() {
        mBtnPermission = findViewById<View>(R.id.btn_permission) as TextView
        mBtnPermission!!.setOnClickListener { view ->
            if (mOnClickListener != null) {
                dismiss()
                mOnClickListener!!.invoke()
            }
        }
    }

    fun setOnClickListener(OnClickListener: () -> Unit) {
        mOnClickListener = OnClickListener
    }


    override fun getContentViewId(): Int {
        return R.layout.permission_access_usage_dialog
    }

}
