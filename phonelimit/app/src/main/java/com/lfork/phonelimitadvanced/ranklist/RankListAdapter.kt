package com.lfork.phonelimitadvanced.ranklist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.rankinfo.UserRankInfo
import kotlinx.android.synthetic.main.rank_info_recycle_item.view.*
import java.util.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class RankListAdapter : RecyclerView.Adapter<RankListAdapter.NormalHolder>() {

    private val items = ArrayList<UserRankInfo>(0);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rank_info_recycle_item, parent, false)
        return NormalHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
        val item = items[p1]
        holder.itemView.run {
            tv_rank.text = "${(p1 + 1)}"
            tv_username.text = item.username
            tv_moto.text = item.motto
            tv_limittime.text = "专注时长: " +getLimitTimeStr(item.showTime)
        }
    }

    inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setItems(itemList: MutableList<UserRankInfo>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    fun clear(){
        items.clear()
        notifyDataSetChanged()
    }

    fun getLimitTimeStr(limitTimeSeconds: Long) =
        when {
            limitTimeSeconds > 60 * 60 ->
                "${limitTimeSeconds / 3600}小时${(limitTimeSeconds % 3600) / 60}分${limitTimeSeconds % 60}秒"
            limitTimeSeconds > 60 -> {
                val result: String = if (limitTimeSeconds % 60 == 0L) {
                    "${limitTimeSeconds / 60}分"
                } else {
                    "${limitTimeSeconds / 60}分${limitTimeSeconds % 60}秒"
                }
                result
            }
            else -> "${limitTimeSeconds}秒"
        }


    private fun Context.dp2px(dip: Float): Float {
        val r = getResources()
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.getDisplayMetrics()
        )

        return px
    }


}