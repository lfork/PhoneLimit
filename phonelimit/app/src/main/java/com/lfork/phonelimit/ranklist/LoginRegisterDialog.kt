package com.lfork.phonelimit.ranklist

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.hjq.toast.ToastUtils
import com.lfork.phonelimit.R
import com.lfork.phonelimit.base.widget.BaseDialog
import com.lfork.phonelimit.data.*
import com.lfork.phonelimit.data.rankinfo.User
import com.lfork.phonelimit.view.user.UserInfoViewModel
import kotlinx.android.synthetic.main.user_info_login_dialog.*
import java.util.regex.Pattern

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 15:51
 */
class LoginRegisterDialog(context: Context, var viewModel: UserInfoViewModel?) :
    BaseDialog(context) {
    override fun setWidthScale(): Float {
        return 0.9f
    }

    override fun setEnterAnim() = null

    override fun setExitAnim() = null

    override fun init() {
        tv_email.setText(context.getUserEmail())
        tv_password.setText(context.getUserPassword())
        registerListener()
    }

    private var email: String? = null
        get() =
            tv_email?.text.toString()

    private var password: String? = null
        get() =
            tv_password?.text.toString()

    private fun registerListener() {
        btn_register.setOnClickListener {
            if (appIsFree()) {
                setProgressVisibility()
                if (!userInfoCheck()) {
                    return@setOnClickListener
                }
                viewModel?.register(email!!, password!!, object :DataCallback<User>{
                    override fun succeed(data: User) {
                        setProgressVisibility(false, true)
                    }

                    override fun failed(code: Int, log: String) {
                        setProgressVisibility(false)
                    }
                })
            }


        }
        btn_sign_in.setOnClickListener {
            if (appIsFree()) {
                if (!userInfoCheck()) {
                    return@setOnClickListener
                }
                setProgressVisibility()
                viewModel?.signin(email!!, password!!, object :DataCallback<User>{
                    override fun succeed(data: User) {
                        setProgressVisibility(false, true)
                    }

                    override fun failed(code: Int, log: String) {
                        setProgressVisibility(false)
                    }
                })
            }
        }
        tv_forget_password.setOnClickListener {
            if (appIsFree()) {
                if (!userInfoCheck(false)) {
                    return@setOnClickListener
                }
                setProgressVisibility()
                viewModel?.forgetPassword(object :DataCallback<String>{
                    override fun succeed(data: String) {
                        setProgressVisibility(false, true)
                    }

                    override fun failed(code: Int, log: String) {
                        setProgressVisibility(false)
                    }
                })
            }
        }
    }

    private fun setProgressVisibility(isVisible: Boolean = true, needCloseDialog:Boolean = false) {
        if (isVisible) {
            task_progress.visibility = View.VISIBLE
        } else {
            task_progress.visibility = View.GONE

        }
        if (needCloseDialog){
            dismiss()
        }
    }

    /**
     * 是否空闲：没有处理网络任务
     */
    private fun appIsFree() = (task_progress.visibility == View.GONE)

    override fun getContentViewId() = R.layout.user_info_login_dialog

    private fun userInfoCheck(checkPassword: Boolean = true): Boolean {

        if (!emailCheck(email)) {
            return false
        }
        if (checkPassword) {
            if (!passwordCheck(password)) {
                return false
            }
        }

        return true
    }

    private fun emailCheck(email: String?): Boolean {

        if (TextUtils.isEmpty(email)) {
            ToastUtils.show("邮箱不能为空")
            return false
        }
        //电子邮件
        val check =
            "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        val regex = Pattern.compile(check)
        val matcher = regex.matcher(email)
        val result = matcher.matches()
        if (!result) {
            ToastUtils.show("邮箱格式有误")
        }

        context.saveUserEmail(email!!)

        return result
    }

    private fun passwordCheck(password: String?): Boolean {
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show("密码不能为空")
            return false
        }

        if (password!!.length < 8) {
            ToastUtils.show("密码长度必须在8位及以上")
            return false
        }
        context.saveUserPassword(password)
        return true
    }

    var userInfoGetListener: UserInfoGetListener? = null

    interface UserInfoGetListener {
        fun onNewUserInfo(user: User)
    }

    override fun dismiss() {
        super.dismiss()
        viewModel = null
        userInfoGetListener = null
    }

}