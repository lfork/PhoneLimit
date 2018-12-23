package com.lfork.phonelimitadvanced.main.focus

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.DataCallback
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.data.appinfo.AppInfoRepository
import com.lfork.phonelimitadvanced.limit.LimitService
import com.lfork.phonelimitadvanced.utils.*
import com.lfork.phonelimitadvanced.utils.PermissionManager.isDefaultLauncher
import com.lfork.phonelimitadvanced.utils.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.isGrantedFloatingWindowPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.requestFloatingWindowPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher
import com.lfork.phonelimitadvanced.utils.ToastUtil.showLong
import com.lfork.phonelimitadvanced.widget.DialogPermission
import kotlinx.android.synthetic.main.main_focus_frag.*
import kotlinx.android.synthetic.main.main_focus_frag.view.*

class FocusFragment : Fragment() {

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
            root = inflater.inflate(R.layout.main_focus_frag, container, false)

            initDialog()
            registerListener(root!!)
            checkAndRecoveryLimitTask()
            displaySetting(root!!)
            root!!.recycle_white_list.layoutManager =
                    LinearLayoutManager(context, HORIZONTAL, false)
            adapter = WhiteNameAdapter()
            adapter.customIconOnClickListener = customIconOnClickListener

            root!!.recycle_white_list.adapter = adapter
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        //可能需要多次开启，因为之前可能没有权限，导致开启无效
        if (inputTimeMinuteCache > 0) {
            startLimit(inputTimeMinuteCache)
        }

        refreshData()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }

    private fun refreshData() {

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
     * 暂时还不需要访问文件的权限
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            //TODO 权限申请
//            REQUEST_STORAGE_PERMISSION -> {
//                if (isGrantedStoragePermission(applicationContext)) {
//                    if (!TextUtils.isEmpty(editText.text.toString())) {
//                        initTimer(editText.text.toString().toLong())
//                    } else {
//                        initTimer()
//                    }
//                }
//            }

            REQUEST_USAGE_ACCESS_PERMISSION -> {
                if (isGrantedStatAccessPermission()) {
                    startLimit()
                } else {
                    ToastUtil.showShort(context, getString(R.string.permission_denied_tips))
                }
            }
        }
    }

    /**
     * 开启限制前需要检查相应的权限
     */
    private fun isPermitted(): Boolean {
        //如果没有获得查看使用情况权限和 手机存在查看使用情况这个界面(Android 5.0以上)
        if (!isGrantedStatAccessPermission() && LockUtil.isNoOption(context)) {
            val dialog = DialogPermission(context)
            dialog.show()
            dialog.setOnClickListener {
                startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS) ,REQUEST_USAGE_ACCESS_PERMISSION)
            }
            dialog.setOnCancelListener {
                inputTimeMinuteCache = -1
            }
            return false
        }

        //如果是华为手机 就申请悬浮窗的权限 否则就申请默认桌面(如果有root权限还要把其他的桌面unhide掉)
        if (DeviceHelper.isHuawei()) {
            LimitApplication.isFloatingWindowMode = true
            if (!isGrantedFloatingWindowPermission()) {
                requestFloatingWindowPermission()
                //TODO result
                if (!isGrantedFloatingWindowPermission()) {
                    ToastUtil.showLong(context, getString(R.string.floating_window_denied_tips))
                    return false
                }
            }
        } else {
            if (!isDefaultLauncher()) {

                if (!dialog.isShowing) {
                    dialog.show()
                }
                if (!isDefaultLauncher()) {
                    ToastUtil.showLong(context, getString(R.string.launcher_denied_tips))
                    return false
                }
            }

            if (PermissionManager.isRooted()) {
                if (!PermissionManager.isGrantedRootPermission()) {
                    PermissionManager.requestRootPermission(activity!!.packageCodePath)

                    if (!PermissionManager.isGrantedRootPermission()) {
                        ToastUtil.showShort(context, getString(R.string.permission_denied_tips))
                        return false
                    }
                }
                LimitApplication.isRooted = true
//                App.getLauncherApps()
            } else {
                Log.d(LimitApplication.TAG, "看来是没有ROOT")
            }
        }
        //        if (!PermissionManager.isGrantedStoragePermission(applicationContext)) {
