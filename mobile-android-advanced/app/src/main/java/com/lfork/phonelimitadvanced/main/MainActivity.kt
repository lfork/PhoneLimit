package com.lfork.phonelimitadvanced.main

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimitadvanced.utils.PermissionManager
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.limit.LimitService
import com.lfork.phonelimitadvanced.limit.LimitStateListener
import com.lfork.phonelimitadvanced.utils.LockUtil
import com.lfork.phonelimitadvanced.utils.QMUIDeviceHelper
import com.lfork.phonelimitadvanced.utils.ToastUtil
import com.lfork.phonelimitadvanced.widget.DialogPermission
import kotlinx.android.synthetic.main.main_act.*

class MainActivity :
        AppCompatActivity() { //, AdapterView.OnItemSelectedListener, OnClickListener, SwipeRefreshLayout.OnRefreshListener, RadioGroup.OnCheckedChangeListener

    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)
        setupToolbar()

        btn_start.setOnClickListener {
            if (!TextUtils.isEmpty(editText.text.toString())) {
                startLimit(editText.text.toString().toLong() * 60)
            } else {
                startLimit()
            }
        }

        btn_close.setOnClickListener { closeLimit() }
        btn_set_launcher.setOnClickListener { setDefaultLauncher() }
        checkAndRecoveryLimitTask()
//        test_recycle.layoutManager = LinearLayoutManager(this)
//        test_recycle.adapter = MyRecycleAdapter()
        requestPermission()
    }


    private fun requestPermission() {
        //申请获取使用情况的权限
        requestStateUsagePermission()

        //如果是华为手机 就申请悬浮窗的权限 否则就申请默认桌面
        if (QMUIDeviceHelper.isHuawei()) {
            PermissionManager.requestFloatingWindowPermission(this)
        } else {
            setDefaultLauncher()
        }

    }

    private fun setupToolbar() {
        main_toolbar.title = resources.getString(R.string.app_name)
    }

    /**
     * 弹出dialog requestStateUsagePermission
     */
    private fun requestStateUsagePermission() {
        //如果没有获得查看使用情况权限和手机存在查看使用情况这个界面
        if (!LockUtil.isStatAccessPermissionSet(this@MainActivity) && LockUtil.isNoOption(this@MainActivity)) {
            val dialog = DialogPermission(this@MainActivity)
            dialog.show()
            dialog.setOnClickListener {
                val RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LimitService.StateBinder

            binder.getLimitService().listener = object : LimitStateListener {
                override fun onLimitFinished() {
                    runOnUiThread { ToastUtil.showLong(applicationContext, "限制已解除") }

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

//        if (!PermissionManager.isGrantedStoragePermission(applicationContext)) {
//            ToastUtil.showShort(this, "请给与程序需要的权限")
//            requestStoragePermission(applicationContext, REQUEST_STORAGE_PERMISSION, this)
//            return
//        }

        if (PermissionManager.checkRootPermission()) {
            if (!PermissionManager.getRootAhth()) {
                PermissionManager.getRootPermission(packageCodePath)

                if (!PermissionManager.getRootAhth()) {
                    ToastUtil.showShort(this, "请授予Root权限")
                    return
                } else{

                    //TODO 如果是root设备 就把相应的app给停用掉
                }
            }
        }


        val limitIntent = Intent(this, LimitService::class.java)
        limitIntent.putExtra("limit_time", limitTimeSeconds)

        bindService(limitIntent, connection, Context.BIND_AUTO_CREATE)
        startService(limitIntent)
    }

    private fun closeLimit() {
        unbindService(connection)
        val stopIntent = Intent(this, LimitService::class.java)
        this.stopService(stopIntent)
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

    private fun setDefaultLauncher() {
        val c = this
        val p = c.packageManager
        val cN = ComponentName(c, FakeHomeActivity::class.java)
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        c.startActivity(selector)
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }

    /**
     * 暂时还不需要访问文件的权限
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            //TODO 权限申请
            REQUEST_STORAGE_PERMISSION -> {
                if (PermissionManager.isGrantedStoragePermission(applicationContext)) {
                    if (!TextUtils.isEmpty(editText.text.toString())) {
                        startLimit(editText.text.toString().toLong())
                    } else {
                        startLimit()
                    }
                }
            }
        }
    }
}
