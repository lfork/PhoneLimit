package com.lfork.phonelimitadvanced.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.lfork.phonelimitadvanced.limit.PLShell
import com.lfork.phonelimitadvanced.PermissionManager
import com.lfork.phonelimitadvanced.PermissionManager.requestStoragePermission
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.limit.AutoLimitService
import com.lfork.phonelimitadvanced.limit.PhoneLimitController
import com.lfork.phonelimitadvanced.util.FileHelper.listDirectory
import com.lfork.phonelimitadvanced.util.ToastUtil
import kotlinx.android.synthetic.main.main_act.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_STORAGE_PERMISSION = 0

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)

        // Example of a call to a native method
       // sample_text.text = stringFromJNI()

        btn_start.setOnClickListener {

            if (!PermissionManager.getRootAhth())
                PermissionManager.getRootPermission(packageCodePath)

            if (!PermissionManager.isGrantedStoragePermission(applicationContext)) {
                ToastUtil.showShort(this, "请授予程序必须的权限")
                requestStoragePermission(applicationContext, REQUEST_STORAGE_PERMISSION, this);
            }

            val startIntent = Intent(it.context, AutoLimitService::class.java)
            it.context.startService(startIntent)
        }

        btn_close.setOnClickListener {
            val stopIntent = Intent(it.context, AutoLimitService::class.java)
            it.context.stopService(stopIntent)

        }



        requestStoragePermission(applicationContext, REQUEST_STORAGE_PERMISSION, this);
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (PermissionManager.isGrantedStoragePermission(applicationContext)) {
                ToastUtil.showShort(applicationContext, "获取文件访问权限成功")
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String


}
