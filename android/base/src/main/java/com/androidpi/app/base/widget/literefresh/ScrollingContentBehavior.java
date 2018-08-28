package com.androidpi.app.base.widget.literefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.androidpi.app.pi.base.R;

import static android.support.v4.view.ViewCompat.TYPE_TOUCH;

/**
 * A behavior for nested scrollable child of {@link CoordinatorLayout}.
 * <p>
 * It's attach to the nested scrolling target view, such as {@link android.support.v4.widget.NestedScrollView},
 * {@link android.support.v7.widget.RecyclerView} which implement {@link android.support.v4.view.NestedScrollingChild}.
 * <p>
 * View with this behavior must be a direct child of {@link CoordinatorLayout}.
 * <p>
 * Created by jastrelax on 2018/8/21.
 */
public class ScrollingContentBehavior<V extends View> extends AnimationOffsetBehavior<V, ContentBehaviorController> {

    /**
     * Minimum top and bottom offset of content view.
     */
    protected int minOffset;

    /**
     * Minimum top and bottom offset relative to parent height.
     */
    protected float minOffsetRatio;

    private float minOffsetRatioOfParent;

    /**
     * If set to true, default minimum offset will be {@link #headerVisibleHeight}.
     */
    protected boolean useDefaultMinOffset = false;

    /**
     * The header's height.
     */
    protected int headerHeight;

    /**
     * The header's visible height.
     */
    protected int headerVisibleHeight;

    /**
     * The footer's maximum offset.
     */
    protected int footerMaxOffset;

    /**
     * The footer's height.
     */
    protected int footerHeight;

    /**
     * The footer's visible height. Maybe not very useful.
     */
    protected int footerVisibleHeight = 0;

    protected int defaultMinOffset;
    private boolean isFirstLayout = true;
    private boolean layoutNow = false;

    {
        controller = new ContentBehaviorController(this);
        addScrollListener(controller);
    }

    public ScrollingContentBehavior() {

    }

    public ScrollingContentBehavior(Context context) {
        super(context);
    }

