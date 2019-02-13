package com.lfork.phonelimitadvanced.statistics

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.lfork.phonelimitadvanced.R

class StatisticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_act)
        val button = findViewById<View>(R.id.OpenButton) as Button
        button.setOnClickListener {
            try {
                if (!isStatAccessPermissionSet(this@StatisticActivity)) {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))   //查看是否为应用设置了权限
                    val toast = Toast.makeText(
                        applicationContext,
                        "请开启应用统计的使用权限",
                        Toast.LENGTH_SHORT
                    )    //显示toast信息
                    toast.show()
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            if (isStatAccessPermissionSet(this)) {
                val intent3 = Intent(this@StatisticActivity, AppStatisticsList::class.java)
                startActivity(intent3)
                finish()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(PackageManager.NameNotFoundException::class)
    private fun isStatAccessPermissionSet(c: Context): Boolean {
        val pm = c.packageManager
        val info = pm.getApplicationInfo(c.packageName, 0)
        val aom = c.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName)
        return aom.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            info.uid,
            info.packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

}