//            ToastUtil.showShort(this, "请给与程序需要的权限")
//            requestStoragePermission(applicationContext, REQUEST_STORAGE_PERMISSION, this)
//            return
//        }
        return true
    }

    private fun registerListener(view: View) {
        view.btn_start.setOnClickListener {
            if (!TextUtils.isEmpty(editText.text.toString())) {
                startLimit(editText.text.toString().toLong() * 60)
            } else {
                startLimit()
            }
        }
        view.btn_set_launcher.setOnClickListener { clearDefaultLauncher() }

//        view.btn_set_launcher
//        limitBinder.getLimitService()
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
        if (DeviceHelper.isHuawei()) {
            view.tips_huawei.visibility = View.VISIBLE
        } else {
//            btn_set_launcher.visibility = View.VISIBLE
            when {
//                android.os.Build.BRAND == "OnePlus" -> view.tips_normal.visibility = View.VISIBLE
                PermissionManager.isRooted() -> view.tips_root.visibility = View.VISIBLE
                else -> view.tips_normal.visibility = View.VISIBLE
            }
        }


        //button显示设置 如果是7.0 以上的系统就跳转到设置界面让用户手动设置默认程序【主流用户】
        //如果是定制系统 就跳转到设置界面即可。  就不主动在当前的界面手动设置默认桌面了。


    }


    private lateinit var limitBinder: LimitService.LimitBinder

    val limitStateListener = object : LimitService.StateListener {
        override fun onLimitFinished() {
            runOnUiThread {
                remain_time_text.text = "限制已解除"
                ToastUtil.showLong(context, "限制已解除")
                unbindLimitService()
            }
        }

        override fun onLimitStarted() {
            runOnUiThread {
                //ToastUtil.showLong(context, "限制已开启")
            }
        }


        override fun remainTimeRefreshed(timeSeconds: Long) {
            runOnUiThread {
                if (remain_time_text != null) {
                    when {
                        timeSeconds > 60 * 60 -> remain_time_text.text =
                                "解除限制剩余时间${timeSeconds / 3600}小时${(timeSeconds % 3600) / 60}分${timeSeconds % 60}秒"
                        timeSeconds > 60 -> remain_time_text.text = "剩余时间${timeSeconds / 60}分${timeSeconds % 60}秒"
                        else -> remain_time_text.text = "剩余时间${timeSeconds}秒"
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

    private fun startLimit(limitTimeSeconds: Long = 60L) {
        inputTimeMinuteCache = limitTimeSeconds
        if (!isPermitted()) {
            return
        }

        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val startTime = sp.getLong("start_time", System.currentTimeMillis())

        //开启之前需要把权限获取到位
        val limitIntent = Intent(context, LimitService::class.java)
        limitIntent.putExtra("limit_time", limitTimeSeconds)
        limitIntent.putExtra("start_time", startTime)


        bindService(limitIntent, limitServiceConnection, Context.BIND_AUTO_CREATE)
        startService(limitIntent)
        inputTimeMinuteCache = -1
    }

    fun unbindLimitService() {
//        btn_set_launcher.visibility = View.VISIBLE
        if (context != null) {
            val stopIntent = Intent(context, LimitService::class.java)
            stopService(stopIntent)
            unbindService(limitServiceConnection)
        }
    }

    private fun checkAndRecoveryLimitTask() {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val remainTimeSeconds = sp.getLong("remain_time_seconds", 0)

        if (remainTimeSeconds > 1) {
            startLimit(remainTimeSeconds)
        }
    }


    private var customIconOnClickListener: CustomIconOnClickListener? = null


    fun setCustomClickListener(customIconOnClickListener: CustomIconOnClickListener) {
        this.customIconOnClickListener = customIconOnClickListener
    }

}
