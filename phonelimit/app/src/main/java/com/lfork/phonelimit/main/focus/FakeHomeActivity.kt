package com.lfork.phonelimit.main.focus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lfork.phonelimit.R
import com.lfork.phonelimit.main.MainActivity
import com.lfork.phonelimit.utils.startActivity

class FakeHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fake_home_act)
        finish()
        startActivity<MainActivity>()
    }

}
