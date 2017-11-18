package com.yorhp.transcribescreen.view.myView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.yorhp.transcribescreen.R;

import java.util.ArrayList;


/**
 * Created by Tyhj on 2017/10/18.
 */

public class LoopBannerView extends FrameLayout implements View.OnTouchListener {

    private Context context;
    private onBannerClickListener onBannerClickListener;

    public interface onBannerClickListener {
        void onBannerTouch(int item);
    }

    private ArrayList<String> imageUrl;

    //高度dp
    private static int ROOTVIEWHEIGHT = 200;
    //想要展示的item数量+2
    private static int ITEM_COUNT = 6;
    //跳转间隔时间
    private static int MOVE_INTERVAL = 4000;
    //跳转动画时间
    private static int MOVE_TIME = 300;
    //手指翻页速度上限,达到即翻页
    private static float MIN_POINT_SPEED = 1.1f;
    //手指翻页时页面跳转时间
    private static int POINT_SKIP_TIME = 140;

    LinearLayout ll_root, ll_point;
    ImageView iv_first, iv_first_copy, iv_last, iv_last_copy, iv_other1, iv_other2;
    ImageView iv_point1, iv_point2, iv_point3, iv_point4;
    ImageView[] pointViews;
    ImageView[] images;

    //判断是否触摸banner
    boolean isTouch;
    //每个item长度
    int viewWidth;
    //移动
    ObjectAnimator animator = null;
    //当前item
    int curItem = 1;
    //当前手指划过点X坐标
    int previewX = 0;
    //当前偏移量
    int translationX = 0;
    //按下时手指坐标
    int pointDownX = 0;
    //手指离开时的坐标
    int pointLeaveX = 0;
    //手指按下时的时间
    int pointDownTime = 0;
    //手指离开时的时间
    int pointLeaveTime = 0;
    //banner是否正在处于自动切换，且正在切换动画中
    boolean isAnimation;
    //手指是否离开一段时间了
    private static boolean isPointLvLong = true;


