package com.androidpi.app.base.widget.literefresh;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by jastrelax on 2018/8/19.
 */
public abstract class RefreshHeaderLayout extends FrameLayout implements OnPullListener, OnRefreshListener{

    public RefreshHeaderLayout(Context context) {
        this(context, null);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) getLayoutParams();
            RefreshHeaderBehavior behavior = (RefreshHeaderBehavior) params.getBehavior();
            if (behavior == null) {
                behavior = new RefreshHeaderBehavior(getContext());
                params.setBehavior(behavior);
            }
            behavior.addOnPullingListener(this);
            behavior.addOnRefreshListener(this);
        } catch (Exception e) {

        }
    }
}