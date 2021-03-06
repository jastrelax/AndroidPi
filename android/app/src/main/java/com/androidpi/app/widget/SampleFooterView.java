package com.androidpi.app.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.androidpi.app.R;

/**
 * Created by jastrelax on 2018/9/5.
 */
public class SampleFooterView extends ConstraintLayout{

    public SampleFooterView(Context context) {
        this(context, null);
    }

    public SampleFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SampleFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.view_sample_footer, this);
    }
}
