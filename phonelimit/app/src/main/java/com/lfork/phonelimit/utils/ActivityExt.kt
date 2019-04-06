package com.lfork.phonelimit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import java.util.*

/**
 *
 * Created by 98620 on 2018/11/25.
 */


fun AppCompatActivity.setupActionBar() {
    val actionBar = supportActionBar
    Objects.requireNonNull<ActionBar>(actionBar).setDisplayShowTitleEnabled(true)
//    actionBar!!.title = title
    // 决定左上角图标的右侧是否有向左的小箭头, true
    actionBar!!.setDisplayHomeAsUpEnabled(true)
    // 有小箭头，并且图标可以点击
    actionBar.setDisplayShowHomeEnabled(false)
}

fun AppCompatActivity.setupToolBar(toolbar: Toolbar, title: String="",needBackArrow:Boolean = false) {
    setSupportActionBar(toolbar)
    val actionBar = supportActionBar
    if (needBackArrow){
        actionBar?.setDisplayHomeAsUpEnabled(true)  //设置返回按钮，需要在监听里面实现返回功能   onOptionsItemSelected(MenuItem item)
    }
    actionBar?.title = title
}


/**
 * 内联函数的类型参数能够被实化。
 * 简单的启动一个activity
 */
inline fun <reified T : Activity> Activity.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * 内联函数的类型参数能够被实化。
 * 简单的启动一个activity
 */
inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}


fun  Context.startOtherApp(packageName:String) {
    val resolveIntent = packageManager.getLaunchIntentForPackage(packageName);
    startActivity(resolveIntent)
}


fun Activity.setTransparentSystemUI(){
    // Android 5.0 以上 全透明
    val window = getWindow();
    window.clearFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    window.getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
    window.setStatusBarColor(Color.TRANSPARENT);
    // 虚拟导航键
    window.setNavigationBarColor(Color.TRANSPARENT);
}
