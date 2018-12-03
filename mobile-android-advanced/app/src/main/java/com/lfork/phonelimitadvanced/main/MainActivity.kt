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
import kotlinx.android.synthetic.main.main2_act.*

class MainActivity : AppCompatActivity(),BrowserFragment.OnFragmentInteractionListener {

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


    override fun onBackPressed() {
        if (!LimitApplication.isOnLimitation) {
            super.onBackPressed()
        }

        runOnUiThread {  }
    }



    companion object {
        private const val FRAG_FOCUS = 0
        private const val FRAG_BROWSER = 1
        private const val FRAG_MY = 2
    }


    private fun initFragments() {
        fragments[FRAG_FOCUS] = FocusFragment()
        fragments[FRAG_BROWSER] = BrowserFragment()
        fragments[FRAG_MY] = MyFragment()
        replaceFragment(fragments[FRAG_FOCUS]!!)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.main_frag, fragment)
        transaction.commit()
    }

}
