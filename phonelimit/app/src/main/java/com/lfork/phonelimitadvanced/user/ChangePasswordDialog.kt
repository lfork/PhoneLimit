package com.lfork.phonelimitadvanced.user

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.hjq.toast.ToastUtils
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.base.widget.BaseDialog
import com.lfork.phonelimitadvanced.data.*
import com.lfork.phonelimitadvanced.data.rankinfo.User
import kotlinx.android.synthetic.main.dialog_change_password.*
import java.util.regex.Pattern

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 15:51
 */
class ChangePasswordDialog(context: Context, var viewModel: UserInfoViewModel?) :
    BaseDialog(context) {
    override fun setWidthScale(): Float {
        return 0.9f
    }

    override fun setEnterAnim() = null

    override fun setExitAnim() = null

    override fun getContentViewId() = R.layout.dialog_change_password

    override fun init() {
        tv_email.setText(context.getUserEmail())
        registerListener()
    }

    private var email: String? = null
        get() =
            tv_email?.text.toString()

    private var password: String? = null
        get() =
            tv_password?.text.toString()

    private var newPassword: String? = null
        get() =
            tv_new_password?.text.toString()

    private var newPasswordRepeat: String? = null
        get() =
            tv_new_password_repeat?.text.toString()

    private fun registerListener() {
        btn_ok.setOnClickListener {
            if (appIsFree()) {
                setProgressVisibility()
                if (!userInfoCheck()) {
                    return@setOnClickListener
                }
                viewModel?.changePassword(email!!, password!!, newPassword!!,object : DataCallback<String> {
                    override fun succeed(data: String) {
                        context.saveUserPassword(data)
                        setProgressVisibility(false, true)
                    }

                    override fun failed(code: Int, log: String) {
                        setProgressVisibility(false)
                    }
                })
            }


        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setProgressVisibility(isVisible: Boolean = true, needCloseDialog: Boolean = false) {
        if (isVisible) {
            task_progress.visibility = View.VISIBLE
        } else {
            task_progress.visibility = View.GONE

        }
        if (needCloseDialog) {
            dismiss()
        }
    }

    /**
     * 是否空闲：没有处理网络任务
     */
    private fun appIsFree() = (task_progress.visibility == View.GONE)


    private fun userInfoCheck(): Boolean {

        if (!emailCheck(email)) {
            return false
        }


        if (TextUtils.isEmpty(password)) {
            ToastUtils.show("旧密码不能为空")
            return false
        }

        if (password!!.length < 8) {
            ToastUtils.show("旧密码长度必须在8位及以上")
            return false
        }

        if (TextUtils.isEmpty(newPassword)) {
            ToastUtils.show("新密码不能为空")
            return false
        }

        if (newPassword!!.length < 8) {
            ToastUtils.show("新密码长度必须在8位及以上")
            return false
        }

        if (!newPassword.equals(newPasswordRepeat)){
            ToastUtils.show("两次输入的密码不一致")
            return false
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