package cn.androidpi.app.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jastrelax on 2017/11/17.
 */

public class PullDownHeaderBehavior extends HeaderBehavior implements PullingRefresher {

    private List<PullingListener> mListeners = new ArrayList<>();

    public PullDownHeaderBehavior() {
        this(null, null);
    }

    public PullDownHeaderBehavior(Context context) {
        this(context, null);
    }

    public PullDownHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        addHeaderListener(new HeaderListener() {
            @Override
            public void onHide() {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onStopScroll() {
                for (PullingListener l : mListeners) {
                    l.onRefresh();
                }
            }
        });
    }

    public void addPullDownListener(PullingListener listener) {
        if (null == listener)
            return;
        mListeners.add(listener);
    }

    public void removePullDownListener(PullingListener listener) {
        if (null == listener)
            return;
        mListeners.remove(listener);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void refreshFinish() {

    }

    @Override
    public void refreshTimeout() {

    }

    @Override
    public void refreshCancelled() {

    }

    @Override
    public void refreshException(Exception exception) {

    }
}
