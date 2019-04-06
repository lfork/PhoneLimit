package com.lfork.phonelimit.data.rankinfo

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/13 19:43
 */
class UserRankInfo {
//    override fun compareTo(other: UserRankInfo) = dailyLimitTime.compareTo(other.dailyLimitTime)

    var id: String? = null

    var totalLimitTime: Long = 0

    var dailyLimitTime: Long = 0

    var weeklyLimitTime: Long = 0

    var monthlyLimitTime: Long = 0

    var yearlyLimitTime: Long = 0

    var showTime:Long = 0

    var username: String? = null

    var motto: String? = null
}
