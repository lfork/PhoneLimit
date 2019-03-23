package com.lfork.phonelimit.ranklist

import android.content.Context
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.data.DataCallback
import com.lfork.phonelimit.data.rankinfo.UserRankInfo
import com.lfork.phonelimit.data.rankinfo.UserRepository
import com.lfork.phonelimit.user.UserInfoViewModel
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