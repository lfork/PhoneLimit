package com.lfork.phonelimit.ranklist

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.getUserLoginStatus
import com.lfork.phonelimit.data.rankinfo.UserRankInfo
import com.lfork.phonelimit.databinding.RankListActBinding
import com.lfork.phonelimit.user.UserInfoActivity
import com.lfork.phonelimit.utils.startActivity
import kotlinx.android.synthetic.main.rank_list_act.*

class RankListActivity : AppCompatActivity(),RankListNavigator {
    override fun onError(log: String) {
        task_progress.visibility = View.GONE
        (rv_rank_list.adapter as RankListAdapter).clear()
    }

    override fun onItemRefreshed(items: ArrayList<UserRankInfo>) {
        task_progress.visibility = View.GONE
        (rv_rank_list.adapter as RankListAdapter).setItems(items)
    }

    private var viewModel: RankListViewModel? = null
    private var binding: RankListActBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.rank_list_act);
        viewModel = RankListViewModel(this)
        viewModel?.navigator = this
        binding?.viewmodel = viewModel
        registerListener()
        setupRankListRecyclerView()
        setupRankTypeSpinner()
    }

    private fun setupRankTypeSpinner() {
        sp_rank_type.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                task_progress.visibility = View.VISIBLE
               when(position){
                   0->viewModel?.getDailyRankList()
                   1->viewModel?.getWeekLyRankList()
                   2->viewModel?.getMonthlyRankList()
                   3->viewModel?.getTotalRankList()
               }
            }
        }
    }

    private fun setupRankListRecyclerView() {
        rv_rank_list.adapter = RankListAdapter()
        rv_rank_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel?.initUserInfo()
    }



    private fun registerListener() {
        tv_login.setOnClickListener {
            val loginDialog = LoginRegisterDialog(this@RankListActivity, viewModel)
            loginDialog.show()
        }

        iv_avatar.setOnClickListener {
            if (getUserLoginStatus()) {
                startActivity<UserInfoActivity>()
            } else {
                val loginDialog = LoginRegisterDialog(this@RankListActivity, viewModel)
                loginDialog.show()
            }

        }

        layout_user_info.setOnClickListener {
            if (getUserLoginStatus()) {
                startActivity<UserInfoActivity>()
            }
        }
    }


}
