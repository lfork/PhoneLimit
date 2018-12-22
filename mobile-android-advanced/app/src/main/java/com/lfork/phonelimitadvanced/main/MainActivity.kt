package com.lfork.phonelimitadvanced.main

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.browser.BrowserFragment
import com.lfork.phonelimitadvanced.main.focus.CustomIconOnClickListener
import com.lfork.phonelimitadvanced.main.focus.FocusFragment
import com.lfork.phonelimitadvanced.main.my.MyFragment
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncherFake
import kotlinx.android.synthetic.main.main2_act.*

class MainActivity : AppCompatActivity() {

    private var focusFragment:FocusFragment?= null


    private val mOnNavigationItemSelectedListener =object : CustomIconOnClickListener {
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
        if (focusFragment == null && fragment is FocusFragment) {
            focusFragment= fragment
            focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main2_act)

        initFragments()
    }

    override fun onPause() {
        super.onPause()
        //清楚Fake桌面，不然在选择默认桌面的时候fake activity会出现在列表当中
        clearDefaultLauncherFake()
    }

    override fun onBackPressed() {
        if (!LimitApplication.isOnLimitation || !focusFragment!!.isVisible) {
            super.onBackPressed()
        }
    }

    private fun initFragments() {

        if (focusFragment!=null) {
            return
        }
        focusFragment= FocusFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frag, focusFragment!!, focusFragment!!.tag)
            .show(focusFragment!!)
            .commit()
        focusFragment!!.setCustomClickListener(mOnNavigationItemSelectedListener)
    }


    private fun openSecondFragment(fragment: Fragment) {
        Log.d("FragmentTest2", fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frag, fragment)
            .addToBackStack(null)
            .commit()
    }
}
