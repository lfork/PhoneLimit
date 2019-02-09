package com.lfork.phonelimitadvanced.base.widget

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.TextView

import com.lfork.phonelimitadvanced.R

/**
 * Created by xian on 2017/2/28.
 */

class UsagePermissionDialog(context: Context?) : PermissionDialog(context) {
    override fun getContentViewId(): Int {
        return R.layout.dialog_access_usage_permission
    }
}