    public ScrollingContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ContentBehavior, 0, 0);
        boolean hasMinOffset = a.hasValue(R.styleable.ContentBehavior_lr_minOffset);
        if (hasMinOffset) {
            minOffset = a.getDimensionPixelOffset(R.styleable.ContentBehavior_lr_minOffset, 0);
            defaultMinOffset = minOffset;
        }

        boolean hasMinOffsetRatio = a.hasValue(R.styleable.ContentBehavior_lr_minOffsetRatio);
        if (hasMinOffsetRatio) {
            minOffsetRatio = a.getFraction(R.styleable.ContentBehavior_lr_minOffsetRatio, 1, 1, 0f);
            minOffsetRatioOfParent = a.getFraction(R.styleable.ContentBehavior_lr_minOffsetRatio, 1, 2, 0f);
        }
        if (!hasMinOffset && !hasMinOffsetRatio) {
            useDefaultMinOffset = true;
        }
        if (a.hasValue(R.styleable.ContentBehavior_lr_headerVisibleHeight)) {
            headerVisibleHeight = a.getDimensionPixelOffset(R.styleable.ContentBehavior_lr_headerVisibleHeight, 0);
        }
        a.recycle();
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, child, layoutDirection);
        if (isFirstLayout || layoutNow) {
            layoutNow = false;
            if (isFirstLayout) {
                // Compute max offset, it will not exceed parent height.
                footerMaxOffset = (int) ((1 - GOLDEN_RATIO) * parent.getHeight());

                if (!useDefaultMinOffset) {
                    minOffset = (int) Math.max(minOffset, minOffsetRatioOfParent > minOffsetRatio ? minOffsetRatio * parent.getHeight() : minOffsetRatio * child.getHeight());
                    defaultMinOffset = minOffset;
                } else {
                    maxOffset = Math.max(maxOffset, GOLDEN_RATIO * parent.getHeight());
                }
            }
            cancelAnimation();
            setTopAndBottomOffset(headerVisibleHeight);
            isFirstLayout = false;
        }
        return handled;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        boolean start = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (start) {
            for (ScrollingListener l : mListeners) {
                l.onStartScroll(coordinatorLayout, child, (int) maxOffset, type == TYPE_TOUCH);
            }
        }
        return start;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0) {
            // When scrolling up, compute the top offset which content can reach.
            int topOffset = minOffset;
            if (child.getTop() <= topOffset)
                return;
            int offset = MathUtils.clamp(-dy, topOffset - child.getTop(), 0);
            if (offset != 0) {
                consumeOffset(coordinatorLayout, child, offset, type, true);
                consumed[1] = -offset;
            }
        } else if (dy < 0){
            // When scrolling down, if footer is still visible.
            if (child.getBottom() < coordinatorLayout.getHeight()) {
                int offset = MathUtils.clamp(-dy, 0, coordinatorLayout.getHeight() - child.getBottom());
                consumeOffset(coordinatorLayout, child, offset, type, true);
                consumed[1] = -offset;
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // If there is unconsumed pixels.
        if (dyUnconsumed < 0) {
            // Scrolling down.
            // If top position of child can not scroll exceed maximum offset.
            if (child.getTop() >= maxOffset)
                return;
            int offset = MathUtils.clamp(-dyUnconsumed, 0, (int) maxOffset - child.getTop());
            if (offset != 0) {
                if (child.getTop() >= headerVisibleHeight) {
                    // When header's visible part is totally visible, do not consume none touch scroll,
                    // content can scroll to the maximum offset with the touch.
                    if (type != TYPE_TOUCH)
                        return;
                    consumeOffset(coordinatorLayout, child, offset, type, false);
                } else {
                    // Recompute the offset so that the top does not exceed headerVisibleHeight.
                    offset = MathUtils.clamp(-dyUnconsumed, 0, headerVisibleHeight - child.getTop());
                    consumeOffset(coordinatorLayout, child, offset, type, true);
                }
            }
        } else if (dyUnconsumed > 0) {
            // Scrolling up.
            // Can not scroll exceed footer maximum offset.
            int maxFooterTopBottomOffset = coordinatorLayout.getHeight() - footerMaxOffset;
            if (child.getBottom() <= maxFooterTopBottomOffset)
                return;
            int offset = MathUtils.clamp(-dyUnconsumed,  maxFooterTopBottomOffset - child.getBottom(),0);
            if (offset != 0) {
                if (coordinatorLayout.getHeight() - child.getBottom() >= footerVisibleHeight) {
                    // If footer's visible part is totally visible, ignore fling too.
                    if (type != TYPE_TOUCH)
                        return;
                    consumeOffset(coordinatorLayout, child, offset, type, false);
                } else {
                    // Recompute it.
                    offset = MathUtils.clamp(-dyUnconsumed, -(footerVisibleHeight - coordinatorLayout.getHeight() + child.getBottom()), 0);
                    consumeOffset(coordinatorLayout, child, offset, type, true);
                }
            }
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        for (ScrollingListener l : mListeners) {
            l.onStopScroll(coordinatorLayout, child, getTopAndBottomOffset(), (int) maxOffset, type == TYPE_TOUCH);
        }
    }

    /**
     *
     * @param coordinatorLayout
     * @param child
     * @param offsetDelta
     * @param type
     * @param consumeRawOffset consume raw offset or not, eg. for a smooth fling action we may not just keep it.
     * @return
     */
    private int consumeOffset(CoordinatorLayout coordinatorLayout, View child, int offsetDelta, int type, boolean consumeRawOffset) {
        int currentOffset = getTopAndBottomOffset();
        // Before child consume the offset.
        for (ScrollingListener l : mListeners) {
            l.onPreScroll(coordinatorLayout, child, currentOffset, (int) maxOffset, type == TYPE_TOUCH);
        }
        float consumed = consumeRawOffset ? offsetDelta : onConsumeOffset(currentOffset, (int) maxOffset, offsetDelta);
        currentOffset = Math.round(currentOffset + consumed);
        setTopAndBottomOffset(currentOffset);
        // In CoordinatorLayout the onChildViewsChanged() will be called after calling behavior's onNestedScroll().
        // The content view itself can make some transformation by setTranslationY() that may keep it's drawing rectangle
        // unchanged while it's offset has changed. In this case CoordinatorLayout will not call onDependentViewChanged().
        // So We need to call onDependentViewChanged() manually.
        coordinatorLayout.dispatchDependentViewsChanged(child);
        for (ScrollingListener l : mListeners) {
            l.onScroll(coordinatorLayout, child, currentOffset, offsetDelta, (int) maxOffset, type == TYPE_TOUCH);
        }
        return currentOffset;
    }

    protected float onConsumeOffset(int current, int max, int delta) {
        return delta;
    }

    /**
     * This will reset the header or footer view to it's original position when it's laid out for the first time.
     */
    protected void reset(long animateDuration) {
        if (null == getChild() || getParent() == null) return;

        // Reset footer first, then consider header.
        // Based on a strong contract that headerVisibleHeight is a distance from parent top.
        int offset;
        if (- getChild().getBottom() + getParent().getHeight() > 0) {
            offset = getParent().getHeight() - getChild().getBottom();
        } else {
            offset = headerVisibleHeight - getTopAndBottomOffset();
        }
        animateOffsetDeltaWithDuration(getParent(), getChild(), offset, animateDuration);
    }

    /**
     * Make the header view entirely visible.
     */
    protected void showHeader(long animateDuration) {
        if (null == getChild()) return;
        int offset = getHeaderHeight() - getChild().getTop();
        animateOffsetDeltaWithDuration(getParent(), getChild(), offset, animateDuration);
    }

    void showFooter(long animationDuration) {
        if (null == getChild()) return;
        int offset = getParent().getHeight() - getFooterHeight() - getChild().getBottom();
        animateOffsetDeltaWithDuration(getParent(), getChild(), offset, animationDuration);
    }

    private Runnable offsetCallback;

    /**
     * If view has scroll to a invalid position, reset it, otherwise do nothing.
     * @param holdOn
     */
    protected void stopScroll(boolean holdOn) {
        int currentOffset = getTopAndBottomOffset();
        // If content offset is larger than header's visible height or smaller than zero,
        // which means content has scrolled to a insignificant or invalid position.
        if (currentOffset > headerVisibleHeight || currentOffset < 0) {
            if (getChild().getHandler() == null) return;
            // Remove previous pending callback.
            handler.removeCallbacks(offsetCallback);
            offsetCallback = new Runnable() {
                @Override
                public void run() {
                    reset(RESET_DURATION);
                }
            };
            handler.postDelayed(offsetCallback, holdOn ? HOLD_ON_DURATION : 0L);
        }
    }


    void resetMinOffset() {
        this.minOffset = defaultMinOffset;
    }

    void setMinOffset(int minOffset) {
        this.minOffset = minOffset;
    }

    public int getMinOffset() {
        return minOffset;
    }

    public void setFooterVisibleHeight(int footerVisibleHeight) {
        this.footerVisibleHeight = footerVisibleHeight;
    }

    public void setFooterMaxOffset(int footerMaxOffset) {
        this.footerMaxOffset = footerMaxOffset;
    }

    public void setFooterHeight(int footerHeight) {
        this.footerHeight = footerHeight;
    }

    public int getFooterHeight() {
        return footerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getHeaderVisibleHeight() {
        return headerVisibleHeight;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderVisibleHeight(int headerVisibleHeight) {
        this.headerVisibleHeight = headerVisibleHeight;
        runWithView(new Runnable() {
            @Override
            public void run() {
                layoutNow = true;
                getChild().requestLayout();
            }
        });
    }

    int getHeaderReadyRefreshHeight() {
        return headerVisibleHeight;
    }
}
