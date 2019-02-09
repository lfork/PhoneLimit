package com.lfork.phonelimitadvanced.limit

import android.support.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(
    LimitTaskConfig.LIMIT_MODEL_LIGHT,
    LimitTaskConfig.LIMIT_MODEL_FLOATING,
    LimitTaskConfig.LIMIT_MODEL_ULTIMATE,
    LimitTaskConfig.LIMIT_MODEL_ROOT
)
@Retention(RetentionPolicy.SOURCE)
annotation class LimitModelType