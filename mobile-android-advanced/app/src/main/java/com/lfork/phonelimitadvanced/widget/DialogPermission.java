package com.lfork.phonelimitadvanced.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.widget.TextView;

import com.lfork.phonelimitadvanced.R;

/**
 * Created by xian on 2017/2/28.
 */

public class DialogPermission extends BaseDialog {

    private TextView mBtnPermission;
    private onClickListener mOnClickListener;

    public DialogPermission(Context context) {
        super(context);
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        mBtnPermission = (TextView) findViewById(R.id.btn_permission);
        mBtnPermission.setOnClickListener(view -> {
            if (mOnClickListener != null) {
                dismiss();
                mOnClickListener.onClick();
            }
        });
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface onClickListener {
        void onClick();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_permission;
    }

}
