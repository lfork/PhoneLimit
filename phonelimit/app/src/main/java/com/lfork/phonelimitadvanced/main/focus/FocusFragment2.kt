package com.lfork.phonelimitadvanced.main.focus

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.widget.UsagePermissionDialog
import com.lfork.phonelimitadvanced.data.DataCallback
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.limit.LimitService
import com.lfork.phonelimitadvanced.limit.LimitTaskConfig
import com.lfork.phonelimitadvanced.utils.*
import com.lfork.phonelimitadvanced.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimitadvanced.permission.PermissionManager.isGrantedFloatingWindowPermission
import com.lfork.phonelimitadvanced.permission.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimitadvanced.permission.PermissionManager.requestFloatingWindowPermission
import com.lfork.phonelimitadvanced.utils.ToastUtil.showLong
import kotlinx.android.synthetic.main.item_window_floating.*
import kotlinx.android.synthetic.main.item_window_floating.view.*
import android.support.v7.widget.LinearSnapHelper
import com.lfork.phonelimitadvanced.base.widget.FloatingPermissionDialog
import com.lfork.phonelimitadvanced.main.MainActivity
import com.lfork.phonelimitadvanced.permission.PermissionCheckerAndRequester
import com.lfork.phonelimitadvanced.permission.PermissionManager


