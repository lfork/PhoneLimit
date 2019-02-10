package com.lfork.phonelimitadvanced.main

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
import com.lfork.phonelimitadvanced.main.my.MyFragment
import com.lfork.phonelimitadvanced.permission.PermissionManager.clearDefaultLauncherFake
import com.lfork.phonelimitadvanced.utils.setupToolBar
import kotlinx.android.synthetic.main.main2_act.*
import android.view.View
import com.lfork.phonelimitadvanced.base.widget.LimitModelSelectDialog
import com.lfork.phonelimitadvanced.timedtask.TimedTaskActivity
import com.lfork.phonelimitadvanced.utils.setSystemUIVisible
import com.lfork.phonelimitadvanced.utils.startActivity


class MainActivity : AppCompatActivity() {

    private var focusFragment: FocusFragment2? = null


    private val mOnNavigationItemSelectedListener = object : CustomIconOnClickListener {
        override fun onBrowserClick() {
            openSecondFragment(BrowserFragment())
        }

        override fun onSettingsClick() {
            openSecondFragment(MyFragment())
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val w = window // in Activity's onCreate() for instance
//            w.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//        }
    }


    var limitModelMenuItem: MenuItem? = null


    var limitModelSelectionDialog: LimitModelSelectDialog? = null

    override fun onResume() {
        super.onResume()
        limitModelSelectionDialog?.onResume()
    }

    fun showLimitModelSelectionDialog() {
        limitModelSelectionDialog = LimitModelSelectDialog(this)
        limitModelSelectionDialog?.show()

        limitModelSelectionDialog?.limitModelUpdateListener = object :LimitModelSelectDialog.LimitModelUpdateListener{
            override fun updateLimitModel(model: String) {
                limitModelMenuItem?.title = model
            }
        }
//        limitModelSelectionDialog?.permissionCheckerAndRequester = focusFragment
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.limit_menu, menu)
        limitModelMenuItem = menu?.findItem(R.id.limit_model)
        limitModelMenuItem?.title = resources.getStringArray(R.array.limit_model_array)[LimitApplication.defaultLimitModel]
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
        if (!LimitApplication.isOnLimitation || !focusFragment!!.isVisible) {
            super.onBackPressed()
        }
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

        if (fragment !is FocusFragment2){
            hideToolBar()
        }
    }


    fun hideOtherUI() {
        hideToolBar()
        setSystemUIVisible(false)
    }

    fun hideToolBar(){
        toolbar.visibility = View.GONE
    }

    fun showToolBar(){
        toolbar.visibility = View.VISIBLE
    }


    fun showOtherUI() {
        showToolBar()
        setSystemUIVisible(true)
    }


}
