package com.lfork.phonelimit.ranklist

import com.lfork.phonelimit.data.rankinfo.UserRankInfo

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/15 20:20
 */
interface RankListNavigator {
    fun onItemRefreshed(items:ArrayList<UserRankInfo>)

    fun onError(log:String)
}