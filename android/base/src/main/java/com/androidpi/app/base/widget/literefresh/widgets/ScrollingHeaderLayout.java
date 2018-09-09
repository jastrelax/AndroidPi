package com.androidpi.app.base.widget.literefresh.widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.androidpi.app.base.widget.literefresh.OnScrollListener;
import com.androidpi.app.base.widget.literefresh.RefreshHeaderBehavior;

/**
 * A scrolling header layout that add a {@link RefreshHeaderBehavior} to itself.
 * Created by jastrelax on 2018/8/19.
 */
public abstract class ScrollingHeaderLayout extends FrameLayout implements OnScrollListener {

    protected RefreshHeaderBehavior behavior;

    public ScrollingHeaderLayout(Context context) {
        this(context, null);
    }

    public ScrollingHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) getLayoutParams();
            behavior = (RefreshHeaderBehavior) params.getBehavior();
            if (behavior == null) {
                behavior = new RefreshHeaderBehavior(getContext());
                params.setBehavior(behavior);
            }
            behavior.addOnScrollListener(this);
        } catch (Exception ignore) {

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) getLayoutParams();
        RefreshHeaderBehavior behavior = (RefreshHeaderBehavior) params.getBehavior();
        if (behavior != null) {
            // todo : remove scroll listener
        }
    }
}