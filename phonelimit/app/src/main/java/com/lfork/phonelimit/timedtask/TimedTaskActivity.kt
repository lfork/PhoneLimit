package com.lfork.phonelimit.timedtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.LimitActivity
import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.taskconfig.TaskConfigRepository
import com.lfork.phonelimit.limitcore.LimitService
import com.lfork.phonelimit.data.taskconfig.TaskConfig
import kotlinx.android.synthetic.main.timed_task_recycle_item.view.*
import kotlinx.android.synthetic.main.timed_task_setting_act.*
import java.util.*


class TimedTaskActivity : LimitActivity(){


    lateinit var tasksAdapter: TaskConfigAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!LimitService.timedTaskCheckFlag) {
            ToastUtils.show("数据正在初始化中...")
            finish()
        }

        setContentView(R.layout.timed_task_setting_act)

        btn_add_task.setOnClickListener {
            if (TaskConfigRepository.cacheTasks == null) {
                ToastUtils.show("数据正在初始化中...")
            } else {
                if (TaskConfigRepository.cacheTasks!!.size >= 5) {
                    ToastUtils.show("最多添加5个任务")
                } else {
                    showAddTaskDialog()
                }
            }


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
            override fun onCompleted(taskConfig: TaskConfig) {

                LimitApplication.executeAsyncDataTask {
                    TaskConfigRepository.addTask(taskConfig)
                    val data = TaskConfigRepository.getTasks()
                    runOnUiThread {
                        tasksAdapter.refreshItems(data)
                        LimitService.commitTimedTask(this@TimedTaskActivity, taskConfig)
                    }
                }


            }
        }


    private val timedTaskUpdateCompletedListener =
        object : TimedTaskAddOrEditDialog.TimedTaskEditCompletedListener {
            override fun onCompleted(taskConfig: TaskConfig) {
                if (LimitService.cancelTimedTask(taskConfig.id)) {
                    LimitService.commitTimedTask(this@TimedTaskActivity, taskConfig)
                    LimitApplication.executeAsyncDataTask {
                        //等任务添加
                        Thread.sleep(100)
                        if (LimitService.timedTaskController.containsKey(taskConfig.id)) {
                            TaskConfigRepository.updateLimitTask(taskConfig)
                            refreshTasks()
                            ToastUtils.show("修改成功")
                        } else {
                            ToastUtils.show("修改失败")
                        }
                    }

                } else {
                    ToastUtils.show("任务已经或者马上开始执行，修改失败")
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
        LimitApplication.executeAsyncDataTask {
            val data = TaskConfigRepository.getTasks()
            runOnUiThread {
                tasksAdapter.refreshItems(data)
            }
        }
    }


    /**
     *
     * Created by 98620 on 2018/11/8.
     */
    inner class TaskConfigAdapter(var context: AppCompatActivity?) :
        RecyclerView.Adapter<TaskConfigAdapter.NormalHolder>() {

        private val items = ArrayList<TaskConfig>(0);

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.timed_task_recycle_item, parent, false)
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
            view.tv_limit_time.text = item.getLimitTimeStr()

            view.tv_start_time.text = item.getStarTimeStr(true)
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
                        item.isActive = false
                        TaskConfigRepository.updateLimitTask(item, object : DataCallback<String> {
                            override fun succeed(data: String) {
                                runOnUiThread {
                                    ToastUtils.show("关闭成功")
                                    view.tv_status.text = "已关闭"
                                }
                            }

                            override fun failed(code: Int, log: String) {
                                runOnUiThread {
                                    item.isActive = true
                                    ToastUtils.show("关闭失败")
                                    view.btn_close.isChecked = !view.btn_close.isChecked
                                }
                            }
                        })


                    } else {
                        item.isActive = true
                        ToastUtils.show("关闭失败")
                        view.btn_close.isChecked = !view.btn_close.isChecked
                    }
                } else {
                    LimitService.commitTimedTask(context!!, item)
                    LimitApplication.executeAsyncDataTask {
                        //等任务添加
                        Thread.sleep(100)
                        item.isActive = true
                        if (TaskConfigRepository.updateLimitTask(item) > 0 && LimitService.timedTaskController.containsKey(
                                item.id
                            )
                        ) {
                            runOnUiThread {
                                ToastUtils.show("激活成功")
                                view.tv_status.text = "已激活"
                            }

                        } else {
                            runOnUiThread {
                                item.isActive = false
                                ToastUtils.show("激活失败")
                                view.btn_close.isChecked = !view.btn_close.isChecked
                            }

                        }
                    }

                }
            }
            view.btn_delete.setOnClickListener {
                if (LimitService.cancelTimedTask(item.id) || !item.isActive) {
                    LimitApplication.executeAsyncDataTask {
                        TaskConfigRepository.deleteTask(item)
                    }
                    deleteItem(p1)
                } else {
                    //可能是任务已经在运行了，所以删除失败
                    if (LimitService.taskIsRunning(item.id)) {
                        ToastUtils.show("限制任务已开始，暂时无法删除")
                    } else if (LimitService.taskIsDone(item.id)) {
                        ToastUtils.show("任务已完成，删除失败")
                    } else {
                        ToastUtils.show("删除失败")
                    }

                }
            }

        }


        private fun showEditTaskDialog(taskConfig: TaskConfig) {
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

        fun refreshItems(itemList: MutableList<TaskConfig>) {
            items.clear()
            items.addAll(itemList)
            notifyDataSetChanged()
        }

        fun onDestroy() {
            context = null
        }


    }
}
