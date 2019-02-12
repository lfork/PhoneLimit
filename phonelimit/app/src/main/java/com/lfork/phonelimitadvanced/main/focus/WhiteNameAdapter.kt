package com.lfork.phonelimitadvanced.main.focus

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isOnLimitation
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.utils.ToastUtil
import com.lfork.phonelimitadvanced.utils.startActivity
import com.lfork.phonelimitadvanced.utils.startOtherApp
import com.lfork.phonelimitadvanced.whitename.WhiteNameEditActivity
import kotlinx.android.synthetic.main.item_recycle_app.view.*
import java.util.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class WhiteNameAdapter : RecyclerView.Adapter<WhiteNameAdapter.NormalHolder>() {

    var customIconOnClickListener: CustomIconOnClickListener?=null

    private val items = ArrayList<AppInfo>(0);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycle_app, parent, false)
        return NormalHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size + 2
    }

    override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
        val context = holder.imageView.context
        when (p1) {
            0 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_search_black_24dp))
                holder.textView.text = "学习浏览器"
                holder.item.setOnClickListener {
                    customIconOnClickListener?.onBrowserClick()
                }
            }
            1 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_edit_black_24dp))
                holder.textView.text = "编辑白名单"
                holder.item.setOnClickListener {
                    if (!isOnLimitation) {
                        //跳转到编辑界面
                        it.context.startActivity<WhiteNameEditActivity>()
                    } else {
                        ToastUtil.showShort(it.context, "暂时不能编辑噢")
                    }
                }
            }
            else -> {
                holder.textView.text = items[p1-2].appName
                val icon = items[p1-2].icon
                holder.imageView.setImageDrawable(icon)
                holder.item.setOnClickListener {
                    it.context.startOtherApp(items[p1-2].packageName)
                }
            }
        }
    }

    inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView
        val textView: TextView = itemView.text_recycle
        val imageView: ImageView = itemView.icon
    }

    fun setItems(itemList: MutableList<AppInfo>) {
        items.clear()
        itemList.sort()
        items.addAll(itemList)
    }

    fun onDestroy(){
        customIconOnClickListener = null
    }

}