package com.lfork.phonelimitadvanced.statistic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lfork.phonelimitadvanced.R

class StatisticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_act)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StatisticFragment.newInstance())
                .commitNow()
        }
    }

}
