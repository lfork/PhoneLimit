package com.lfork.phonelimitadvanced.main.focus

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.DataCallback
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.limitcore.LimitService
import com.lfork.phonelimitadvanced.data.taskconfig.TaskConfig
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.base.permission.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimitadvanced.base.permission.checkAndRequestUsagePermission
import com.lfork.phonelimitadvanced.base.permission.requestFloatingPermission
import com.lfork.phonelimitadvanced.base.permission.requestLauncherPermission
import com.lfork.phonelimitadvanced.base.permission.requestRootPermission
import com.lfork.phonelimitadvanced.base.widget.FirstItemSnapHelper
import com.lfork.phonelimitadvanced.base.widget.ScrollLinearLayoutManager
import com.lfork.phonelimitadvanced.data.getSettingsIndexTipsSwitch
import com.lfork.phonelimitadvanced.data.urlinfo.UrlInfoRepository
import com.lfork.phonelimitadvanced.utils.*
import com.lfork.phonelimitadvanced.utils.ToastUtil.showLong
import kotlinx.android.synthetic.main.item_window_floating.*
import kotlinx.android.synthetic.main.item_window_floating.view.*


class FocusFragment2 : Fragment() {


    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0

        const val REQUEST_USAGE_ACCESS_PERMISSION = 1

        /**
         * 大于0的话说明正在开启当中，但是还没有完全开启，把数据设置为静态的，只要进程没被杀掉就不会被回收
         * 可以做到类似onSaveState的效果
         */
        private var inputTimeMinuteCache = -1L

        var remainTimeCache: String = ""

        /**
         * 第一打开APP的滑动提示是否完成
         */
        var isRecyclerScrolled = false
    }

