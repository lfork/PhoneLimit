package com.lfork.phonelimitadvanced.whitename

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.lfork.phonelimitadvanced.LimitApplication.Companion.App
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.utils.setupToolBar
import kotlinx.android.synthetic.main.white_name_edit_act.*

class WhiteNameEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.white_name_edit_act)
        recycle_white_list.layoutManager = LinearLayoutManager(this)
        val adapter = WhiteNameRecycleAdapter()
        adapter.setItems(App.getAllAppsInfo())
        recycle_white_list.adapter = adapter
        setupToolBar(toolbar,"请选择白名单应用")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
