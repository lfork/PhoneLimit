package com.lfork.phonelimitadvanced.main.focus

import android.content.*
import android.os.Bundle
import android.os.IBinder
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
import com.lfork.phonelimitadvanced.LimitApplication.Companion.App
import com.lfork.phonelimitadvanced.LimitApplication.Companion.haveRemainTime
import com.lfork.phonelimitadvanced.LimitApplication.Companion.tempInputTimeMinute
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.limit.LimitService
import com.lfork.phonelimitadvanced.limit.LimitStateListener
import com.lfork.phonelimitadvanced.utils.*
import com.lfork.phonelimitadvanced.utils.PermissionManager.isDefaultLauncher
import com.lfork.phonelimitadvanced.utils.PermissionManager.isGrantedStatAccessPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.isGrantedWindowPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.requestFloatingWindowPermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.requestStateUsagePermission
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncher
import kotlinx.android.synthetic.main.main_focus_frag.*
import kotlinx.android.synthetic.main.main_focus_frag.view.*

class FocusFragment : Fragment() {

    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0

        const val REQUEST_USAGE_ACCESS_PERMISSION = 1

    }

    lateinit var dialog: AlertDialog

    private var root: View? = null

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
            val adapter = FocusRecycleAdapter()

            root!!.recycle_white_list.adapter = adapter
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        //可能需要多次开启，因为之前可能没有权限，导致开启无效
        if (tempInputTimeMinute > 0) {
            startLimit(tempInputTimeMinute)
        }
        (root!!.recycle_white_list.adapter as FocusRecycleAdapter).setItems(App.getWhiteNameAppsInfo())
        root!!.recycle_white_list.adapter?.notifyDataSetChanged()
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
//                        startLimit(editText.text.toString().toLong())
//                    } else {
//                        startLimit()
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
            requestStateUsagePermission(REQUEST_USAGE_ACCESS_PERMISSION)
            return false
        }

        //如果是华为手机 就申请悬浮窗的权限 否则就申请默认桌面
        if (DeviceHelper.isHuawei()) {
            LimitApplication.isHuawei = true
            if (!isGrantedWindowPermission()) {
                requestFloatingWindowPermission()
                //TODO result
                return false
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
                App.getLauncherApps()
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
    }

    private fun initDialog() {
        dialog = AlertDialog.Builder(context!!).setTitle(R.string.tips_launcher_setting)
            .setPositiveButton(R.string.action_default_apps_setting) { dialog, id ->
                //去设置默认桌面
                openDefaultAppsSetting()
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                tempInputTimeMinute = -1
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
            if (android.os.Build.BRAND == "OnePlus") {
                view.tips_normal.visibility = View.VISIBLE
            } else if (PermissionManager.isRooted()) {
                view.tips_root.visibility = View.VISIBLE
            } else {
                view.tips_normal.visibility = View.VISIBLE
            }
        }


        //button显示设置 如果是7.0 以上的系统就跳转到设置界面让用户手动设置默认程序【主流用户】
        //如果是定制系统 就跳转到设置界面即可。  就不主动在当前的界面手动设置默认桌面了。


    }

    private val limitServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LimitService.StateBinder

            binder.getLimitService().listener = object : LimitStateListener {
                override fun onLimitFinished() {
                    runOnUiThread {
                        ToastUtil.showLong(context, "限制已解除")
                        LimitApplication.isOnLimitation = false
                    }
                }

                override fun onLimitStarted() {
                    runOnUiThread {
                        ToastUtil.showLong(context, "限制已开启")
                    }
                }

                override fun autoUnlocked(msg: String) {
                    runOnUiThread {
                        remain_time_text.text = msg
                        closeLimit()
                    }
                }

                override fun forceUnlocked(msg: String) {
                    runOnUiThread {
                        remain_time_text.text = msg
                        closeLimit()
                    }
                }

                override fun remainTimeRefreshed(timeSeconds: Long) {
                    runOnUiThread {
                        //刷新剩余时间
                        Log.d("timeTest", "剩余时间${timeSeconds}秒")

                        if (timeSeconds > 60 * 60) {
                            remain_time_text.text =
                                    "解除限制剩余时间${timeSeconds / 3600}小时${(timeSeconds % 3600) / 60}分${timeSeconds % 60}秒"
                        } else if (timeSeconds > 60) {
                            remain_time_text.text = "剩余时间${timeSeconds / 60}分${timeSeconds % 60}秒"
                        } else {
                            remain_time_text.text = "剩余时间${timeSeconds}秒"
                        }

                        saveRemainTime(timeSeconds)
                    }
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }

    private fun startLimit(limitTimeSeconds: Long = 60L) {
        tempInputTimeMinute = limitTimeSeconds

        if (!isPermitted()) {
            return
        }

        haveRemainTime = true

//        btn_set_launcher.visibility = View.INVISIBLE

        //开启之前需要把权限获取到位
        val limitIntent = Intent(context, LimitService::class.java)
        limitIntent.putExtra("limit_time", limitTimeSeconds)

        bindService(limitIntent, limitServiceConnection, Context.BIND_AUTO_CREATE)
        startService(limitIntent)


        //开启完成
        LimitApplication.isOnLimitation = true
        tempInputTimeMinute = -1
    }

    private fun closeLimit() {
//        btn_set_launcher.visibility = View.VISIBLE
        unbindService(limitServiceConnection)
        val stopIntent = Intent(context, LimitService::class.java)
        stopService(stopIntent)

        if (!LimitApplication.isHuawei) {

            clearDefaultLauncher()
        }

        haveRemainTime = false
    }


    private fun checkAndRecoveryLimitTask() {


        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val remainTimeSeconds = sp.getLong("remain_time_seconds", 0)
        if (remainTimeSeconds > 1) {
            haveRemainTime = true
            startLimit(remainTimeSeconds)
        }
    }

    private fun saveRemainTime(remainTimeSeconds: Long) {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putLong("remain_time_seconds", remainTimeSeconds)
        editor.apply()
    }

}
