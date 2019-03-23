package com.lfork.phonelimit

import android.support.v7.app.AppCompatActivity

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/12 20:21
 */
abstract class LimitActivity : AppCompatActivity(), LimitApplication.LimitSwitchListener {

    override fun onResume() {
        super.onResume()
        LimitApplication.registerSwitchListener(this)
    }

    override fun onPause() {
        super.onPause()
        LimitApplication.unregisterSwitchListener(this)
    }

    override fun onStartLimit() {
        finish()
    }

    override fun onCloseLimit() {
        finish()
    }
}