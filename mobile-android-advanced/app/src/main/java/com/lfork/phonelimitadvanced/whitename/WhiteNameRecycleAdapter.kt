package com.lfork.phonelimitadvanced.whitename

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import com.lfork.phonelimitadvanced.LimitApplication.Companion.App
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.appinfo.AppInfo
import kotlinx.android.synthetic.main.white_name_edit_recycle_item.view.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class WhiteNameRecycleAdapter : RecyclerView.Adapter<WhiteNameRecycleAdapter.NormalHolder>() {

    private val items = ArrayList<AppInfo>(0);


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.white_name_edit_recycle_item, parent, false)
        val holder = NormalHolder(view)
        return holder
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
        holder.textView.text = items[p1].name
        holder.textView.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                App.addWhiteList(items[p1])
            } else {
                App.deleteWhiteList(items[p1])
            }
        }
        holder.imageView.setImageDrawable(items[p1].icon)
    }

    @Synchronized
    private fun deleteItem(position: Int) {
        //防止动画未消失再次点击到View ，此时Index为-1
        //ViewHolder是会复用的
        //减一是剪掉tail item
        if (position !in (0 until items.size)) {
            return
        }
        items.remove(items[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size - position)
    }

    inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView
        val textView: TextView = itemView.text_name
        val imageView: ImageView = itemView.icon
        val checkBox: CheckBox = itemView.checkBox
    }

    fun setItems(itemList: ArrayList<AppInfo>) {
        items.clear()
        items.addAll(itemList)
    }

}