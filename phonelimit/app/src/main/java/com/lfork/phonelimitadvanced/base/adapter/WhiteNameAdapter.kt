package com.lfork.phonelimitadvanced.base.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isOnLimitation
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import com.lfork.phonelimitadvanced.main.focus.CustomIconOnClickListener
import com.lfork.phonelimitadvanced.utils.ToastUtil
import com.lfork.phonelimitadvanced.utils.startActivity
import com.lfork.phonelimitadvanced.utils.startOtherApp
import com.lfork.phonelimitadvanced.whitename.WhiteNameEditActivity
import kotlinx.android.synthetic.main.main_focus_recycle_item.view.*
import java.util.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class WhiteNameAdapter : RecyclerView.Adapter<WhiteNameAdapter.NormalHolder>() {

    var customIconOnClickListener: CustomIconOnClickListener?=null

    private val items = ArrayList<AppInfo>(0);

    private val TYPE_NORMAL = 0

    private val TYPE_TAIL = 1

    private val TYPE_HEAD_GENERAL = 2

    private val TYPE_HEAD_BROWSER = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
//        = when (viewType)
//        TYPE_HEAD_GENERAL -> {
//
//        }
//
//        TYPE_HEAD_BROWSER -> {
//            val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.main_focus_recycle_item, parent, false)
//            NormalHolder(view)
//        }
//
//
//        TYPE_TAIL -> {
//            val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.main_focus_edit_recycle_item, parent, false)
//            NormalHolder(view)
//
//        }
//
//        else -> {
//            val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.main_focus_recycle_item, parent, false)
//            NormalHolder(view)
//
//        }
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.main_focus_recycle_item, parent, false)
        return NormalHolder(view)
    }


    override fun getItemViewType(position: Int) = when (position) {
        0 -> TYPE_HEAD_GENERAL
        1 -> TYPE_HEAD_BROWSER
        itemCount - 1 -> TYPE_TAIL
        else -> TYPE_NORMAL
    }


    override fun getItemCount(): Int {
        return items.size + 3
    }

    override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
        val context = holder.imageView.context
        when (p1) {
            0 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_search_black_24dp))
                holder.textView.text = "查资料"
                holder.item.setOnClickListener {
                    customIconOnClickListener?.onBrowserClick()
                }
            }
            1 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_settings_black_24dp))
                holder.textView.text = "设置"
                holder.item.setOnClickListener {
                    customIconOnClickListener?.onSettingsClick()
                }
            }
            itemCount - 1 -> {
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

//    @Synchronized
//    private fun deleteItem(position: Int) {
//        //防止动画未消失再次点击到View ，此时Index为-1
//        //ViewHolder是会复用的
//        //减一是剪掉tail item
//        if (position !in (0 until items.size - 1)) {
//            return
//        }
//        items.remove(items[position])
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, items.size - position)
//    }

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