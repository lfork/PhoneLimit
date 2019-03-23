package com.lfork.phonelimit.main.focus

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.LimitApplication.Companion.isOnLimitation
import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.appinfo.AppInfo
import com.lfork.phonelimit.help.HelpActivity
import com.lfork.phonelimit.ranklist.RankListActivity
import com.lfork.phonelimit.statistics.StatisticActivity
import com.lfork.phonelimit.timedtask.TimedTaskActivity
import com.lfork.phonelimit.utils.startActivity
import com.lfork.phonelimit.utils.startOtherApp
import com.lfork.phonelimit.whitename.WhiteNameEditActivity
import kotlinx.android.synthetic.main.focus_recycle_app.view.*


/**
 *
 * Created by 98620 on 2018/11/8.
 */
class WhiteNameAdapter : RecyclerView.Adapter<WhiteNameAdapter.NormalHolder>() {

    var customIconOnClickListener: CustomIconOnClickListener? = null

    private var items = ArrayList<AppInfo>(0);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.focus_recycle_app, parent, false)
        return NormalHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size + customHeaderCount + customTailCount
    }

    private val customHeaderCount = 4

    private val customTailCount = 3

    override fun onBindViewHolder(holder: NormalHolder, p1: Int) {
        val context = holder.imageView.context
        when (p1) {
            0 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_timed_task_24dp))
                holder.textView.text = "定时任务"
                holder.item.setOnClickListener {
                    it.context.startActivity<TimedTaskActivity>()
                }
            }

            1 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_browser_24dp))
                holder.textView.text = "浏览器"
                holder.item.setOnClickListener {
                    customIconOnClickListener?.onBrowserClick()
                }
            }

            2 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_chart_24dp))
                holder.textView.text = "使用统计"
                holder.item.setOnClickListener {
                    it.context.startActivity<StatisticActivity>()
                }
            }

            3 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_rank_24dp))
                holder.textView.text = "排行榜"
                holder.item.setOnClickListener {
                    //跳转到编辑界面
                    it.context.startActivity<RankListActivity>()
                }
            }

            //最后一个按钮
            itemCount - 1 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_help_24dp))
                holder.textView.text = "帮助"
                holder.item.setOnClickListener {
                    it.context.startActivity<HelpActivity>()
                }
            }

            itemCount - 2 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_white_name_24dp))
                holder.textView.text = "白名单"
                holder.item.setOnClickListener {
                    if (!isOnLimitation) {
                        //跳转到编辑界面
                        it.context.startActivity<WhiteNameEditActivity>()
                    } else {
                        ToastUtils.show("暂时不能编辑噢")
                    }
                }
            }

            itemCount - 3 -> {
                holder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_settings_black_24dp))
                holder.textView.text = "设置"
                holder.item.setOnClickListener {
                    customIconOnClickListener?.onSettingsClick()
                }
            }
            else -> {
                holder.textView.text = items[p1 - customHeaderCount].appName
                val icon = items[p1 - customHeaderCount].icon
                holder.imageView.setImageDrawable(icon)
                holder.item.setOnClickListener {
                    it.context.startOtherApp(items[p1 - customHeaderCount].packageName)
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
        items = ArrayList(itemList)
    }

    fun onDestroy() {
        customIconOnClickListener = null
    }

}