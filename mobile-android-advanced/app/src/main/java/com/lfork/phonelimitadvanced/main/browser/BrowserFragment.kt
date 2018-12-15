package com.lfork.phonelimitadvanced.main.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.browser.WebViewActivity
import kotlinx.android.synthetic.main.browser_frag.view.*

class BrowserFragment : Fragment() {

    private var root: View? = null

    private lateinit var inputBox: EditText

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.browser_frag, container, false)
            inputBox = root!!.edit_url
            setupUrlInputListener()
        }
        return root
    }

    private fun setupUrlInputListener() {
        inputBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (!TextUtils.isEmpty(inputBox.text)) {
                    Log.i("---", "搜索操作执行");
                    WebViewActivity.loadUrl(context!!, inputBox.text.toString(),"test")
                }
            }
            false;
        }
    }
}
