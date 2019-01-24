package com.yorhp.transcribescreen.view.myView;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yorhp.transcribescreen.R;


public class RefreshableViewRecycle extends LinearLayout implements View.OnTouchListener {

    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;

    /**
     * 释放立即刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;

    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;

    /**
     * 刷新完成或未刷新状态
     */
    public static final int STATUS_REFRESH_FINISHED = 3;

    /**
     * 下拉头部回滚的速度
     */
    public static final int SCROLL_SPEED = -30;


    /**
     * 下拉的长度
     */

    private int pullLength;


    /**
     * 下拉刷新的回调接口
     */
    private PullToRefreshListener mListener;


    /**
     * 下拉头的View
     */
    private View header;

    /**
     * 需要去下拉刷新的ListView
     */
    private RecyclerView listView;

    /**
     * 刷新时显示的进度条
     */
    //private ProgressBar progressBar;


    //三角形
    private ImageView iv_triangle;

    /**
     * 指示下拉和释放的箭头
     */
    //private ImageView arrow;

    /**
     * 指示下拉和释放的文字描述
     */
    //private TextView description;


    /**
     * 下拉头的布局参数
     */
    private MarginLayoutParams headerLayoutParams;

    /**
     * 上次更新时间的毫秒值
     */
    private long lastUpdateTime;

    /**
     * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
     */
    private int mId = -1;

    /**
     * 下拉头的高度
     */
    private int hideHeaderHeight;

    /**
     * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
     * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;
    ;

    /**
     * 记录上一次的状态是什么，避免进行重复操作
     */
    private int lastStatus = currentStatus;

    /**
     * 手指按下时的屏幕纵坐标
     */
    private float yDown;

    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int touchSlop;

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;

    /**
     * 当前是否可以下拉，只有ListView滚动到头的时候才允许下拉
     */
    private boolean ableToPull;

    /**
     * 下拉刷新控件的构造函数，会在运行时动态添加一个下拉头的布局。
     *
     * @param context
     * @param attrs
     */
    public RefreshableViewRecycle(Context context, AttributeSet attrs) {
        super(context, attrs);
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
        //progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        //arrow = (ImageView) header.findViewById(R.id.arrow);
        //description = (TextView) header.findViewById(R.id.description);
        iv_triangle = (ImageView) header.findViewById(R.id.iv_triangle);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    /**
     * 进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给ListView注册touch事件。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            pullLength = hideHeaderHeight / 4 * 3;
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            listView = (RecyclerView) getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
        }
    }


    int preDistance = 0;

    /**
     * 当ListView被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    // 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if (distance <= pullLength && headerLayoutParams.topMargin <= hideHeaderHeight) {
                        return false;
                    }
                    if (distance < touchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > pullLength) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        // 通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (int) ((distance / 2.8) + hideHeaderHeight);
                        header.setLayoutParams(headerLayoutParams);
                        //添加动画（这里是手指控制滑动的动画）、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
                        rotateTriangle((distance - preDistance)/2);
                        preDistance=distance;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            // 时刻记得更新下拉头中的信息  
            if (currentStatus == STATUS_PULL_TO_REFRESH
                    || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态  
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件  
                return true;
            }
        }
        return false;
    }

    /**
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener 监听器的实现。
     * @param id       为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        new HideHeaderTask().execute();
    }

    /**
     * 根据当前ListView的滚动状态来设定 {@link #ableToPull}
     * 的值，每次都需要在onTouch中第一个执行，这样可以判断出当前应该是滚动ListView，还是应该进行下拉。
     *
     * @param event
     */
    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            LinearLayoutManager lm = (LinearLayoutManager) listView.getLayoutManager();
            int firstVisiblePos = lm.findFirstVisibleItemPosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                // 如果首个元素的上边缘，距离父布局值为0，就说明RecyclerView滚动到了最顶部，此时应该允许下拉刷新
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新  
            ableToPull = true;
        }
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {  //下拉状态
                //description.setText(getResources().getString(R.string.pull_to_refresh));
                //arrow.setVisibility(View.VISIBLE);
                //progressBar.setVisibility(View.GONE);
                //rotateArrow();
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {  //释放状态
                //description.setText(getResources().getString(R.string.release_to_refresh));
                //arrow.setVisibility(View.VISIBLE);
                //progressBar.setVisibility(View.GONE);
                //rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {  //刷新中
                //description.setText(getResources().getString(R.string.refreshing));
                //progressBar.setVisibility(View.VISIBLE);
                //arrow.clearAnimation();
                //arrow.setVisibility(View.GONE);
                iv_triangle.clearAnimation();
                TriangelRotate();
            }
        }
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
   /* private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }*/

    float preDegres = 0;

    private void rotateTriangle(float angle) {
        float pivotX = iv_triangle.getWidth() /2;
        float pivotY = (float) (iv_triangle.getHeight() /1.6);
        float fromDegrees = preDegres;
        float toDegrees = angle;

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(10);
        animation.setFillAfter(true);
        iv_triangle.startAnimation(animation);
        preDegres = preDegres + angle;
    }

    private void TriangelRotate(){
        float pivotX = iv_triangle.getWidth() /2;
        float pivotY = (float) (iv_triangle.getHeight() /1.6);
        RotateAnimation animation = new RotateAnimation(0f, 120f, pivotX, pivotY);
        animation.setDuration(50);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        preDegres = 0;
        LinearInterpolator linearInterpolator=new LinearInterpolator();
        animation.setInterpolator(linearInterpolator);
        iv_triangle.startAnimation(animation);
    }




    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     * 下拉超过了，要返回到刷新的位置
     *
     * @author guolin
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= pullLength) {
                    topMargin = pullLength;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(pullLength);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
            //添加动画（这里是手指松开后返回到刷新位置的动画）、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
        }

    }

    /**
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     *
     * @author guolin
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
            //添加动画（这里是手指松开后返回到初始位置的动画）、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、

        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
            //完成刷新、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
            iv_triangle.clearAnimation();
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param time 指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     *
     * @author guolin
     */
    public interface PullToRefreshListener {

        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
         */
        void onRefresh();

    }

}  