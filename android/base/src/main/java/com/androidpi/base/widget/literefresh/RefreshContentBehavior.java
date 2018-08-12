package com.androidpi.base.widget.literefresh;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * A behavior for scrollable child of {@link CoordinatorLayout}.
 *
 * View with this behavior must be a direct child of {@link CoordinatorLayout}.
 *
 * Created by jastrelax on 2017/11/16.
 */

public class RefreshContentBehavior<V extends View> extends ViewOffsetBehavior<V> {

    public RefreshContentBehavior() {
    }

    public RefreshContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        if (null != lp) {
            CoordinatorLayout.Behavior behavior = lp.getBehavior();
            return behavior instanceof HeaderBehavior
                    || behavior instanceof FooterBehavior;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        CoordinatorLayout.Behavior behavior = lp.getBehavior();
        int offset = 0;
        if (behavior instanceof HeaderBehavior) {
            offset = dependency.getBottom() - child.getTop();
        } else if (behavior instanceof FooterBehavior) {
            offset = dependency.getTop() - child.getBottom();
        }
        if (offset != 0) {
            setTopAndBottomOffset(getTopAndBottomOffset() + offset);
            return true;
        }
        return false;
    }
}