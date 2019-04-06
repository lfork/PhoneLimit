package com.lfork.phonelimit.base.widget

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.*
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.LimitApplication

import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_FLOATING
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_LIGHT
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_ROOT
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_ULTIMATE
import com.lfork.phonelimit.base.permission.*
import com.lfork.phonelimit.base.permission.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimit.base.permission.PermissionManager.isGrantedFloatingWindowPermission
import com.lfork.phonelimit.base.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimit.base.permission.PermissionManager.modelPermissionCheck
import com.lfork.phonelimit.utils.openDefaultAppsSetting
import kotlinx.android.synthetic.main.focus_limit_model_select_dialog.*

/**
 * Created by xian on 2017/2/28.
 */

class LimitModelSelectDialog(context: Context) : BaseDialog(context) {

    private lateinit var modelArray: Array<String>
    private lateinit var modelTipsArray: Array<String>
    private var itemPosition = 0
//    var permissionCheckerAndRequester: PermissionCheckerAndRequester? = null

    override fun setWidthScale(): Float {
        return 0.85f
    }

    override fun setEnterAnim(): AnimatorSet? {
        return null
    }

    override fun setExitAnim(): AnimatorSet? {
        return null
    }

   private val btnDefaultModelListener = {
        if (modelPermissionCheck(context,itemPosition)) {
            tv_default_model.text = "当前模式：${modelArray[itemPosition]}"
            LimitApplication.defaultLimitModel = itemPosition
            limitModelUpdateListener?.updateLimitModel(modelArray[itemPosition])
            ToastUtils.show("设置成功，当前模式为：${modelArray[itemPosition]}")
            dismiss()
        } else {
            ToastUtils.show("权限不足，请设置相应的权限")
        }
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            itemPosition = position
            permissionCheckUISettings(itemPosition)
//                tv_default_model.text = modelArray[position]
            tv_model_tips.text = modelTipsArray[position]

        }
    }

    override fun init() {
        itemPosition = LimitApplication.defaultLimitModel
        modelArray = context.resources.getStringArray(R.array.limit_model_array)
        modelTipsArray = context.resources.getStringArray(R.array.limit_model_tips_array)
        btn_set_default_model.setOnClickListener {
            btnDefaultModelListener.invoke() }

        btn_close.setOnClickListener { dismiss() }

        val spinner = sp_model_select
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.limit_model_array, android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter
        spinner.setSelection(itemPosition)
        permissionCheckUISettings(itemPosition)
        tv_default_model.text = "当前模式：${modelArray[itemPosition]}"
        tv_model_tips.text = modelTipsArray[itemPosition]

        spinner.onItemSelectedListener = itemSelectedListener

        layout_usage_permission.setOnClickListener {
           context.checkAndRequestUsagePermission()

        }

        layout_floating_permission.setOnClickListener {
            context.requestFloatingPermission()

        }

        layout_launcher_permission.setOnClickListener {
            if (!context.isDefaultLauncher()){
                context.requestLauncherPermission()
            } else{
                context.openDefaultAppsSetting()
            }
        }

        layout_root_permission.setOnClickListener {
            context.requestRootPermission()

        }

    }


    fun permissionCheckUISettings(model: Int) {
        layout_floating_permission.visibility = View.GONE
        layout_launcher_permission.visibility = View.GONE
        layout_root_permission.visibility = View.GONE

        when (model) {
            LIMIT_MODEL_LIGHT -> {
            }
            LIMIT_MODEL_FLOATING -> {
                layout_floating_permission.visibility = View.VISIBLE
            }
            LIMIT_MODEL_ULTIMATE -> {
                layout_floating_permission.visibility = View.VISIBLE
                layout_launcher_permission.visibility = View.VISIBLE
            }
            LIMIT_MODEL_ROOT -> {
                layout_root_permission.visibility = View.VISIBLE
            }
        }

        if (context.isGrantedStatAccessPermission()) {
            iv_usage_permission_check.setImageResource(R.drawable.ic_check_24dp)
        } else {
            iv_usage_permission_check.setImageResource(R.drawable.ic_red_close_24dp)
        }


        if (context.isGrantedFloatingWindowPermission()) {
            iv_floating_permission_check.setImageResource(R.drawable.ic_check_24dp)
        } else {
            iv_floating_permission_check.setImageResource(R.drawable.ic_red_close_24dp)
        }

        if (context.isDefaultLauncher()) {
            iv_launcher_permission_check.setImageResource(R.drawable.ic_check_24dp)
        } else {
            iv_launcher_permission_check.setImageResource(R.drawable.ic_red_close_24dp)
        }

        if (LimitApplication.isRooted) {
            iv_root_permission_check.setImageResource(R.drawable.ic_check_24dp)
        } else {
            iv_root_permission_check.setImageResource(R.drawable.ic_red_close_24dp)
        }
    }


    override fun getContentViewId(): Int {
        return R.layout.focus_limit_model_select_dialog
    }




    var limitModelUpdateListener: LimitModelUpdateListener? = null


    interface LimitModelUpdateListener {
        fun updateLimitModel(model: String)
    }


    override fun dismiss() {
        super.dismiss()
        limitModelUpdateListener = null
    }

    override fun onResume() {
        super.onResume()

        permissionCheckUISettings(itemPosition)
    }
}
