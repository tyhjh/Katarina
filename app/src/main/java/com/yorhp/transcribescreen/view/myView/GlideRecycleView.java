package com.yorhp.transcribescreen.view.myView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

/**
 * Created by Tyhj on 2017/10/24.
 */

public class GlideRecycleView extends RecyclerView {
    public GlideRecycleView(Context context) {
        super(context);
    }

    public GlideRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GlideRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case SCROLL_STATE_IDLE:
                //当屏幕停止滚动，加载图片
                try {
                    if (getContext() != null) Glide.with(getContext()).resumeRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SCROLL_STATE_DRAGGING:
                //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                try {
                    if (getContext() != null) Glide.with(getContext()).pauseRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }
}