    public LoopBannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.loopbanner, this);

        iv_first = (ImageView) findViewById(R.id.iv_first);
        iv_first_copy = (ImageView) findViewById(R.id.iv_first_copy);

        iv_other1 = (ImageView) findViewById(R.id.iv_other1);
        iv_other2 = (ImageView) findViewById(R.id.iv_other2);

        iv_last = (ImageView) findViewById(R.id.iv_last);
        iv_last_copy = (ImageView) findViewById(R.id.iv_last_copy);


        iv_point1 = (ImageView) findViewById(R.id.iv_point1);
        iv_point2 = (ImageView) findViewById(R.id.iv_point2);
        iv_point3 = (ImageView) findViewById(R.id.iv_point3);
        iv_point4 = (ImageView) findViewById(R.id.iv_point4);


        pointViews = new ImageView[]{iv_point1, iv_point2, iv_point3, iv_point4};
        images = new ImageView[]{iv_first, iv_first_copy, iv_other1, iv_other2, iv_last, iv_last_copy};

        Picasso.with(context).load("http://tastespirit.com/img/GEWURZTRAMINER-5.jpg").into(iv_first);
        Picasso.with(context).load("http://tastespirit.com/img/GEWURZTRAMINER-5.jpg").into(iv_first_copy);

        Picasso.with(context).load("http://img5.imgtn.bdimg.com/it/u=3469050749,2701596166&fm=27&gp=0.jpg").into(iv_last);
        Picasso.with(context).load("http://img5.imgtn.bdimg.com/it/u=3469050749,2701596166&fm=27&gp=0.jpg").into(iv_last_copy);

        Picasso.with(context).load("http://img1.imgtn.bdimg.com/it/u=907245543,1366014994&fm=27&gp=0.jpg").into(iv_other1);
        Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=3746288086,1167920727&fm=27&gp=0.jpg").into(iv_other2);

        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        ll_point = (LinearLayout) findViewById(R.id.ll_point);
        setOnTouchListener(this);
    }


    public void initPoint() {
        iv_point1.setImageResource(R.drawable.ic_point_nomal);
        iv_point2.setImageResource(R.drawable.ic_point_nomal);
        iv_point3.setImageResource(R.drawable.ic_point_nomal);
        iv_point4.setImageResource(R.drawable.ic_point_nomal);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);

        ll_root.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * ITEM_COUNT, dp2px(ROOTVIEWHEIGHT)));
        ll_point.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(ROOTVIEWHEIGHT)));

        iv_first.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));
        iv_first_copy.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));

        iv_last.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));
        iv_last_copy.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));

        iv_other1.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));
        iv_other2.setLayoutParams(new LinearLayout.LayoutParams(viewWidth * 1, dp2px(ROOTVIEWHEIGHT)));


        reSetBanner();
        pointViews[0].setImageResource(R.drawable.ic_point_selected);

    }

    //直接跳转到第一个页面
    private void reSetBanner() {
        Animator animator = ObjectAnimator.ofFloat(ll_root, "translationX", translationX, -viewWidth);
        animator.setDuration(1);
        animator.setStartDelay(100);
        animator.start();
        curItem = 2;
        translationX = -viewWidth;
    }

    //dp转换成px
    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //开始循环滚动
    public void startLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(MOVE_INTERVAL + MOVE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isTouch && isPointLvLong) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

       /* if (isAnimation) {
            return false;
        }*/
        if (isAnimation && animator != null && animator.isRunning()) {
            animator.cancel();
        }

        if (animator == null || !animator.isRunning()) {
            isAnimation = false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointDownX = (int) event.getX();
                pointDownTime = (int) System.currentTimeMillis();
                isTouch = true;
                isPointLvLong = false;
                break;
            case MotionEvent.ACTION_UP:
                pointLeaveTime = (int) System.currentTimeMillis();
                pointLeaveX = (int) event.getX();
                onPointLeave();
                break;
            case MotionEvent.ACTION_MOVE:
                moveWithPoint((int) event.getX());
                break;
            case MotionEvent.ACTION_CANCEL:
                pointLeaveTime = (int) System.currentTimeMillis();
                pointLeaveX = (int) event.getX();
                onPointLeave();
                break;
        }
        return true;
    }

    //手指离开界面
    private void onPointLeave() {
        isAnimation = true;
        float time = pointLeaveTime - pointDownTime;
        float distance = pointLeaveX - pointDownX;
        //Log.e("speed", "time：" + time + " distance：" + distance);
        Log.e("curItem", "curItem1：" + curItem + "translationX：" + translationX);

        float speed = Math.abs(distance / time);
        if (speed >= MIN_POINT_SPEED || Math.abs(distance) >= (viewWidth / 3)) {//达到翻页的条件
            if (distance < 0) {
                if (curItem < ITEM_COUNT)
                    curItem++;
            } else {
                if (curItem > 1)
                    curItem--;
            }
            Log.e("curItem", "curItem：" + curItem + "translationX：" + translationX);
        } else {
            curItem = Math.abs(translationX % viewWidth) > viewWidth / 2 ? Math.abs(translationX / viewWidth) + 2 : Math.abs(translationX / viewWidth) + 1;
            if (curItem < 1)
                curItem = 1;
            if (curItem > ITEM_COUNT)
                curItem = ITEM_COUNT;
        }
        moveAnim(viewWidth * (1 - curItem), POINT_SKIP_TIME);
        isTouch = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if (!isTouch) {
                        isPointLvLong = true;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        previewX = 0;

        if(onBannerClickListener!=null){
            if(time<100){
                onBannerClickListener.onBannerTouch(curItem-1);
            }
        }
    }

    //跟随手指移动
    private void moveWithPoint(int x) {
        if (previewX != 0) {
            int distanceX = x - previewX;
            moveAnim(translationX + distanceX, 10);
        }
        previewX = x;
    }

    //平移动画
    private void moveAnim(int toX, int moveTime) {

        //Log.e("toX","toX："+toX);
        if (toX > 0) {
            return;
        }
        if (Math.abs(toX) > (ITEM_COUNT - 1) * viewWidth) {
            return;
        }

        animator = ObjectAnimator.ofFloat(ll_root, "translationX", translationX, toX);
        animator.setDuration(moveTime);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //Log.e("Animator",animation+"");
                if (curItem == ITEM_COUNT && isAnimation) {
                    reSetBanner();
                }

                if (curItem == 1 && isAnimation) {
                    Animator animator = ObjectAnimator.ofFloat(ll_root, "translationX", translationX, -viewWidth * (ITEM_COUNT - 2));
                    animator.setDuration(1);
                    animator.setStartDelay(100);
                    animator.start();
                    translationX = -viewWidth * (ITEM_COUNT - 2);
                    curItem = ITEM_COUNT - 1;
                }

                initPoint();
                if (curItem == 1) {
                    pointViews[pointViews.length].setImageResource(R.drawable.ic_point_selected);
                } else if (curItem == ITEM_COUNT) {
                    pointViews[0].setImageResource(R.drawable.ic_point_selected);
                } else {
                    pointViews[curItem - 2].setImageResource(R.drawable.ic_point_selected);
                }


                isAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimation = false;
                translationX = (int) ll_root.getTranslationX();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        translationX = toX;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && !isTouch && isPointLvLong) {
                isAnimation = true;
                translationX = viewWidth * (1 - curItem);
                moveAnim(-viewWidth * curItem, MOVE_TIME);
                curItem++;
            }
        }
    };


    public void setImageUrl(ArrayList<String> imageUrl) {
        this.imageUrl.clear();
        this.imageUrl.addAll(imageUrl);
        for (int i = 0; i < images.length; i++) {
            if(i==0){
                Picasso.with(context).load(imageUrl.get(imageUrl.size()-1)).into(images[0]);
            }else if(i==images.length-1){
                Picasso.with(context).load(imageUrl.get(0)).into(images[i]);
            }else {
                Picasso.with(context).load(imageUrl.get(i-1)).into(images[i]);
            }
        }
    }

    public void setOnBannerClickListener(final onBannerClickListener listener) {
        this.onBannerClickListener=listener;
    }
}
