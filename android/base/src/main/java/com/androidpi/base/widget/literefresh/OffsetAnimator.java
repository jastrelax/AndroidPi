package com.androidpi.base.widget.literefresh;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Created by jastrelax on 2018/8/8.
 */
public abstract class OffsetAnimator {

    public interface AnimationUpdateListener {
        void onAnimationUpdate(int value);
    }

    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * Cancel running animation.
     */
    public abstract void cancel();

    /**
     * Animate current offset to destination offset with a duration.
     *
     * @param current  current offset
     * @param offset   destination offset
     * @param duration animation duration
     * @param listener animaiton listener
     */
    public abstract void animateOffsetWithDuration(int current, int offset, long duration, AnimationUpdateListener listener);
}
