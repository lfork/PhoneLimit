package com.lfork.phonelimitadvanced.main.focus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.utils.PermissionManager.clearDefaultLauncherFake

class FakeHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fake_home_act)

    }

}
