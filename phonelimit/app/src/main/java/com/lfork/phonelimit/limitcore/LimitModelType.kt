package com.lfork.phonelimit.limitcore

import android.support.annotation.IntDef
import com.lfork.phonelimit.data.taskconfig.TaskConfig

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(
    TaskConfig.LIMIT_MODEL_LIGHT,
    TaskConfig.LIMIT_MODEL_FLOATING,
    TaskConfig.LIMIT_MODEL_ULTIMATE,
    TaskConfig.LIMIT_MODEL_ROOT
)
@Retention(RetentionPolicy.SOURCE)
annotation class LimitModelType