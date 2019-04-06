package com.lfork.phonelimit.base.widget

import android.content.Context
import android.util.AttributeSet

internal class MarqueeTextView : android.support.v7.widget.AppCompatTextView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun isFocused(): Boolean {
        return true
    }
}

/*
<style name="username_marquee_text">
<item name="android:textColor">@color/colorPrimary</item>
<item name="android:textSize">36sp</item>
<item name="android:singleLine">true</item>
<item name="android:ellipsize">marquee</item>
<item name="android:marqueeRepeatLimit">marquee_forever</item>
<item name="android:focusable">true</item>
<item name="android:focusableInTouchMode">true</item>
<!--android:singleLine="true" //单行显示-->
<!--android:ellipsize="marquee" //跑马灯显示(动画横向移动)-->
<!--android:marqueeRepeatLimit="marquee_forever"//永久滚动-->
<!--android:focusable="true" //控件是否能够获取焦点-->
<!--android:focusableInTouchMode="true" //是否在触摸模式下获得焦点-->
<!---->
</style>

        */