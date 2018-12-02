package com.lfork.phonelimitadvanced.main

import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.lfork.phonelimitadvanced.LimitApplication
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
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncherFake
import kotlinx.android.synthetic.main.main_act.*

class MainActivity :
    AppCompatActivity() { //, AdapterView.OnItemSelectedListener, OnClickListener, SwipeRefreshLayout.OnRefreshListener, RadioGroup.OnCheckedChangeListener

    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0

        const val REQUEST_USAGE_ACCESS_PERMISSION = 1


    }

    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)

        registerListener()
        checkAndRecoveryLimitTask()
        displaySetting()

//        test_recycle.layoutManager = LinearLayoutManager(this)
//        test_recycle.adapter = MyRecycleAdapter()
    }

    override fun onResume() {
        super.onResume()
        //可能需要多次开启，因为之前可能没有权限，导致开启无效
        if (tempInputTimeMinute > 0) {
            startLimit(tempInputTimeMinute)
        }
    }

    override fun onPause() {
        super.onPause()
        clearDefaultLauncherFake()
    }

    override fun onBackPressed() {
        if (!LimitApplication.isOnLimitation) {
            super.onBackPressed()
        }
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
                    ToastUtil.showShort(this, getString(R.string.permission_denied_tips))
                }
            }
        }
    }

    /**
     * 开启限制前需要检查相应的权限
     */
    private fun isPermitted(): Boolean {
        //如果没有获得查看使用情况权限和 手机存在查看使用情况这个界面(Android 5.0以上)
        if (!isGrantedStatAccessPermission() && LockUtil.isNoOption(this@MainActivity)) {
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
                    ToastUtil.showLong(this, getString(R.string.launcher_denied_tips))
                    return false
                }
            }

            if (PermissionManager.isRooted()) {
                if (!PermissionManager.isGrantedRootPermission()) {
                    PermissionManager.requestRootPermission(packageCodePath)

                    if (!PermissionManager.isGrantedRootPermission()) {
                        ToastUtil.showShort(this, getString(R.string.permission_denied_tips))
                        return false
                    }
                }

                LimitApplication.isRooted = true
            }
        }

        //        if (!PermissionManager.isGrantedStoragePermission(applicationContext)) {
//            ToastUtil.showShort(this, "请给与程序需要的权限")
//            requestStoragePermission(applicationContext, REQUEST_STORAGE_PERMISSION, this)
//            return
//        }


        return true
    }

    private fun registerListener() {
        btn_start.setOnClickListener {
            if (!TextUtils.isEmpty(editText.text.toString())) {
                startLimit(editText.text.toString().toLong() * 60)
            } else {
                startLimit()
            }
        }
        btn_set_launcher.setOnClickListener { clearDefaultLauncher() }
    }

    private fun displaySetting() {

        dialog = AlertDialog.Builder(this).setTitle(R.string.tips_launcher_setting)
            .setPositiveButton(R.string.action_default_apps_setting) { dialog, id ->
                //去设置默认桌面
                openDefaultAppsSetting()
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                tempInputTimeMinute = -1
            }
            .setCancelable(false)
            .create()

        //tips设置
        if (DeviceHelper.isHuawei()) {
            tips_huawei.visibility = View.VISIBLE
        } else {
//            btn_set_launcher.visibility = View.VISIBLE
            if (android.os.Build.BRAND == "OnePlus") {
                tips_normal.visibility = View.VISIBLE
            } else if (PermissionManager.isRooted()) {
                tips_root.visibility = View.VISIBLE
            } else {
                tips_normal.visibility = View.VISIBLE
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
                        ToastUtil.showLong(applicationContext, "限制已解除")
                        LimitApplication.isOnLimitation = false
                    }
                }

                override fun onLimitStarted() {
                    runOnUiThread {
                        ToastUtil.showLong(this@MainActivity, "限制已开启")
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


//        btn_set_launcher.visibility = View.INVISIBLE

        //开启之前需要把权限获取到位
        val limitIntent = Intent(this, LimitService::class.java)
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
        val stopIntent = Intent(this, LimitService::class.java)
        this.stopService(stopIntent)

        if (!LimitApplication.isHuawei) {
            clearDefaultLauncher()
        }
    }


    private fun checkAndRecoveryLimitTask() {
        val sp: SharedPreferences = getSharedPreferences("LimitStatus", Context.MODE_PRIVATE)
        val remainTimeSeconds = sp.getLong("remain_time_seconds", 0)
        if (remainTimeSeconds > 1) {
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
