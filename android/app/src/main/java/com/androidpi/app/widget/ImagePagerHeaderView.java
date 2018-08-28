package com.androidpi.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidpi.app.R;

/**
 * Created by jastrelax on 2018/8/13.
 */
public class ImagePagerHeaderView extends ScalableHeaderLayout {

    private ViewPager viewPager;

    public ImagePagerHeaderView(Context context) {
        this(context, null);
    }

    public ImagePagerHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePagerHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.view_image_pager, this);
        viewPager = findViewById(R.id.view_pager);
    }

    public void setFragmentManager(FragmentManager fm) {
        viewPager.setAdapter(new ImagePagerAdapter(fm));
    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {

        private final int[] images = new int[]{R.mipmap.photo1, R.mipmap.photo2, R.mipmap.photo3};

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePageFragment.newInstance(images[position]);
        }

        @Override
        public int getCount() {
            return images.length;
        }
    }

    public static class ImagePageFragment extends Fragment {

        private static final String ARGS_IMAGE_RES = "ARGS_IMAGE_RES";

        public static ImagePageFragment newInstance(@DrawableRes int imageRes) {
            Bundle args = new Bundle();
            args.putInt(ARGS_IMAGE_RES, imageRes);
            ImagePageFragment fragment = new ImagePageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        public ImagePageFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_image_page, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (getArguments() == null) return;
            ImageView imageView = view.findViewById(R.id.iv_image);
            imageView.setImageResource(getArguments().getInt(ARGS_IMAGE_RES));
        }
    }
}
