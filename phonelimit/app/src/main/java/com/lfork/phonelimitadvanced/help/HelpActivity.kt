package com.lfork.phonelimitadvanced.help

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import com.lfork.phonelimitadvanced.R
import kotlinx.android.synthetic.main.help_act.*


class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_act)


////        val cacheDirPath = filesDir.absolutePath + APP_CACAHE_DIRNAME
//        val settings = webview.getSettings()
//        settings.cacheMode = WebSettings.LOAD_CACHE_ONLY
    }

    override fun onStart() {
        super.onStart()
        webview.loadUrl("file:///android_asset/web/index.html");
    }
}
