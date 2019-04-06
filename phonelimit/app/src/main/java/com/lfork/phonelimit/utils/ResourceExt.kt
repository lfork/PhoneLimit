package com.lfork.phonelimit.utils

import android.content.Context
import com.lfork.phonelimit.R

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/02/15 16:07
 */
fun Context.getAvatar(index: Int) = when (index) {
    0 -> getDrawable(R.drawable.avatar_male_01)
    1 -> getDrawable(R.drawable.avatar_female_01)
    2 -> getDrawable(R.drawable.avatar_male_02)
    3 -> getDrawable(R.drawable.avatar_female_02)
    4 -> getDrawable(R.drawable.avatar_male_03)
    5 -> getDrawable(R.drawable.avatar_female_03)
    6 -> getDrawable(R.drawable.avatar_male_04)
    7 -> getDrawable(R.drawable.avatar_female_04)
    8 -> getDrawable(R.drawable.avatar_male_05)
    9 -> getDrawable(R.drawable.avatar_female_05)
    10 -> getDrawable(R.drawable.avatar_male_06)
    11 -> getDrawable(R.drawable.avatar_female_06)
    12 -> getDrawable(R.drawable.avatar_male_07)
    13 -> getDrawable(R.drawable.avatar_female_07)
    14 -> getDrawable(R.drawable.avatar_male_08)
    15 -> getDrawable(R.drawable.avatar_female_08)

    else -> getDrawable(R.drawable.ic_portrait_24dp)
}
