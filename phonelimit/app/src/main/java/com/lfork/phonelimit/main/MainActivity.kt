package com.lfork.phonelimit.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.R
import com.lfork.phonelimit.main.browser.BrowserFragment
import com.lfork.phonelimit.main.focus.CustomIconOnClickListener
import com.lfork.phonelimit.main.focus.FocusFragment
import com.lfork.phonelimit.main.settings.SettingsFragment
import com.lfork.phonelimit.base.permission.PermissionManager.clearDefaultLauncherFake
import kotlinx.android.synthetic.main.main2_act.*
import android.view.View
import com.lfork.phonelimit.base.widget.LimitModelSelectDialog
import com.lfork.phonelimit.base.permission.PermissionManager.isDefaultLauncher
import com.lfork.phonelimit.data.getBackgroundFilePath
import com.lfork.phonelimit.data.getMainMenuVisibility
import com.lfork.phonelimit.limitcore.LimitEnvironment.isOnRecentApps
import com.lfork.phonelimit.main.settings.SettingsChangeManager
import com.lfork.phonelimit.timedtask.TimedTaskActivity
import com.lfork.phonelimit.utils.*


class MainActivity : AppCompatActivity() {

    private var focusFragment: FocusFragment? = null
    private var browserFragment: BrowserFragment? = null
    private var settingsFragment: SettingsFragment? = null

    private val mOnNavigationItemSelectedListener = object : CustomIconOnClickListener {
        override fun onBrowserClick() {
            openSecondFragment(BrowserFragment())

        }

        override fun onSettingsClick() {
            openSecondFragment(SettingsFragment())
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }


    val TAG = "MainActivity"
    /**
     * 如果activity被回收，系统会在这里恢复之前的fragment
     */
    override fun onAttachFragment(fragment: Fragment?) {
//        super.onAttachFragment(fragment)
        //当前的界面的保存状态，只是重新让新的Fragment指向了原本未被销毁的fragment，它就是onAttach方法对应的Fragment对象
        //防止重影
//        Log.d(TAG, "onAttachFragment" + fragment?.toString() + "   ${focusFragment?.hashCode()}")
//        if (focusFragment == null && fragment is FocusFragment) {
//            focusFragment = fragment
//            focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)
//        } else if (browserFragment == null && fragment is BrowserFragment) {
//            browserFragment = fragment
//        } else if (settingsFragment == null && fragment is SettingsFragment) {
//            settingsFragment = fragment
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main2_act)
        setupToolBar(toolbar, "Phone Limit")
        initFragments()
        setBackground()
        setTransparentSystemUI()
        SettingsChangeManager.addListener(listener)

    }

    fun setBackground() {
        val bgPath = getBackgroundFilePath()

        if (bgPath != null) {
            val drawable = Drawable.createFromPath(bgPath)
            iv_bg.setImageDrawable(drawable)
            window.setBackgroundDrawable(ColorDrawable(0))
        } else {
            iv_bg.setImageDrawable(getDrawable(R.drawable.mountain))
            window.setBackgroundDrawable(ColorDrawable(0))
//            window.setBackgroundDrawable(getDrawable(R.drawable.mountain))
        }
    }

    var limitModelMenuItem: MenuItem? = null


    var limitModelSelectionDialog: LimitModelSelectDialog? = null

    override fun onResume() {
        super.onResume()
        isOnRecentApps = false
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if (getMainMenuVisibility() == true) {
            menu?.findItem(R.id.timed_task)?.isVisible = false;
            menu?.findItem(R.id.settings)?.isVisible = false;
        }

        return super.onPrepareOptionsMenu(menu)
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

    val listener = object : SettingsChangeManager.SettingsChangeListener {
        override fun onBackgroundChanged() {
            setBackground()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SettingsChangeManager.removeListener(listener)
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
        } else {
            focusFragment?.unbindLimitService()
        }
        super.onBackPressed()
    }

    private fun initFragments() {

        //清除由于activity被回收，然后可能保存的一些错误的回退栈的信息。
        supportFragmentManager.popBackStackImmediate()
        if (focusFragment != null) {
            return
        }
        focusFragment = FocusFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frag, focusFragment!!, focusFragment!!.tag)
            .show(focusFragment!!)
            .commit()
        focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)


    }


    private fun openSecondFragment(fragment: Fragment) {
        Log.d(TAG, fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frag, fragment)
            .addToBackStack(null)
            .commit()
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
