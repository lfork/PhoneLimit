package com.lfork.phonelimitadvanced.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.browser.BrowserFragment
import com.lfork.phonelimitadvanced.main.focus.CustomIconOnClickListener
import com.lfork.phonelimitadvanced.main.focus.FocusFragment2
import com.lfork.phonelimitadvanced.main.settings.SettingsFragment
import com.lfork.phonelimitadvanced.base.permission.PermissionManager.clearDefaultLauncherFake
import com.lfork.phonelimitadvanced.utils.setupToolBar
import kotlinx.android.synthetic.main.main2_act.*
import android.view.View
import com.lfork.phonelimitadvanced.base.widget.LimitModelSelectDialog
import com.lfork.phonelimitadvanced.base.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimitadvanced.limit.task.FloatingLimitTask
import com.lfork.phonelimitadvanced.timedtask.TimedTaskActivity
import com.lfork.phonelimitadvanced.utils.startActivity


class MainActivity : AppCompatActivity() {

    private var focusFragment: FocusFragment2? = null


    private val mOnNavigationItemSelectedListener = object : CustomIconOnClickListener {
        override fun onBrowserClick() {
            openSecondFragment(BrowserFragment())
        }

        override fun onSettingsClick() {
            openSecondFragment(SettingsFragment())
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        //当前的界面的保存状态，只是重新让新的Fragment指向了原本未被销毁的fragment，它就是onAttach方法对应的Fragment对象
        if (focusFragment == null && fragment is FocusFragment2) {
            focusFragment = fragment
            focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main2_act)
        setupToolBar(toolbar, "Phone Limit")
        initFragments()
    }


    var limitModelMenuItem: MenuItem? = null


    var limitModelSelectionDialog: LimitModelSelectDialog? = null

    override fun onResume() {
        super.onResume()
        FloatingLimitTask.isOnRecentApps = false
        limitModelSelectionDialog?.onResume()
    }

    private fun showLimitModelSelectionDialog() {
        limitModelSelectionDialog = LimitModelSelectDialog(this)
        limitModelSelectionDialog?.show()

        limitModelSelectionDialog?.limitModelUpdateListener =
                object : LimitModelSelectDialog.LimitModelUpdateListener {
                    override fun updateLimitModel(model: String) {
                        limitModelMenuItem?.title = model
                    }
                }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.limit_menu, menu)
        limitModelMenuItem = menu?.findItem(R.id.limit_model)
        limitModelMenuItem?.title =
                resources.getStringArray(R.array.limit_model_array)[LimitApplication.defaultLimitModel]
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.limit_model -> {
                showLimitModelSelectionDialog()
            }
            R.id.timed_task -> {
                startActivity<TimedTaskActivity>()
            }
            R.id.settings -> {
                openSecondFragment(SettingsFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        //清楚Fake桌面，不然在选择默认桌面的时候fake activity会出现在列表当中
        clearDefaultLauncherFake()

    }


    override fun onDestroy() {
        super.onDestroy()
        limitModelSelectionDialog?.dismiss()
    }

    override fun onBackPressed() {

        //碎片之间的返回
        if (!focusFragment!!.isVisible) {
            super.onBackPressed()
            return
        }

        if (LimitApplication.isOnLimitation) {
            return
        }

        if (LimitApplication.isTimedTaskRunning && !isDefaultLauncher()) {
            val homeIntent = Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeIntent);
            return
        }
        super.onBackPressed()
    }

    private fun initFragments() {
        if (focusFragment != null) {
            return
        }
        focusFragment = FocusFragment2()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_frag, focusFragment!!, focusFragment!!.tag)
            .show(focusFragment!!)
            .commit()
        focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)
    }


    private fun openSecondFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frag, fragment)
            .addToBackStack(null)
            .commit()

        if (fragment !is FocusFragment2) {
            hideToolBar()
        }
    }


    fun hideOtherUI() {
        hideToolBar()
//        setSystemUIVisible(false)
    }

    fun hideToolBar() {
        toolbar.visibility = View.INVISIBLE
    }

    fun showToolBar() {
        toolbar.visibility = View.VISIBLE
    }


    fun showOtherUI() {
        showToolBar()
//        setSystemUIVisible(true)
    }


}
