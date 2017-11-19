package cn.androidpi.app.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewCompat.TYPE_TOUCH;

/**
 * Created by jastrelax on 2017/11/16.
 */

public class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {

    public interface HeaderListener {
        void onHide();

        void onShow();

        void onStopScroll();
    }

    private List<HeaderListener> mListeners = new ArrayList<>();
    private Scroller mScroller;
    private int mLastY;

    private int DEFAULT_HEIGHT;
    private int mTop;
    private int mHeight;

    public HeaderBehavior() {
        this(null, null);
    }

    public HeaderBehavior(Context context) {
        this(context, null);
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScroller = new Scroller(context);
    }

    public void addHeaderListener(HeaderListener listener) {
        if (null == listener)
            return;
        mListeners.add(listener);
    }

    public void removeHeaderListener(HeaderListener listener) {
        if (null == listener) {
            return;
        }
        mListeners.remove(listener);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        super.onLayoutChild(parent, child, layoutDirection);
        if (DEFAULT_HEIGHT == 0) {
            mHeight = DEFAULT_HEIGHT = child.getHeight();
        }
        resetTop();
        setTopAndBottomOffset(mTop);
        return true;
    }

    private void smoothOffsetTopAndBottom(V child, int offset) {
        mScroller.forceFinished(true);
        mLastY = 0;
        mScroller.startScroll(0, 0, 0, offset);
        offsetTopAndBottomStep(child, ValueAnimator.getFrameDelay());
    }

    private void offsetTopAndBottomStep(final V child, final long timeDelta) {
        if (!mScroller.computeScrollOffset()) {
            return;
        }
        int current = mScroller.getCurrY();
        ViewCompat.offsetTopAndBottom(child, current - mLastY);
        mLastY = current;
        child.postDelayed(new Runnable() {
            @Override
            public void run() {
                offsetTopAndBottomStep(child, timeDelta);
            }
        }, timeDelta);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        // The action is pull along vertical axes.
        boolean started = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return started;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (type == TYPE_TOUCH) {
            // If header is visible, it will consume the scroll range until it's invisible.
            if (!isInvisible()) {
                int offset = 0;
                if (dy > 0) {
                    // Pulling up.
                    offset = MathUtils.clamp(-dy, -(mHeight + mTop), 0);
                } else if (dy < 0) {
                    // Pulling down.
                    offset = MathUtils.clamp(-dy, 0, -mTop);
                }
                if (offset != 0) {
                    offsetTop(offset);
                    ViewCompat.offsetTopAndBottom(child, offset);
                }
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (type == TYPE_TOUCH) {
            // If header is invisible and the scrolling content has reached it's top,
            // The pulling down range not consumed by the scrolling view is consumed by the header.
            if (isInvisible()) {
                if (dyUnconsumed < 0) {
                    int offset = MathUtils.clamp(-dyUnconsumed, 0, -mTop);
                    offsetTop(offset);
                    ViewCompat.offsetTopAndBottom(child, offset);
                }
            }
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        if (type == TYPE_TOUCH) {
            if (isCompleteVisible()) {
                for (HeaderListener l : mListeners) {
                    l.onStopScroll();
                }
            }
        }
    }

    private void resetTop() {
        mTop = -DEFAULT_HEIGHT;
    }

    private void offsetTop(int offset) {
        mTop += offset;
    }

    private boolean isCompleteVisible() {
        return mTop >= 0;
    }

    private boolean isPartialVisible() {
        return mTop > -DEFAULT_HEIGHT && mTop < 0;
    }

    private boolean isInvisible() {
        return mTop <= -DEFAULT_HEIGHT;
    }
}
