package com.lfork.devicetest

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    //        btn_start.setOnClickListener {
//            LimitService.startLimit(this)
//            disableComponent(this, "com.lfork.devicetest.SecondActivity")
//        }
//
//        btn_close.setOnClickListener {
//            enableComponent(this, "com.lfork.devicetest.SecondActivity")
//        }

    private fun enableComponent(context: Context, klass: String) {
        val name = ComponentName(context, klass)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun disableComponent(context: Context, klass: String) {
        val name = ComponentName(context, klass)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    // 激活程序
    fun OnActivate(v: View?) {
        DeviceMethod.getInstance(this).onActivate()
    }

    // 移除程序 如果不移除程序 APP无法被卸载
    fun OnRemoveActivate(v: View) {
        DeviceMethod.getInstance(this).onRemoveActivate()
    }

    // 设置解锁方式 不需要激活就可以运行
    fun btnszmm(v: View) {
        DeviceMethod.getInstance(this).startLockMethod()
    }

    // 设置解锁方式
    fun btnmm(v: View) {
        DeviceMethod.getInstance(this).setLockMethod()
    }

    // 立刻锁屏
    fun btnlock(v: View) {
        DeviceMethod.getInstance(this).LockNow()
    }

    // 设置5秒后锁屏
    fun btnlocktime(v: View) {
        DeviceMethod.getInstance(this).LockByTime(5000)
    }

    // 恢复出厂设置
    fun btnwipe(v: View) {
        DeviceMethod.getInstance(this).WipeData()
    }

    // 设置密码锁
    fun setPassword(v: View) {
        DeviceMethod.getInstance(this).setPassword("1234")

    }
}
