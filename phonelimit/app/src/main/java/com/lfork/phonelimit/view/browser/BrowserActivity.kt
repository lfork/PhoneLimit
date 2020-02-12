package com.lfork.phonelimit.view.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.R
import com.lfork.phonelimit.view.browser.config.*
import com.lfork.phonelimit.view.browser.utils.StatusBarUtil
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.urlinfo.UrlInfoRepository
import com.lfork.phonelimit.utils.getUrlDomainName
import kotlinx.android.synthetic.main.browser_act.*

/**
 * 网页可以处理:
 * 点击相应控件：
 * - 拨打电话、发送短信、发送邮件
 * - 上传图片(版本兼容)
 * - 全屏播放网络视频
 * - 进度条显示
 * - 返回网页上一层、显示网页标题
 * JS交互部分：
 * - 前端代码嵌入js(缺乏灵活性)
 * - 网页自带js跳转
 * 被作为第三方浏览器打开
 */
class BrowserActivity : AppCompatActivity(), IWebPageView {

    // 进度条
    private var mProgressBar: ProgressBar? = null
    private var webView: WebView? = null
    // 全屏时视频加载view
    var videoFullView: FrameLayout? = null
        private set
    // 加载视频相关
    private var mWebChromeClient: MyWebChromeClient? = null
    // 网页链接
    private var mUrl: String? = null
    private var mTitleToolBar: Toolbar? = null
    // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
    private var tvGunTitle: TextView? = null
    private var mTitle: String? = null
    private var webClient: MyWebViewClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_act)
        getIntentData()
        initTitle()
        initWebView()
        webView!!.loadUrl(mUrl)
        getDataFromBrowser(intent)
        setupLimitView()
        webview_refresh.setOnRefreshListener { webView?.reload() }
    }


    private fun getIntentData() {
        mUrl = intent.getStringExtra("mUrl")
        mTitle = intent.getStringExtra("mTitle")
    }


    private fun initTitle() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0)
        mProgressBar = findViewById(R.id.pb_progress)
        webView = findViewById(R.id.webview_detail)
        videoFullView = findViewById(R.id.video_fullView)
        mTitleToolBar = findViewById(R.id.title_tool_bar)
        tvGunTitle = findViewById(R.id.tv_gun_title)
        initToolBar()
    }

    private fun initToolBar() {
        setSupportActionBar(mTitleToolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        mTitleToolBar!!.overflowIcon =
                ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp)
        tvGunTitle!!.postDelayed({ tvGunTitle!!.isSelected = true }, 1900)
        setTitle(mTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_webview, menu)
        addOrDeleteItem = menu.findItem(R.id.actionbar_cope)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home// 返回键
            -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            } else {
                finish()
            }
            R.id.actionbar_cope
            -> if (!TextUtils.isEmpty(webView!!.url)) {
                UrlInfoRepository.addOrDeleteUrl(
                    getUrlDomainName(webView!!.url),
                    object : DataCallback<String> {
                        override fun succeed(data: String) {
                            runOnUiThread {
                                if (data == "添加成功") {
                                    item.title = "从白名单中移除"
                                    Toast.makeText(this@BrowserActivity, data, Toast.LENGTH_LONG)
                                        .show()
                                } else if (data == "删除成功") {
                                    item.title = "添加到白名单"
                                    Toast.makeText(this@BrowserActivity, data, Toast.LENGTH_LONG)
                                        .show()
                                    if (LimitApplication.isOnLimitation) {
                                        //重新加载页面
                                        resetWebBrowser()
                                    }
                                }

                            }
                        }

                        override fun failed(code: Int, log: String) {
                            runOnUiThread {
                                ToastUtils.show("error$code $log ")
                            }
                        }
                    })
            }
            R.id.actionbar_webview_refresh// 刷新页面
            -> if (webView != null) {
                webView!!.reload()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {
        mProgressBar!!.visibility = View.VISIBLE
        val ws = webView!!.settings
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.loadWithOverviewMode = false
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = true
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        ws.cacheMode = WebSettings.LOAD_DEFAULT

        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.useWideViewPort = true
        // 不缩放
        webView!!.setInitialScale(100)
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView是否新窗口打开(加了后可能打不开网页)
        ws.setSupportMultipleWindows(false)

        ws.userAgentString

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用) */
        ws.textZoom = 100

        mWebChromeClient = MyWebChromeClient(this)
        webView!!.webChromeClient = mWebChromeClient
        // 与js交互
        webView!!.addJavascriptInterface(ImageClickInterface(this), "injectedObject")
        webClient = MyWebViewClient(this)
        webView!!.webViewClient = webClient


        webView!!.setOnLongClickListener(View.OnLongClickListener {
            val hitTestResult = webView!!.hitTestResult
            // 如果是图片类型或者是带有图片链接的类型
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE || hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                // 弹出保存图片的对话框
                AlertDialog.Builder(this@BrowserActivity)
                    .setItems(arrayOf("查看大图", "保存图片到相册")) { dialog, which ->
                        val picUrl = hitTestResult.extra
                        //获取图片
                        Log.e("picUrl", picUrl)
                        when (which) {
                            0 -> {
                            }
                            1 -> {
                            }
                            else -> {
                            }
                        }
                    }
                    .show()
                return@OnLongClickListener true
            }
            false
        })

    }


    /**
     * 全屏时按返加键执行退出全屏方法
     */
    private fun hideCustomView() {
        mWebChromeClient!!.onHideCustomView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 上传图片之后的回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient!!.mUploadMessage(intent, resultCode)
        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient!!.mUploadMessageForAndroid5(intent, resultCode)
        }
    }


    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getDataFromBrowser(intent)
    }

    /**
     * 作为三方浏览器打开
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private fun getDataFromBrowser(intent: Intent) {
        val data = intent.data
        if (data != null) {
            try {
                val scheme = data.scheme
                val host = data.host
                val path = data.path
                val text = "Scheme: $scheme\nhost: $host\npath: $path"
                Log.e("data", text)
                val url = "$scheme://$host$path"
                webView!!.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //全屏播放退出全屏
            if (mWebChromeClient!!.inCustomView()) {
                hideCustomView()
                return true

                //返回网页上一页
            } else if (webView!!.canGoBack()) {
                webView!!.goBack()
                return true

                //退出网页
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition()
                } else {
                    finish()
                }
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        webView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView!!.onResume()
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView!!.resumeTimers()
        // 设置为横屏
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        videoFullView!!.removeAllViews()
        if (webView != null) {
            val parent = webView!!.parent as ViewGroup
            parent.removeView(webView)
            webView!!.removeAllViews()
            webView!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView!!.stopLoading()
            webView!!.webChromeClient = null
            webView!!.webViewClient = null
            webView!!.destroy()
            webView = null
            webClient = null
        }
        super.onDestroy()
    }


    private var lastUrl: String? = null

    private var addOrDeleteItem: MenuItem? = null

    private fun setupLimitView() {

        //防止点击穿透
        item_limit_tips.setOnTouchListener { _, _ -> true }
        btn_close_limit.setOnClickListener { closeLimit() }
        webClient?.setWebLoadedListener {
            lastUrl = it
            webview_refresh.isRefreshing = false
            if (it == "https://cn.bing.com" || it == "www.bing.com") {
                addOrDeleteItem?.title = ""
            } else if (urlIsInWhiteNameList(it)) {
                addOrDeleteItem?.title = "从白名单中移除"
            } else {
                addOrDeleteItem?.title = "添加到白名单"
                if (LimitApplication.isOnLimitation) {
                    doLimit(it)
                }
            }


        }

        btn_add_to_white_name.setOnClickListener {

            val callback = object : DataCallback<String> {
                override fun succeed(data: String) {
                    closeLimit()
                    ToastUtils.show(data)
                }

                override fun failed(code: Int, log: String) {
                    ToastUtils.show("$log ")
                }
            }
            UrlInfoRepository.addNextUrl(site_info.text.toString(), callback)
        }
    }

    private fun urlIsInWhiteNameList(url: String): Boolean {
        return UrlInfoRepository.contains(url)
    }


    /**
     *
     */
    private fun doLimit(url: String) {
        item_limit_tips.visibility = View.VISIBLE
        webView?.goBack()
        site_info.text = url
    }

    private fun closeLimit() {
        runOnUiThread {
            item_limit_tips.visibility = View.GONE
            //如果goback后还是限制页面，说明就要退出webview了
        }

    }

    fun addSiteToWhiteName(url: String) {
        getUrlDomainName(url)
    }

    private fun resetWebBrowser() {
        webView?.clearHistory(); // 清除
        webView?.loadUrl("https://cn.bing.com"); // 你要回到的那个首页的URL
    }


    override fun hindProgressBar() {
        mProgressBar!!.visibility = View.GONE
    }

    override fun showWebView() {
        webView!!.visibility = View.VISIBLE
    }

    override fun hindWebView() {
        webView!!.visibility = View.INVISIBLE
    }

    override fun fullViewAddView(view: View) {
        val decor = window.decorView as FrameLayout
        videoFullView = FullscreenHolder(this@BrowserActivity)
        videoFullView!!.addView(view)
        decor.addView(videoFullView)
    }

    override fun showVideoFullView() {
        videoFullView!!.visibility = View.VISIBLE
    }

    override fun hindVideoFullView() {
        videoFullView!!.visibility = View.GONE
    }

    override fun startProgress(newProgress: Int) {
        mProgressBar!!.visibility = View.VISIBLE
        mProgressBar!!.progress = newProgress
        if (newProgress == 100) {
            mProgressBar!!.visibility = View.GONE
        }
    }

    fun setTitle(mTitle: String?) {
        tvGunTitle!!.text = mTitle
    }

    /**
     * android与js交互：
     * 前端嵌入js代码：不能加重复的节点，不然会覆盖
     */
    override fun addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
        webView!!.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    //  "objs[i].onclick=function(){alert(this.getAttribute(\"has_link\"));}" +
                    "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
                    "}" +
                    "})()"
        )

        // 遍历所有的<li>节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        webView!!.loadUrl(
            "javascript:(function(){" +
                    "var objs =document.getElementsByTagName(\"li\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){" +
                    "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                    "}" +
                    "})()"
        )

        /**传应用内的数据给html，方便html处理 */
        // 无参数调用
        webView!!.loadUrl("javascript:javacalljs()")
        // 传递参数调用
        webView!!.loadUrl("javascript:javacalljswithargs('" + "android传入到网页里的数据，有参" + "')")

    }

    companion object {

        /**
         * 打开网页:
         *
         * @param mContext 上下文
         * @param mUrl     要加载的网页url
         * @param mTitle   标题
         */
        fun loadUrl(mContext: Context, mUrl: String, mTitle: String?) {
            val intent = Intent(mContext, BrowserActivity::class.java)
            intent.putExtra("mUrl", getRealUrl(mUrl))
            intent.putExtra("mTitle", mTitle ?: "加载中...")
            mContext.startActivity(intent)
        }

        fun loadUrl(mContext: Context, mUrl: String) {
            val intent = Intent(mContext, BrowserActivity::class.java)
            intent.putExtra("mUrl", getRealUrl(mUrl))
            intent.putExtra("mTitle", "详情")
            mContext.startActivity(intent)
        }

        /**
         * 对URL进行处理
         */
        private fun getRealUrl(_url: String): String {
            var url = _url
            if (TextUtils.isEmpty(url)) {
                // 空url
                url = "https://github.com/lfork"

            } else if (!url.startsWith("http") && url.contains("http")) {
                // 有http且不在头部
                url = url.substring(url.indexOf("http"), url.length)

            } else if (url.startsWith("www")) {
                // 以"www"开头
                url = "http://$url"

            } else if (!url.startsWith("http") && (url.contains(".me") || url.contains(".com") || url.contains(
                    ".cn"
                ) || url.contains(".top"))
            ) {
                // 不以"http"开头且有后缀
                url = "http://$url"

            } else if (!url.startsWith("http") && !url.contains("www")) {
                // 输入纯文字 或 汉字的情况
                url = "https://cn.bing.com/search?q=$url"
            }
            return url
        }
    }
}
