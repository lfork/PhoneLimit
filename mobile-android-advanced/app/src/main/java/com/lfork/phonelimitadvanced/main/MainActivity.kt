package com.lfork.phonelimitadvanced.main

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.lfork.phonelimitadvanced.CallBack
import com.lfork.phonelimitadvanced.PLShell
import com.lfork.phonelimitadvanced.PermissionManager
import com.lfork.phonelimitadvanced.PermissionManager.requestStoragePermission
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.util.FileHelper
import com.lfork.phonelimitadvanced.util.FileHelper.listDirectory
import com.lfork.phonelimitadvanced.util.ToastUtil
import com.stericson.RootShell.RootShell
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import kotlinx.android.synthetic.main.main_act.*
import java.io.File
import java.lang.StringBuilder

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
        sample_text.text = stringFromJNI()
        get_root.setOnClickListener { PermissionManager.getRootPermission(packageCodePath) }
        list_files.setOnClickListener {
            if (PermissionManager.isGrantedStoragePermission(applicationContext)) {
//                sample_text.text = listDirectory(FileHelper.SDRootPath)
//                if (dir_path != null) {
//                    sample_text.text = listDirectory("${dir_path.text}")
//                }
//
//                val cmd = Command(1,"ls -l")
//                val shell = RootTools.getShell(true)
//
//                val result = shell.add(cmd)
//                result.commandOutput(1, "???")
                listDirectory(sample_text.text.toString())

            }
        }
        btn_execute_shell.setOnClickListener {
            if(!TextUtils.isEmpty(args_input.text.toString())){
//                PLShell.shellExec(args_input.text.toString())
//                sample_text.text = PLShell.execRootCmd(args_input.text.toString())
                PLShell.asyncExecRootCmd(args_input.text.toString(),object : CallBack<String> {
                    override fun succeed(result: String) {
                        Log.d("ShellTest2", result)
                        runOnUiThread {
                            sample_text.text=result
                        }
                    }

                    override fun failed(log: String) {
                    }
                })
            } else{
            }
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