//    lateinit var dialog: AlertDialog

    private var root: View? = null

    private lateinit var adapter: WhiteNameAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (root == null) {
            root = inflater.inflate(R.layout.focus_frag_v2, container, false)
            registerListener(root!!)
            setupRecyclerView()



            val limitIntent = Intent(context, LimitService::class.java)
            bindService(limitIntent, limitServiceConnection, Context.BIND_AUTO_CREATE)
            startService(limitIntent)
        }

        return root
    }

    private fun setupRecyclerView() {
        root!!.recycle_white_list.layoutManager =
                ScrollLinearLayoutManager(context, HORIZONTAL, false)
        adapter = WhiteNameAdapter()
        adapter.customIconOnClickListener = customIconOnClickListener

        root!!.recycle_white_list.adapter = adapter

        FirstItemSnapHelper().attachToRecyclerView(root!!.recycle_white_list)
    }



    override fun onResume() {
        super.onResume()
        //可能需要多次开启，因为之前可能没有权限，导致开启无效
        if (inputTimeMinuteCache > 0) {
            startLimit(inputTimeMinuteCache)
        }
        refreshAppInfoData()

        if (remainTimeCache.isEmpty()){
            view?.btn_start_remain_time_text?.text = "Start"
        }

        if (LimitApplication.isOnLimitation) {
            (activity as MainActivity?)?.hideOtherUI()
        } else {
            (activity as MainActivity?)?.showOtherUI()
        }

        if (LimitApplication.isFirstOpen && !isRecyclerScrolled){
            LimitApplication.executeAsyncDataTask {
                Thread.sleep(1500)
                runOnUiThread {
                    root!!.recycle_white_list.smoothScrollToPosition(adapter.itemCount - 1)
                }

                Thread.sleep(2500)

                runOnUiThread {
                    root!!.recycle_white_list.smoothScrollToPosition(0)
                }

                isRecyclerScrolled = true
            }
        }


        if (context?.getSettingsIndexTipsSwitch() != false){
            root!!.tv_index_tips.visibility = View.VISIBLE
        } else{
            root!!.tv_index_tips.visibility = View.GONE
        }

//
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity?)?.hideOtherUI()
    }


    /**
     * 主动退出才能完全关闭服务
     */
    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
        unbindLimitService()
    }


    /**
     * 暂时还不需要访问文件的权限
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_USAGE_ACCESS_PERMISSION -> {
                if (isGrantedStatAccessPermission()) {
                    startLimit()
                } else {
                    ToastUtil.showShort(context, getString(R.string.permission_denied_tips))
                }
            }
        }
    }


    private fun refreshAppInfoData() {
        AppInfoRepository.getWhiteNameApps(object : DataCallback<List<AppInfo>> {
            override fun succeed(data: List<AppInfo>) {
                if (context != null) {
                    val tempData = ArrayList<AppInfo>();

                    data.forEach {
                        it.icon = getAppIcon(context!!, it.packageName)
                        if (it.icon != null) {
                            tempData.add(it)
                        }
                    }
                    adapter.setItems(tempData)
                    runOnUiThread { adapter.notifyDataSetChanged() }
                }
            }

            override fun failed(code: Int, log: String) {
                if (context != null) {
                    showLong(context!!, log)
                }
            }
        })
    }

    /**
     * 开启限制前需要检查相应的权限
     * @return false 表示权限不足
     */
    private fun permissionCheck(): Boolean {
        //如果没有获得查看使用情况权限和 手机存在查看使用情况这个界面(Android 5.0以上)

        if (!checkAndRequestUsagePermission()) {
            return false
        }

        //悬浮窗权限
        if (LimitApplication.defaultLimitModel in arrayOf(
                TaskConfig.LIMIT_MODEL_FLOATING,
                TaskConfig.LIMIT_MODEL_ULTIMATE
            )
        ) {
            if (!requestFloatingPermission()) {
                return false
            }
        }


        //默认桌面权限
        if (LimitApplication.defaultLimitModel in arrayOf(
//                TaskConfig.LIMIT_MODEL_ROOT,
                TaskConfig.LIMIT_MODEL_ULTIMATE
            )
        ) {
            if (!requestLauncherPermission()) {
                return false
            }

        }




        if (LimitApplication.defaultLimitModel == TaskConfig.LIMIT_MODEL_ROOT) {
            if (!requestRootPermission()) {
                return false
            }
        }

        return true
    }


    private fun registerListener(view: View) {

        if (remainTimeCache.isNotEmpty()) {
            view.btn_start_remain_time_text.text = ""
        }

        view.btn_start_remain_time_text.setOnClickListener {

            if (LimitApplication.isOnLimitation) {
                return@setOnClickListener
            }

            if (!permissionCheck()) {
                return@setOnClickListener
            }

            val et = EditText(context).apply {
                inputType = EditorInfo.TYPE_CLASS_NUMBER
            }

            et.hint = "25分钟"

            AlertDialog.Builder(context!!).setTitle("输入限制时间(单位：分钟)")
                .setView(et)
                .setCancelable(true)
                .setPositiveButton(
                    "确定"
                ) { dialog, which ->

                    val input = et.text.toString()
                    if (input == "") {
                        startLimit()
                    } else {
                        startLimit(limitTimeSeconds = input.toLong() * 60)
                        val imm =
                            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }

                }
                .setNegativeButton("取消", null)
                .show()

        }
    }


    private lateinit var limitBinder: LimitService.LimitBinder

    val limitStateListener = object : LimitService.StateListener {
        override fun onLimitFinished() {
            runOnUiThread {
                btn_start_remain_time_text?.text = "Start"
//                tips.text = "限制已解除"
                ToastUtil.showLong(context, "限制已解除")
                if (this@FocusFragment2.isVisible){
                    (activity as MainActivity?)?.showOtherUI()
                }
            }
            UrlInfoRepository.activeUrl()
            remainTimeCache = ""
        }

        override fun onLimitStarted() {
            runOnUiThread {
                //ToastUtil.showLong(context, "限制已开启")
                (activity as MainActivity?)?.hideOtherUI()
            }
        }


        override fun updateRemainTime(timeSeconds: Long) {
            runOnUiThread {
                if (btn_start_remain_time_text != null) {
                    remainTimeCache = when {
                        timeSeconds > 60 * 60 ->
                            "${timeSeconds / 3600}小时${(timeSeconds % 3600) / 60}分${timeSeconds % 60}秒"
                        timeSeconds > 60 ->
                            "${timeSeconds / 60}分${timeSeconds % 60}秒"
                        else -> "${timeSeconds}秒"
                    }

                    btn_start_remain_time_text.text = remainTimeCache
                }

                //ToastUtil.showLong(context, "限制已开启")
                (activity as MainActivity?)?.hideOtherUI()
            }
        }
    }

    private val limitServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            limitBinder = iBinder as LimitService.LimitBinder
            limitBinder.setLimitStateListener(limitStateListener)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }


    /**
     * 开始限制
     */
    @Synchronized
    private fun startLimit(limitTimeSeconds: Long = 15L) {
        inputTimeMinuteCache = limitTimeSeconds

        if (!permissionCheck()) {
            return
        }
        LimitService.startLimit(context!!, limitTimeSeconds)
        inputTimeMinuteCache = -1
    }

    private fun unbindLimitService() {
        if (context != null) {
            val stopIntent = Intent(context, LimitService::class.java)
            stopService(stopIntent)
            unbindService(limitServiceConnection)
        }
    }


    private var customIconOnClickListener: CustomIconOnClickListener? = null


    fun setCustomClickListener(customIconOnClickListener: CustomIconOnClickListener) {
        this.customIconOnClickListener = customIconOnClickListener
    }


}
