package com.example.henry.henrydemos.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henry.Ren on 16/6/30.
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG = FlowLayout.class.getName();

    private static final int CHILD_MARGIN = dp2px(10);   // dp
    /*private static final int PADDING_LEFT = dp2px(10);   // dp
    private static final int PADDING_TOP = dp2px(10);    // dp
    private static final int PADDING_RIGHT = dp2px(10);  // dp
    private static final int PADDING_BOTTOM = dp2px(10); // dp*/

    private List<Integer> mLineHeights = new ArrayList<>();
    private List<Integer> mLineStartPositions = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLineHeights.clear();
        mLineStartPositions.clear();

        int mode4Width = MeasureSpec.getMode(widthMeasureSpec);
        int size4Width = MeasureSpec.getSize(widthMeasureSpec);
        int mode4Height = MeasureSpec.getMode(heightMeasureSpec);
        int size4Height = MeasureSpec.getSize(heightMeasureSpec);

        int width = getPaddingHorizontal(), height = getPaddingVertical();  // layout's  width and height
        int lineWidth = getPaddingHorizontal(), lineHeight = 0;   //  every row's width and height

        int childCount = getChildCount();

        if (childCount > 0) {
            mLineStartPositions.add(0);
        }


        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // measure child
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            if (layoutParams.leftMargin == 0) {
                layoutParams.leftMargin = CHILD_MARGIN;
                layoutParams.topMargin = CHILD_MARGIN;
                layoutParams.rightMargin = CHILD_MARGIN;
                layoutParams.bottomMargin = CHILD_MARGIN;
                child.setLayoutParams(layoutParams);
            }

            // current child's width
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            // current child's height
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if (lineWidth + childWidth <= size4Width) {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            } else {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                mLineHeights.add(lineHeight);
                // start a new row
                lineWidth = getPaddingHorizontal() + childWidth;
                lineHeight = childHeight;
                mLineStartPositions.add(i);
            }

        }

        width = Math.max(width, lineWidth);
        height += lineHeight;
        mLineHeights.add(lineHeight);

        setMeasuredDimension(
                (mode4Width == MeasureSpec.EXACTLY) ? size4Width : width,
                (mode4Height == MeasureSpec.EXACTLY) ? size4Height : height);

    }


    private int getPaddingHorizontal() {
        return getPaddingLeft() + getPaddingRight();
    }

    private int getPaddingVertical() {
        return getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft(), top = getPaddingTop();
        int lineCount = mLineHeights.size();
        for (int i = 0; i < lineCount; i++) {
            int lineHeight = mLineHeights.get(i);  // line height
            int lineStartPosition = mLineStartPositions.get(i);
            int lineEndPosition;
            if (i == lineCount - 1) {
                lineEndPosition = getChildCount() - 1;
            } else {
                lineEndPosition = mLineStartPositions.get(i + 1) - 1;
            }
            for (int j = lineStartPosition; j <= lineEndPosition; j++) {
                //  layout the line
                View child = getChildAt(j);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

                int cl = left + layoutParams.leftMargin;
                int ct = top + layoutParams.topMargin;
                int cr = cl + child.getMeasuredWidth();
                int cb = ct + child.getMeasuredHeight();

                Log.e(TAG, child + " , l = " + cl + " , t = " + ct + " , r ="
                        + cr + " , b = " + cb);

                child.layout(cl, ct, cr, cb);

                left += child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;

            }

            left = getPaddingLeft();
            top += lineHeight;
        }


    }


    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
