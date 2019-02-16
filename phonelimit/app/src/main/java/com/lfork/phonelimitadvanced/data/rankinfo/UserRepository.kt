package com.lfork.phonelimitadvanced.data.rankinfo

import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.MainHandler
import com.lfork.phonelimitadvanced.data.DataCallback
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 18:13
 */
object UserRepository {

    fun register(email: String, password: String, dataCallback: DataCallback<User>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0

                if (succeed) {
                    val user = User()
                    user.email = email
                    user.password = password
                    dataCallback.succeed(user)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }

            }

        }

    }

    fun signin(email: String, password: String, dataCallback: DataCallback<User>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    val user = User()
                    user.email = email
                    user.password = password
                    dataCallback.succeed(user)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }

            }

        }
    }

    fun forgetPassword(email: String, dataCallback: DataCallback<String>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dataCallback.succeed("处理成功")
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }


    fun changePassword(
        email: String,
        password: String,
        newPassword: String,
        dataCallback: DataCallback<String>
    ) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dataCallback.succeed(newPassword)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }


    private val dailyItemsCache = ArrayList<UserRankInfo>()
    private val weeklyItemsCache = ArrayList<UserRankInfo>()
    private val monthlyItemsCache = ArrayList<UserRankInfo>()
    private val totallyItemsCache = ArrayList<UserRankInfo>()

    init {
        for (i in 0 until 100) {
            val random = Math.abs(Random().nextInt())
            val item = UserRankInfo()
            item.username = "测试用户 $i"
            item.dailyLimitTime = 60L * 60  * (random % 24)
            item.weeklyLimitTime = item.dailyLimitTime + 24 * 60L * 60  * (random % 6)
            item.monthlyLimitTime = item.weeklyLimitTime + 30 * 24 * 60L * 60  * (random % 21)
            item.totalLimitTime = item.monthlyLimitTime + 30 * 24 * 60L * 60  * (random % 100)
            item.motto = "专注、热爱、全心贯注于你所期望的事物上，必有收获。"
            dailyItemsCache.add(item)
        }
    }


    fun getDailyRankList(dataCallback: DataCallback<ArrayList<UserRankInfo>>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dailyItemsCache.sortByDescending {
                        it.showTime = it.dailyLimitTime
                        it.dailyLimitTime
                    }

                    dataCallback.succeed(dailyItemsCache)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }


    }

    fun getWeeklyRankList(dataCallback: DataCallback<ArrayList<UserRankInfo>>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dailyItemsCache.sortByDescending {
                        it.showTime = it.weeklyLimitTime
                        it.weeklyLimitTime
                    }

                    dataCallback.succeed(dailyItemsCache)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }

    fun getMonthlyRankList(dataCallback: DataCallback<ArrayList<UserRankInfo>>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dailyItemsCache.sortByDescending {
                        it.showTime = it.monthlyLimitTime
                        it.monthlyLimitTime
                    }

                    dataCallback.succeed(dailyItemsCache)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }

    fun getTotallyRankList(dataCallback: DataCallback<ArrayList<UserRankInfo>>) {
        LimitApplication.executeAsyncDataTask {
            Thread.sleep(2000)
            MainHandler.getInstance().post {
                val succeed = Random().nextInt() % 2 == 0
                if (succeed) {
                    dailyItemsCache.sortByDescending {
                        it.showTime = it.totalLimitTime
                        it.totalLimitTime
                    }

                    dataCallback.succeed(dailyItemsCache)
                } else {
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }

    fun uploadRankInfo(){

    }
}