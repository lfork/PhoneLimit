package com.lfork.phonelimitadvanced.ranklist

import android.content.Context
import com.hjq.toast.ToastUtils
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.DataCallback
import com.lfork.phonelimitadvanced.data.rankinfo.UserRankInfo
import com.lfork.phonelimitadvanced.data.rankinfo.UserRepository
import com.lfork.phonelimitadvanced.user.UserInfoViewModel
import kotlinx.android.synthetic.main.rank_list_act.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 16:31
 */
class RankListViewModel(_context: Context) : UserInfoViewModel(_context) {

    var navigator: RankListNavigator? = null

    val callback = object : DataCallback<ArrayList<UserRankInfo>> {
        override fun succeed(data: ArrayList<UserRankInfo>) {
            navigator?.onItemRefreshed(data)
        }

        override fun failed(code: Int, log: String) {
            navigator?.onError(log)
            ToastUtils.show(log)
        }
    }

    fun getDailyRankList() {
        UserRepository.getDailyRankList(callback)
    }


    fun getWeekLyRankList() {
        UserRepository.getWeeklyRankList(callback)
    }


    fun getMonthlyRankList() {
        UserRepository.getMonthlyRankList(callback)
    }


    fun getTotalRankList() {
        UserRepository.getTotallyRankList(callback)
    }


    override fun onDestroy() {
        navigator = null
    }

}