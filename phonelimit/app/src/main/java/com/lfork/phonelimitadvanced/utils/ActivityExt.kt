package com.lfork.phonelimitadvanced.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
