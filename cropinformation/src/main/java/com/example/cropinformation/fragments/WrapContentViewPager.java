package com.example.cropinformation.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class WrapContentViewPager extends ViewPager {

    private int mCurrentPagePosition = 0;

    private final int[] heightArr = new int[3];

    public WrapContentViewPager(@NonNull Context context) {
        super(context);
    }

    public WrapContentViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int height = 0;
//        int childWidthSpec = MeasureSpec.makeMeasureSpec(
//                Math.max(0, MeasureSpec.getSize(widthMeasureSpec) -
//                        getPaddingLeft() - getPaddingRight()),
//                MeasureSpec.getMode(widthMeasureSpec)
//        );
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(childWidthSpec, MeasureSpec.UNSPECIFIED);
//            int h = child.getMeasuredHeight();
//            if (h > height) height = h;
//        }
//
//        if (height != 0) {
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            // super has to be called in the beginning so the child views can be initialized.
            // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            int numOfElements = getChildCount();
            for (int i = 0; i < numOfElements; i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                height = h;
                //if (h > height) height = h;
                heightArr[i]=height;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightArr[mCurrentPagePosition], MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reMeasureCurrentPage(int position) {
        mCurrentPagePosition = position;
        requestLayout();
    }
}
