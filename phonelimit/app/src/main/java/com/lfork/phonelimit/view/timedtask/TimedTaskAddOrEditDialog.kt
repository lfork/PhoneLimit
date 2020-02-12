package com.lfork.phonelimit.view.timedtask

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.R
import com.lfork.phonelimit.base.widget.BaseDialog
import com.lfork.phonelimit.data.taskconfig.TaskConfig
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_FLOATING
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_LIGHT
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_ROOT
import com.lfork.phonelimit.data.taskconfig.TaskConfig.Companion.LIMIT_MODEL_ULTIMATE
import com.lfork.phonelimit.base.permission.*
import com.lfork.phonelimit.base.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimit.base.permission.PermissionManager.isGrantedFloatingWindowPermission
import com.lfork.phonelimit.base.permission.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimit.base.permission.PermissionManager.modelPermissionCheck
import kotlinx.android.synthetic.main.timed_task_add_or_edit_dialog.*


class TimedTaskAddOrEditDialog(context: Context) : BaseDialog(context) {

    private var limitModelItemPosition = 0
    private var limitCycleItemPosition = 0


    var supportFragmentManager: FragmentManager? = null
    var taskConfig: TaskConfig =
        TaskConfig()
        @SuppressLint("SetTextI18n")


    override fun setWidthScale(): Float {
        return 0.85f
    }

    override fun setEnterAnim(): AnimatorSet? {
        return null
    }

    override fun setExitAnim(): AnimatorSet? {
        return null
    }


    @SuppressLint("SetTextI18n")
    override fun init() {

        taskConfig.let {
            et_focus_time.setText("${it.limitTimeSeconds/60}")
            tv_input_time.text = it.getStarTimeStr()
            limitCycleItemPosition = it.cycleModel
            limitModelItemPosition = it.limitModel
        }
        setupTaskEditFinishedButton()
        btn_close.setOnClickListener { dismiss() }
        setupLimitModelSpinner()
        setupLimitCycleModelSpinner()
        setupDayOfWeekSpinner()
        setupTimePicker()

    }

    private fun setupTaskEditFinishedButton(){
        btn_finish_task.setOnClickListener {

            if (!modelPermissionCheck(context, limitModelItemPosition)) {
                ToastUtils.show("权限不足，请授予相应的权限")
                return@setOnClickListener
            }

            taskConfig.apply {
                val startTimeText = tv_input_time.text.toString()
                startTimeHourOfDay = startTimeText.substring(0, startTimeText.indexOf(':')).toInt()
                startTimeMinute = startTimeText.substring(startTimeText.indexOf(':') + 1).toInt()
                val focusTimeText = et_focus_time.text.toString()

                if (!TextUtils.isEmpty(focusTimeText)) {
                    limitTimeSeconds = focusTimeText.toLong() * 60
//                    limitTimeSeconds = focusTimeText.toLong()
                }
                isImmediatelyExecuted = false
                limitModel = limitModelItemPosition
                cycleModel = limitCycleItemPosition


            }
            timedTaskEditCompletedListener?.onCompleted(taskConfig)
            dismiss()
        }
    }

    private fun setupTimePicker(){
        tv_input_time.setOnClickListener {
            if (supportFragmentManager == null) {
                ToastUtils.show("supportFragmentManager  cannot be null.")
                return@setOnClickListener
            }
            val timePicker = TimePickerFragment()
            timePicker.timePickListener =
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        val minuteStr = if (minute < 10) {
                            "0$minute"
                        } else {
                            minute.toString()
                        }
                        tv_input_time.text = "$hourOfDay:$minuteStr"
                    }
            timePicker.show(supportFragmentManager, "timePicker")
        }
    }

    private fun setupLimitModelSpinner() {
        val spinner = sp_model_select
        sp_model_select.selectedItemPosition
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.limit_model_array, android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter
        spinner.setSelection(limitModelItemPosition)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                limitModelItemPosition = position
                permissionCheckUISettings(limitModelItemPosition)

            }
        }

        layout_usage_permission.setOnClickListener {
            context.checkAndRequestUsagePermission()

        }

        layout_floating_permission.setOnClickListener {
            context.requestFloatingPermission()

        }

        layout_launcher_permission.setOnClickListener {
            context.requestLauncherPermission()

        }

        layout_root_permission.setOnClickListener {
            context.requestRootPermission()

        }
    }

    private fun setupLimitCycleModelSpinner() {
        val spinner = sp_cycle_select
        spinner.setSelection(limitCycleItemPosition)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                limitCycleItemPosition = position
                if (position == 2) {
                    sp_day_of_week.visibility = View.VISIBLE
                } else {
                    taskConfig.startTimeDayOfWeek = -1
                    sp_day_of_week.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun setupDayOfWeekSpinner() {
        if (taskConfig.startTimeDayOfWeek > 0) {
            sp_day_of_week.setSelection(taskConfig.startTimeDayOfWeek - 1)
        }
        sp_day_of_week.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (sp_day_of_week.visibility == View.VISIBLE){
                    taskConfig.startTimeDayOfWeek = position + 1
                }
            }
        }


    }

    var timedTaskEditCompletedListener: TimedTaskEditCompletedListener? = null

    interface TimedTaskEditCompletedListener {
        fun onCompleted(taskConfig: TaskConfig)
    }


    override fun getContentViewId(): Int {
        return R.layout.timed_task_add_or_edit_dialog
    }


    override fun dismiss() {
        super.dismiss()
        supportFragmentManager = null
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

    override fun onResume() {
        super.onResume()

        permissionCheckUISettings(limitModelItemPosition)
    }
}
