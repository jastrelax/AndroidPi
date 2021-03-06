package com.androidpi.app.widget;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.AttributeSet;
import android.view.View;

import com.androidpi.app.R;
import com.androidpi.app.activity.VideoActivity;
import com.androidpi.literefresh.OnScrollListener;
import com.androidpi.literefresh.widget.RefreshHeaderLayout;

import static android.support.v4.view.ViewCompat.TYPE_TOUCH;

/**
 * Created by jastrelax on 2018/8/20.
 */
public class VideoHeaderView extends RefreshHeaderLayout implements OnScrollListener {

    private boolean launched;

    public VideoHeaderView(Context context) {
        this(context, null);
    }

    public VideoHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.view_video_header, this);
    }

    @Override
    public void onStartScroll(CoordinatorLayout parent, View view, int initial, int trigger, int min, int max, int type) {
        if (type == TYPE_TOUCH) {
            launched = false;
        }
    }

    @Override
    public void onScroll(CoordinatorLayout parent, View view, int current, int delta, int initial, int trigger, int min, int max, int type) {
        if (type != TYPE_TOUCH || launched) return;
        if ((current /(float) max) >= 0.85f) {
            String sharedElementName = getResources().getString(R.string.transition_header);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), this, sharedElementName);
            VideoActivity.Companion.start(getContext(), options.toBundle());
            launched = true;
        }
    }

    @Override
    public void onStopScroll(CoordinatorLayout parent, View view, int current, int initial, int trigger, int min, int max, int type) {
    }

}
