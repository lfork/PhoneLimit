package com.lfork.phonelimit.view.user

import android.content.Context
import com.lfork.phonelimit.R
import com.lfork.phonelimit.base.widget.BaseDialog
import kotlinx.android.synthetic.main.user_info_avatar_select_dialog.*
import com.lfork.phonelimit.data.saveUserAvatarIndex
import com.lfork.phonelimit.utils.getAvatar


/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/14 15:51
 */
class AvatarSelectDialog(context: Context, var viewModel: UserInfoViewModel?) :
    BaseDialog(context) {
    override fun getContentViewId() = R.layout.user_info_avatar_select_dialog

    override fun setWidthScale(): Float {
        return 0.9f
    }

    override fun setEnterAnim() = null

    override fun setExitAnim() = null

    override fun init() {

        avatar_male_01.setOnClickListener {setAvatar(0)}
        avatar_male_02.setOnClickListener {setAvatar(2)}
        avatar_male_03.setOnClickListener {setAvatar(4)}
        avatar_male_04.setOnClickListener {setAvatar(6)}
        avatar_male_05.setOnClickListener {setAvatar(8)}
        avatar_male_06.setOnClickListener {setAvatar(10)}
        avatar_male_07.setOnClickListener {setAvatar(12)}
        avatar_male_08.setOnClickListener {setAvatar(14)}


        avatar_female_01.setOnClickListener {setAvatar(1)}
        avatar_female_02.setOnClickListener {setAvatar(3)}
        avatar_female_03.setOnClickListener {setAvatar(5)}
        avatar_female_04.setOnClickListener {setAvatar(7)}
        avatar_female_05.setOnClickListener {setAvatar(9)}
        avatar_female_06.setOnClickListener {setAvatar(11)}
        avatar_female_07.setOnClickListener {setAvatar(13)}
        avatar_female_08.setOnClickListener {setAvatar(15)}


    }
    
    private fun setAvatar(index:Int){
        context.saveUserAvatarIndex(index)
        viewModel?.avatar?.set(context.getAvatar(index))
        dismiss()
    }

    override fun dismiss() {
        super.dismiss()
        viewModel = null
    }
}