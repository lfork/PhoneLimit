package com.lfork.phonelimitadvanced.user

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.EditText
import com.hjq.toast.ToastUtils
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.getUserLoginStatus
import com.lfork.phonelimitadvanced.databinding.UserInfoActBinding
import com.lfork.phonelimitadvanced.ranklist.LoginRegisterDialog
import com.lfork.phonelimitadvanced.ranklist.RankListViewModel
import kotlinx.android.synthetic.main.user_info_act.*

class UserInfoActivity : AppCompatActivity() {


    private var viewModel: UserInfoViewModel? = null
    private var binding: UserInfoActBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.user_info_act);
        viewModel = RankListViewModel(this)
        binding?.viewmodel = viewModel
//        showNotLogInUI()
        registerListener()

    }

    override fun onStart() {
        super.onStart()
        viewModel?.initUserInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.onDestroy()
        usernameEditDialog?.cancel()
    }


    private fun registerListener() {
        tv_login.setOnClickListener {
            val loginDialog = LoginRegisterDialog(this@UserInfoActivity, viewModel)
            loginDialog.show()
        }

        iv_avatar.setOnClickListener {
            if (getUserLoginStatus()) {
                val avatarSelectDialog = AvatarSelectDialog(
                    this@UserInfoActivity,
                    viewModel
                )
                avatarSelectDialog.show()
            } else {
                val loginDialog = LoginRegisterDialog(this@UserInfoActivity, viewModel)
                loginDialog.show()
            }

        }

        btn_edit_username.setOnClickListener {
            editUsername()
        }

        btn_edit_tips.setOnClickListener {
            editMotto()
        }


        btn_forget_password.setOnClickListener {
            ToastUtils.show("处理中...")
            viewModel?.forgetPassword(null)
        }

        btn_logout.setOnClickListener {
            viewModel?.logout()
            ToastUtils.show("退出成功")
            finish()
        }
    }


    var usernameEditDialog: AlertDialog? = null

    private fun editUsername() {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.setText(viewModel?.username?.get())
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(editText).setTitle("修改昵称")
            // Add action buttons
            .setPositiveButton(
                "确定"
            ) { _, id ->
                val username = editText.text.toString()
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.show("昵称不能为空")
                    return@setPositiveButton
                }

                viewModel?.updateUsername(username)


            }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, id ->
                dialog.cancel()
            }
        usernameEditDialog = builder.create()

        usernameEditDialog?.show()
    }

    private fun editMotto() {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.setText(viewModel?.motto?.get())
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(editText).setTitle("修改格言")
            // Add action buttons
            .setPositiveButton(
                "确定"
            ) { _, id ->
                val motto = editText.text.toString()
                if (TextUtils.isEmpty(motto)) {
                    ToastUtils.show("提示文字不能为空")
                    return@setPositiveButton
                }

                viewModel?.updateMotto(motto)
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, id ->
                dialog.cancel()
            }
        usernameEditDialog = builder.create()

        usernameEditDialog?.show()
    }
}
