package com.lfork.phonelimit.base.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class ScrollLinearLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 25f;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }

    public ScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return ScrollLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                return 2;
            }

        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }


    /**
     *  @Override
     *             protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
     *                 View view = getChildAt(0);
     *                 if (view != null) {
     *                     final int firstChildPos = getPosition(getChildAt(0)); //获取当前item的position
     *                     int delta = Math.abs(position - firstChildPos);//算出需要滑动的item数量
     *                     if (delta == 0)
     *                         delta = 1;
     *                     return (MILLISECONDS_PER_INCH / delta) / displayMetrics.densityDpi;
     *                 } else {
     *                     return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
     *                 }
     *
     *             }
     */


}