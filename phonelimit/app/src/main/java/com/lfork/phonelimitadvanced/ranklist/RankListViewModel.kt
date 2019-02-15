package com.lfork.phonelimitadvanced.ranklist

import android.content.Context
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.rankinfo.UserRankInfo
import com.lfork.phonelimitadvanced.user.UserInfoViewModel
import kotlinx.android.synthetic.main.rank_list_act.*
import java.util.*

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 16:31
 */
class RankListViewModel(_context: Context) : UserInfoViewModel(_context) {
    val items = ArrayList<UserRankInfo>()

    init {
        for (i in 0 until 100) {
            val random = Math.abs(Random().nextInt())
            val item = UserRankInfo()
            item.username = "测试用户 $i"
            item.dailyLimitTime = 60L * 60 * 1000 * (random % 24)
            item.weeklyLimitTime = item.dailyLimitTime + 24 * 60L * 60 * 1000 * (random % 6)
            item.monthlyLimitTime = item.weeklyLimitTime + 30 * 24 * 60L * 60 * 1000 * (random % 21)
            item.totalLimitTime = item.monthlyLimitTime + 30 * 24 * 60L * 60 * 1000 * (random % 100)
            item.motto = "专注、热爱、全心贯注于你所期望的事物上，必有收获。"
            items.add(item)
        }
    }

    var navigator: RankListNavigator? = null

    fun getDailyRankList() {
        items.sortBy {
            it.showTime =it.dailyLimitTime
            it.dailyLimitTime }
        navigator?.onItemRefreshed(items)
    }


    fun getWeekLyRankList() {
        items.sortBy {
            it.showTime =it.weeklyLimitTime
            it.weeklyLimitTime }
        navigator?.onItemRefreshed(items)

    }


    fun getMonthlyRankList() {
        items.sortBy {
            it.showTime =it.monthlyLimitTime
            it.monthlyLimitTime }
        navigator?.onItemRefreshed(items)

    }


    fun getTotalRankList() {
        items.sortBy {
            it.showTime =it.totalLimitTime
            it.totalLimitTime }
        navigator?.onItemRefreshed(items)

    }


    override fun onDestroy() {
        navigator = null
    }

}