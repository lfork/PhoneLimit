package com.lfork.phonelimitadvanced.timedtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hjq.toast.ToastUtils
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.limittask.LimitTaskRepository
import com.lfork.phonelimitadvanced.limit.LimitService
import com.lfork.phonelimitadvanced.limit.LimitTaskConfig
import kotlinx.android.synthetic.main.item_timed_task.view.*
import kotlinx.android.synthetic.main.timed_task_setting_act.*
import java.util.*


class TimedTaskActivity : AppCompatActivity() {

    lateinit var tasksAdapter: TaskConfigAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timed_task_setting_act)

        btn_add_task.setOnClickListener {
            showAddTaskDialog()
        }

        recycle_timed_tasks.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        tasksAdapter = TaskConfigAdapter(this)
        recycle_timed_tasks.adapter = tasksAdapter

    }

    var tempDialog: TimedTaskAddOrEditDialog? = null

    override fun onResume() {
        super.onResume()
        tempDialog?.onResume()
        refreshTasks()

    }


    override fun onDestroy() {
        super.onDestroy()
        tasksAdapter.onDestroy()
    }


    private val timedTaskAddedListener =
        object : TimedTaskAddOrEditDialog.TimedTaskEditCompletedListener {
            override fun onCompleted(taskConfig: LimitTaskConfig) {
                LimitTaskRepository.addTask(taskConfig)
                refreshTasks()
                LimitService.commitTimedTask(this@TimedTaskActivity, taskConfig)
            }
        }


    private val timedTaskUpdateCompletedListener =
        object : TimedTaskAddOrEditDialog.TimedTaskEditCompletedListener {
            override fun onCompleted(taskConfig: LimitTaskConfig) {
                if (LimitService.cancelTimedTask(taskConfig.id)) {
                    LimitService.commitTimedTask(this@TimedTaskActivity, taskConfig)
                    LimitApplication.executeAsyncDataTask {
                        //等任务添加
                        Thread.sleep(100)
                        runOnUiThread {
                            if (LimitService.timedTaskController.containsKey(taskConfig.id)) {
                                ToastUtils.show("修改成功")
                                LimitTaskRepository.updateLimitTask(taskConfig)
                                refreshTasks()
                            } else {
                                ToastUtils.show("修改失败")
                            }
                        }

                    }

                }

            }
        }

    private fun showAddTaskDialog() {
        tempDialog = TimedTaskAddOrEditDialog(this)
        tempDialog?.supportFragmentManager = supportFragmentManager
        tempDialog?.timedTaskEditCompletedListener = timedTaskAddedListener
        tempDialog?.show()

    }


    private fun refreshTasks() {
        tasksAdapter.setItems(LimitTaskRepository.getTasks())
    }


    /**
     *
     * Created by 98620 on 2018/11/8.
     */
    inner class TaskConfigAdapter(var context: AppCompatActivity?) :
        RecyclerView.Adapter<TaskConfigAdapter.NormalHolder>() {

        private val items = ArrayList<LimitTaskConfig>(0);

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_timed_task, parent, false)
            return NormalHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        private var limitModelArray: Array<String> =
            context!!.resources.getStringArray(R.array.limit_model_array)

        private var cycleModelArray: Array<String> =
            context!!.resources.getStringArray(R.array.limit_cycle_array)

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
            val view = holder.itemView
            val item = items[p1]
            view.tv_limit_time.text = (item.limitTimeSeconds / 60).toString()

            view.tv_start_time.text = item.getStarTimeStr()
            view.tv_cycle_model.text = cycleModelArray[item.cycleModel]
            view.tv_limit_model.text = limitModelArray[item.limitModel]


            view.btn_close.isChecked = item.isActive
            if (item.isActive) {
                view.tv_status.text = "已激活"
            } else {
                view.tv_status.text = "已关闭"
            }

            view.btn_edit.setOnClickListener {
                showEditTaskDialog(item)

            }
            view.btn_close.setOnClickListener {
                //点击后，checked的状态就直接变了
                if (!view.btn_close.isChecked) {
                    if (LimitService.cancelTimedTask(item.id)) {
                        view.tv_status.text = "已关闭"
                    } else {
                        ToastUtils.show("关闭失败")
                        view.btn_close.isChecked = !view.btn_close.isChecked
                    }
                } else {
                    LimitService.commitTimedTask(context!!, item)
                    LimitApplication.executeAsyncDataTask {
                        //等任务添加
                        Thread.sleep(100)
                        runOnUiThread {
                            if (LimitService.timedTaskController.containsKey(item.id)) {
                                view.tv_status.text = "已激活"
                            } else {
                                ToastUtils.show("激活失败")
                                view.btn_close.isChecked = !view.btn_close.isChecked
                            }
                        }

                    }

                }
            }
            view.btn_delete.setOnClickListener {
                if (LimitService.cancelTimedTask(item.id)) {
                    LimitTaskRepository.deleteTask(item)
                    deleteItem(p1)
                } else {
                    ToastUtils.show("删除失败")
                }
            }

        }


        private fun showEditTaskDialog(taskConfig: LimitTaskConfig) {
            tempDialog = TimedTaskAddOrEditDialog(context!!)
            tempDialog?.taskConfig = taskConfig
            tempDialog?.supportFragmentManager = context?.supportFragmentManager
            tempDialog?.timedTaskEditCompletedListener = timedTaskUpdateCompletedListener
            tempDialog?.show()
        }

        @Synchronized
        private fun deleteItem(position: Int) {
            if (position !in (0 until items.size)) {
                return
            }
            items.remove(items[position])
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
        }

        inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) //{

        fun setItems(itemList: MutableList<LimitTaskConfig>) {
            items.clear()
            items.addAll(itemList)
            notifyDataSetChanged()
        }

        fun onDestroy() {
            context = null
        }


    }
}
