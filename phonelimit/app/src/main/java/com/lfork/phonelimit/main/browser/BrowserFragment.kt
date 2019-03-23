package com.lfork.phonelimit.main.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lfork.phonelimit.LimitApplication
import com.lfork.phonelimit.R
import com.lfork.phonelimit.browser.BrowserActivity
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.urlinfo.UrlInfo
import com.lfork.phonelimit.data.urlinfo.UrlInfoRepository
import com.lfork.phonelimit.utils.ToastUtil
import com.lfork.phonelimit.utils.isHttpUrl
import com.lfork.phonelimit.utils.runOnUiThread
import kotlinx.android.synthetic.main.browser_frag.view.*
import kotlinx.android.synthetic.main.browser_url_recycle_item.view.*

class BrowserFragment : Fragment() {

    private var root: View? = null

    private lateinit var inputBox: EditText

    private var adapter: WhiteNameAdapter? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.browser_frag, container, false)
            inputBox = root!!.edit_url
            adapter = WhiteNameAdapter()
            root!!.recycle_white_name_urls.adapter = adapter
            root!!.recycle_white_name_urls.layoutManager = LinearLayoutManager(context)
            setupUrlInputListener()
        }
        return root
    }


    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData(){
        UrlInfoRepository.getWhiteNameUrls(object :DataCallback<List<UrlInfo>>{
            override fun succeed(data: List<UrlInfo>) {
                runOnUiThread {
                    adapter?.setItems(data.toMutableList())
                }

            }

            override fun failed(code: Int, log: String) {
                runOnUiThread {
                    ToastUtil.showShort(context, "未知异常,白名单获取失败")
                }
            }
        })

    }


    private fun setupUrlInputListener() {
        inputBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                openUrl(inputBox.text.toString())
            }
            false;
        }
    }

    private fun openUrl(urlOrigin: String) {
        var url = urlOrigin
        if (!TextUtils.isEmpty(url)) {
            //UrlInfoRepository
            if (isHttpUrl(url) && LimitApplication.isOnLimitation) {
                if (url.contains("http://") || url.contains("https://")) {
                    url = url.substring(url.indexOf("://") + +3)
                }

                if (UrlInfoRepository.contains(url)) {
                    BrowserActivity.loadUrl(context!!, url, "WebBrowser")
                } else {
                    ToastUtil.showShort(context, "当前网址不在白名单当中，限制模式下无法访问")
                }
            } else {
                BrowserActivity.loadUrl(context!!, url, "Phone Limit WebBrowser")
            }

        }
    }

    inner class WhiteNameAdapter : RecyclerView.Adapter<WhiteNameAdapter.NormalHolder>() {

        private val items = ArrayList<UrlInfo>(0);

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.browser_url_recycle_item, parent, false)
            return NormalHolder(view)
        }


        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
            holder.textView.text = items[p1].url
            holder.item.setOnClickListener {
                openUrl(items[p1].url)
            }

            val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(android.R.drawable.ic_menu_report_image)
            Glide.with(context!!).load("https://"+items[p1].url+"/favicon.ico").apply(options).into(holder.imageView)
        }

//    @Synchronized
//    private fun deleteItem(position: Int) {
//        //防止动画未消失再次点击到View ，此时Index为-1
//        //ViewHolder是会复用的
//        //减一是剪掉tail item
//        if (position !in (0 until dailyItemsCache.size - 1)) {
//            return
//        }
//        dailyItemsCache.remove(dailyItemsCache[position])
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, dailyItemsCache.size - position)
//    }

        inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val item = itemView
            val textView: TextView = itemView.domain
            val imageView: ImageView = itemView.favicon
        }

        fun setItems(itemList: MutableList<UrlInfo>) {
            items.clear()
            itemList.sort()
            items.addAll(itemList)
            notifyDataSetChanged()
        }


    }
}


