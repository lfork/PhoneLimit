package com.lfork.phonelimit.user

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.*
import com.lfork.phonelimit.data.rankinfo.User
import com.lfork.phonelimit.data.rankinfo.UserRepository
import com.lfork.phonelimit.utils.getAvatar

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
                ToastUtils.show("注册成功")
            }

            override fun failed(code: Int, log: String) {
                dataCallback.failed(code, log)
                setWorkStatus(false)
                ToastUtils.show(log)
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
                ToastUtils.show("登录成功")
            }

            override fun failed(code: Int, log: String) {
                dataCallback.failed(code, log)
                setWorkStatus(false)
                ToastUtils.show(log)
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
                ToastUtils.show("新密码已经发送到您的注册邮箱，请及时查看")
            }

            override fun failed(code: Int, log: String) {
                ToastUtils.show(log)
                dataCallback?.failed(code, log)
                setWorkStatus(false)
                ToastUtils.show(log)
            }
        }
        UserRepository.forgetPassword(email.get()!!, callback)
    }

    fun changePassword(
        email: String,
        password: String,
        newPassword: String,
        dataCallback: DataCallback<String>?
    ) {
        if (isDoingTimeCostTask) {
            return
        }
        setWorkStatus(true)
        val callback = object : DataCallback<String> {
            override fun succeed(data: String) {
                dataCallback?.succeed(data)
                setWorkStatus(false)
                ToastUtils.show("修改成功")
            }

            override fun failed(code: Int, log: String) {
                ToastUtils.show(log)
                dataCallback?.failed(code, log)
                setWorkStatus(false)
                ToastUtils.show(log)
            }
        }
        UserRepository.changePassword(email, password, newPassword, callback)
    }

    open fun onDestroy() {
        context = null
    }

    fun logout() {
        isLoggedIn.set(false)
        context?.saveUserAvatarIndex(-1)
        context?.saveUserLoginStatus(false)
    }

    private fun updateUserInfo(user: User) {
        this@UserInfoViewModel.email.set(user.email)
        this@UserInfoViewModel.username.set(user.email)
        context?.saveUsername(user.username ?: user.email!!)
        context?.saveUserLoginStatus(true)
        isLoggedIn.set(true)
    }

    fun updateUsername(_username: String) {
        username.set(_username)
    }

    fun updateMotto(_motto: String) {
        context?.saveBio(_motto)
        motto.set(_motto)
    }
}