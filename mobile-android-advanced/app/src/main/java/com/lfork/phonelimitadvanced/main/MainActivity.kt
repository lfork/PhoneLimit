package com.lfork.phonelimitadvanced.main

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.main.browser.BrowserFragment
import com.lfork.phonelimitadvanced.main.focus.FocusFragment
import com.lfork.phonelimitadvanced.main.my.MyFragment
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncherFake
import kotlinx.android.synthetic.main.main2_act.*

class MainActivity : AppCompatActivity(), BrowserFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var fragments = HashMap<Int, Fragment>()

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(fragments[FRAG_FOCUS]!!)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    replaceFragment(fragments[FRAG_BROWSER]!!)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(fragments[FRAG_MY]!!)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main2_act)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initFragments()
    }


    override fun onPause() {
        super.onPause()
        //清楚Fake桌面，不然在选择默认桌面的时候fake activity会出现在列表当中
        clearDefaultLauncherFake()
    }

    override fun onBackPressed() {
        if (!LimitApplication.isOnLimitation) {
            super.onBackPressed()
        }
        runOnUiThread { }
    }

    companion object {
        private const val FRAG_FOCUS = 0
        private const val FRAG_BROWSER = 1
        private const val FRAG_MY = 2
    }

    private fun initFragments() {

        if (fragments.size > 1) {
            return
        }

        fragments[FRAG_FOCUS] = FocusFragment()
        fragments[FRAG_BROWSER] = BrowserFragment()
        fragments[FRAG_MY] = MyFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main_frag, fragments[FRAG_BROWSER]!!, "FRAG_FOCUS")
            .hide(fragments[FRAG_BROWSER]!!)
        transaction.add(R.id.main_frag, fragments[FRAG_MY]!!, "FRAG_BROWSER")
            .hide(fragments[FRAG_MY]!!)
        transaction.add(R.id.main_frag, fragments[FRAG_FOCUS]!!, "FRAG_MY")
            .show(fragments[FRAG_FOCUS]!!);
        transaction.commit()

        mCurrentFragment = fragments[FRAG_FOCUS]!!
    }

    lateinit var mCurrentFragment: Fragment

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .hide(mCurrentFragment)
            .show(fragment)
            .commit()
        mCurrentFragment = fragment
    }

}
