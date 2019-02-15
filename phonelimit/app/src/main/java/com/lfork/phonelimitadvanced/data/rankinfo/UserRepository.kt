package com.lfork.phonelimitadvanced.data.rankinfo

import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.MainHandler
import com.lfork.phonelimitadvanced.data.DataCallback
import java.util.*

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

                if (succeed){
                    val user =  User()
                    user.email = email
                    user.password = password
                    dataCallback.succeed(user)
                } else{
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
                if (succeed){
                    val user =  User()
                    user.email = email
                    user.password = password
                    dataCallback.succeed(user)
                } else{
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
                if (succeed){
                    dataCallback.succeed("处理成功")
                } else{
                    dataCallback.failed(-1, "网络异常(随机测试模式)")
                }
            }
        }
    }
}