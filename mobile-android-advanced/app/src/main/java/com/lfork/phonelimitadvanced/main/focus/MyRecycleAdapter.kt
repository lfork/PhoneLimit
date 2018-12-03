package com.lfork.phonelimitadvanced.main.focus

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import com.lfork.phonelimitadvanced.R
import kotlinx.android.synthetic.main.main_text_recycle_item.view.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class MyRecycleAdapter : RecyclerView.Adapter<MyRecycleAdapter.MyViewHolder>() {
    val items = ArrayList<String>(0);

    init {
        items.add("0001")
        items.add("0002")
        items.add("0003")
        items.add("0004")
        items.add("0005")
        items.add("0006")
        items.add("0007")
        items.add("0008")
        items.add("0009")
        items.add("0010")
        items.add("0011")
        items.add("0012")
        items.add("0013")
        items.add("0014")
        items.add("0015")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_text_recycle_item, parent, false)
        val holder = MyViewHolder(view)

        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, p1: Int) {
        holder.textView.text = items[p1]
        holder.textView.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
    }

    @Synchronized
    private fun deleteItem(position: Int) {
        //防止动画未消失再次点击到View ，此时Index为-1
        //ViewHolder是会复用的
        if(position !in (0 until items.size)){
            return
        }
        items.remove(items[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size - position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.text_recycle
    }

}