package com.lfork.phonelimit.whitename

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.appinfo.AppInfo
import com.lfork.phonelimit.data.appinfo.AppInfoRepository
import com.lfork.phonelimit.utils.ToastUtil
import com.lfork.phonelimit.utils.getAppIcon
import com.lfork.phonelimit.utils.setupToolBar
import kotlinx.android.synthetic.main.white_name_edit_act.*
import kotlinx.android.synthetic.main.white_name_edit_recycle_item.view.*
import java.util.*

class WhiteNameEditActivity : AppCompatActivity() {

    lateinit var adapter: WhiteNameRecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.white_name_edit_act)
        recycle_white_list.layoutManager = LinearLayoutManager(this)
        adapter = WhiteNameRecycleAdapter()

        recycle_white_list.adapter = adapter
        setupToolBar(toolbar, "请选择白名单应用")
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        AppInfoRepository.getAllAppInfo(object : DataCallback<List<AppInfo>> {
            override fun succeed(data: List<AppInfo>) {

                val tempData = ArrayList<AppInfo>();
                data.forEach {
                    val icon = getAppIcon(this@WhiteNameEditActivity, it.packageName)
                    it.icon = icon
                    if(icon != null){
                        tempData.add(it)
                    }

                }

                runOnUiThread {
                    adapter.setItems(tempData)
                    adapter.notifyDataSetChanged()
                }

            }

            override fun failed(code: Int, log: String) {
                runOnUiThread {
                    ToastUtil.showShort(this@WhiteNameEditActivity, "Error 应用程序加载失败")
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class WhiteNameRecycleAdapter :
        RecyclerView.Adapter<WhiteNameRecycleAdapter.NormalHolder>() {

        private val items = ArrayList<AppInfo>(0);


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.white_name_edit_recycle_item, parent, false)
            return NormalHolder(view)
        }


        override fun getItemCount(): Int {
            return items.size
        }

        private val simpleTips = object : DataCallback<String> {
            override fun succeed(data: String) {
                ToastUtil.showLong(this@WhiteNameEditActivity, data)
            }

            override fun failed(code: Int, log: String) {
                ToastUtil.showLong(this@WhiteNameEditActivity, log)
            }
        }

        override fun onBindViewHolder(holder: NormalHolder, p1: Int) {

            val item = items[p1]

//            Log.d("奇怪的Item", item.toString())
            holder.textView.text = item.appName
            holder.textView.setOnClickListener {
                deleteItem(holder.adapterPosition)
            }
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                item.isInWhiteNameList = isChecked
                AppInfoRepository.update(item, simpleTips)
            }
            holder.imageView.setImageDrawable(item.icon)
            holder.checkBox.isChecked = item.isInWhiteNameList
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

        fun setItems(itemList: MutableList<AppInfo>) {
            items.clear()
            itemList.sort()
            items.addAll(itemList)
        }

    }

}
