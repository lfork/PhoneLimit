package com.lfork.phonelimitadvanced.user

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import com.hjq.toast.ToastUtils
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.*
import com.lfork.phonelimitadvanced.data.rankinfo.User
import com.lfork.phonelimitadvanced.data.rankinfo.UserRepository
import com.lfork.phonelimitadvanced.utils.getAvatar

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 16:31
 */
open class UserInfoViewModel(_context: Context) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    var context: Context? = _context


    val username = ObservableField<String>("")

    val email = ObservableField<String>("")


    val isLoggedIn = ObservableBoolean(false)

    val avatar = ObservableField<Drawable>()

    val motto = ObservableField<String>(_context.getString(R.string.default_motto))


    fun initUserInfo() {
        context?.run {
            if (getUserLoginStatus()) {
                username.set(getUsername())
                email.set(getUserEmail())
                isLoggedIn.set(true)
                avatar.set(getAvatar(getUserAvatarIndex()))
                motto.set(getBio())
            } else {
                username.set("")
                email.set("")
                isLoggedIn.set(false)
                avatar.set(getAvatar(-1))
            }
        }
    }

    val isDoingTask = ObservableBoolean(false)

    @Volatile
    var isDoingTimeCostTask = false

    @Synchronized
    private fun setWorkStatus(isDoing: Boolean) {
        isDoingTimeCostTask = isDoing
        isDoingTask.set(isDoing)
    }


    fun register(email: String, password: String, dataCallback: DataCallback<User>) {
        if (isDoingTimeCostTask) {
            return
        }
        setWorkStatus(true)
        val callback = object : DataCallback<User> {
            override fun succeed(data: User) {
                dataCallback.succeed(data)
                setWorkStatus(false)
                updateUserInfo(data)
            }

            override fun failed(code: Int, log: String) {
                dataCallback.failed(code, log)
                setWorkStatus(false)
            }
        }
        UserRepository.register(email, password, callback)
    }

    fun signin(email: String, password: String, dataCallback: DataCallback<User>) {
        if (isDoingTimeCostTask) {
            return
        }
        setWorkStatus(true)
        val callback = object : DataCallback<User> {
            override fun succeed(data: User) {
                dataCallback.succeed(data)
                setWorkStatus(false)
                updateUserInfo(data)
            }

            override fun failed(code: Int, log: String) {
                dataCallback.failed(code, log)
                setWorkStatus(false)
            }
        }
        UserRepository.signin(email, password, callback)
    }

    fun forgetPassword(dataCallback: DataCallback<String>?) {
        if (isDoingTimeCostTask) {
            return
        }
        setWorkStatus(true)
        val callback = object : DataCallback<String> {
            override fun succeed(data: String) {
                dataCallback?.succeed(data)
                setWorkStatus(false)
                ToastUtils.show("修改密码的方式已经发送到您的邮箱，请及时查看")
            }

            override fun failed(code: Int, log: String) {
                ToastUtils.show(log)
                dataCallback?.failed(code, log)
                setWorkStatus(false)
            }
        }
        UserRepository.forgetPassword(email.get()!!, callback)
    }

    open fun onDestroy() {
        context = null
    }

    fun logout(){
        isLoggedIn.set(false)
        context?.saveUserAvatarIndex(-1)
        context?.saveUserLoginStatus(false)
    }

    private fun updateUserInfo(user: User) {
        this@UserInfoViewModel.email.set(user.email)
        this@UserInfoViewModel.username.set(user.email)
        context?.saveUsername(user.username?:user.email!!)
        context?.saveUserLoginStatus(true)
        isLoggedIn.set(true)
    }

    fun updateUsername(_username: String) {
        username.set(_username)
    }

    fun updateMotto(_motto:String){
        context?.saveBio(_motto)
        motto.set(_motto)
    }
}