class FocusFragment2 : Fragment(),
    PermissionCheckerAndRequester {


    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0

        const val REQUEST_USAGE_ACCESS_PERMISSION = 1

        /**
         * 大于0的话说明正在开启当中，但是还没有完全开启，把数据设置为静态的，只要进程没被杀掉就不会被回收
         * 可以做到类似onSaveState的效果
         */
        var inputTimeMinuteCache = -1L

    }

    lateinit var dialog: AlertDialog

    private var root: View? = null

    private lateinit var adapter: WhiteNameAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (root == null) {
            root = inflater.inflate(R.layout.main_focus_frag_v2, container, false)

            initDialog()
            registerListener(root!!)
//            checkAndRecoveryLimitTask()
            //displaySetting(root!!)
            root!!.recycle_white_list.layoutManager =
                    LinearLayoutManager(context, HORIZONTAL, false)
            adapter = WhiteNameAdapter()
            adapter.customIconOnClickListener = customIconOnClickListener

            root!!.recycle_white_list.adapter = adapter

            LinearSnapHelper().attachToRecyclerView(root!!.recycle_white_list)

            val limitIntent = Intent(context, LimitService::class.java)

            bindService(limitIntent, limitServiceConnection, Context.BIND_AUTO_CREATE)
            startService(limitIntent)
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        //可能需要多次开启，因为之前可能没有权限，导致开启无效
        if (inputTimeMinuteCache > 0) {
            startLimit(inputTimeMinuteCache)
        }
        refreshAppInfoData()

        if (LimitApplication.isOnLimitation) {
            (activity as MainActivity?)?.hideOtherUI()
        } else {
            (activity as MainActivity?)?.showOtherUI()
        }
    }

    override fun onPause() {
        super.onPause()
//        (activity as MainActivity?)?.hideOtherUI()
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

        if (!requestUsagePermission()) {
            return false
        }

        //悬浮窗权限
        if (LimitApplication.defaultLimitModel == LimitTaskConfig.LIMIT_MODEL_FLOATING) {
            if (!requestFloatingPermission()) {
                return false
            }
        }


        //默认桌面权限

        if (LimitApplication.defaultLimitModel in arrayOf(
                LimitTaskConfig.LIMIT_MODEL_ROOT,
                LimitTaskConfig.LIMIT_MODEL_ULTIMATE
            )
        ) {
            if (!requestLauncherPermission()){
                return false
            }

        }




        if (LimitApplication.defaultLimitModel == LimitTaskConfig.LIMIT_MODEL_ROOT) {

           if(!requestRootPermission()){
               requestRootPermission()
           }
        }

        return true
    }


    override fun requestUsagePermission(): Boolean {
        if (!isGrantedStatAccessPermission() && LockUtil.isNoOption(context)) {
            val dialog = UsagePermissionDialog(context)

            dialog.setOnClickListener {
                startActivityForResult(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                    REQUEST_USAGE_ACCESS_PERMISSION
                )
            }
            dialog.setOnCancelListener {
                inputTimeMinuteCache = -1
            }
            dialog.show()

            return false
        }

        return true
    }

    override fun requestFloatingPermission(): Boolean {

        if (!isGrantedFloatingWindowPermission()) {
            val dialog = FloatingPermissionDialog(context)
            dialog.setOnClickListener {
                requestFloatingWindowPermission()
            }
            dialog.setOnCancelListener { inputTimeMinuteCache = -1 }
            dialog.show()
            return false
        }
        return true
    }

    override fun requestLauncherPermission(): Boolean {
        if (!isDefaultLauncher()) {

            if (!dialog.isShowing) {
                dialog.show()
            }
            if (!isDefaultLauncher()) {
                ToastUtil.showLong(context, getString(R.string.launcher_denied_tips))
                return false
            }
        }

        return true
    }

    override fun requestRootPermission(): Boolean {
        if (PermissionManager.isRooted()) {
            if (!PermissionManager.isGrantedRootPermission()) {
                PermissionManager.requestRootPermission(activity!!.packageCodePath)

                if (!PermissionManager.isGrantedRootPermission()) {
                    ToastUtil.showShort(context, getString(R.string.permission_denied_tips))
                    LimitApplication.isRooted = false
                    return false
                }
            }
            LimitApplication.isRooted = true
        } else {
            LimitApplication.isRooted = false
            Log.d(LimitApplication.TAG, "看来是没有ROOT")
            return false
        }

        return true
    }

    private fun registerListener(view: View) {
        view.btn_start_remain_time_text.setOnClickListener {
            startLimit()
        }
    }

    private fun initDialog() {
        dialog = AlertDialog.Builder(context!!).setTitle(R.string.tips_launcher_setting)
            .setPositiveButton(R.string.action_default_apps_setting) { dialog, id ->
                //去设置默认桌面
                openDefaultAppsSetting()
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                inputTimeMinuteCache = -1
            }
            .setCancelable(false)
            .create()
    }

    private fun displaySetting(view: View) {

        //tips设置
//        if (DeviceHelper.isHuawei()) {
//            view.tips_huawei.visibility = View.VISIBLE
//        } else {
////            btn_set_launcher.visibility = View.VISIBLE
//            when {
////                android.os.Build.BRAND == "OnePlus" -> view.tips_normal.visibility = View.VISIBLE
//                PermissionManager.isRooted() -> view.tips_root.visibility = View.VISIBLE
//                else -> view.tips_normal.visibility = View.VISIBLE
//            }
//        }
//        //button显示设置 如果是7.0 以上的系统就跳转到设置界面让用户手动设置默认程序【主流用户】
//        //如果是定制系统 就跳转到设置界面即可。  就不主动在当前的界面手动设置默认桌面了。


    }


    private lateinit var limitBinder: LimitService.LimitBinder

    val limitStateListener = object : LimitService.StateListener {
        override fun onLimitFinished() {
            runOnUiThread {
                btn_start_remain_time_text.text = "Start"
//                tips.text = "限制已解除"
                ToastUtil.showLong(context, "限制已解除")
                (activity as MainActivity?)?.showOtherUI()
            }
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
                    when {
                        timeSeconds > 60 * 60 -> btn_start_remain_time_text.text =
                                "${timeSeconds / 3600}小时${(timeSeconds % 3600) / 60}分${timeSeconds % 60}秒"
                        timeSeconds > 60 -> btn_start_remain_time_text.text =
                                "${timeSeconds / 60}分${timeSeconds % 60}秒"
                        else -> btn_start_remain_time_text.text = "${timeSeconds}秒"
                    }
                }
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
     * 开始限制的入口函数
     */
    @Synchronized
    private fun startLimit(limitTimeSeconds: Long = 15L) {
        inputTimeMinuteCache = limitTimeSeconds

        if (!permissionCheck()) {
            return
        }
        val taskInfo = LimitTaskConfig().apply {
            this.limitTimeSeconds = limitTimeSeconds
            isImmediatelyExecuted = true
            limitModel = LimitApplication.defaultLimitModel
        }

        //开启之前需要把权限获取到位  不同的限制模式需要不同的权限。
        val limitIntent = Intent(context, LimitService::class.java)
        limitIntent.putExtra("limit_task_time_info", taskInfo)
        startService(limitIntent)
        inputTimeMinuteCache = -1
    }

    private fun unbindLimitService() {
//        btn_set_launcher.visibility = View.VISIBLE
        if (context != null) {
            val stopIntent = Intent(context, LimitService::class.java)
            stopService(stopIntent)
            unbindService(limitServiceConnection)
        }
    }

//    private fun checkAndRecoveryLimitTask() {
//        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
//        val remainTimeSeconds = sp.getLong("remain_time_seconds", 0)
//
//        if (remainTimeSeconds > 1) {
//            startLimit(remainTimeSeconds)
//        }
//    }


    private var customIconOnClickListener: CustomIconOnClickListener? = null


    fun setCustomClickListener(customIconOnClickListener: CustomIconOnClickListener) {
        this.customIconOnClickListener = customIconOnClickListener
    }

}
