package com.yorhp.transcribescreen.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingMenuLayout;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.BaseActivity;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.module.Gif;
import com.yorhp.transcribescreen.utils.Defined;
import com.yorhp.transcribescreen.utils.Mv2Gif;
import com.yorhp.transcribescreen.utils.ScreenRecorder;
import com.yorhp.transcribescreen.view.adapter.Adapter;
import com.yorhp.transcribescreen.view.fragement.MenuListFragment;
import com.yorhp.transcribescreen.view.myView.LoopBannerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;


@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private String transcribeMoviePath;
    private String fileName;
    private static final int REQUEST_CODE = 1;
    private static final int VIDEO_CAPTURE=3;
    private static final int EXTRA_DURATION_LIMIT=100;
    private static final long EXTRA_SIZE_LIMIT=10485760L*500;
    private AnimatorSet animatorSet, animatorSetBack, transcribeStart, transcribeStop;

    MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private boolean menuOpen;

    Adapter adapter;

    ArrayList<Gif> gifs = new ArrayList<>();

    @ViewById
    FlowingDrawer drawerlayout;

    @ViewById
    RecyclerView rcly_gif;

    @ViewById
    FloatingActionButton fab_add;

    @ViewById
    ImageView fab_item1, fab_item2, fab_item3;

    @ViewById
    FlowingMenuLayout menulayout;

    @ViewById
    FrameLayout container_menu;

    @ViewById
    LoopBannerView lbv_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidthDip = dm.widthPixels;// 屏幕宽（dip，如：320dip）
        Defined.scrWidth = Defined.dp2px(this, screenWidthDip);

    }

    @AfterViews
    void afterView() {
        initView();
        initdrawerLayout();
        initAnimator();
        initRclyView();
    }

    private void initView() {
        fab_item1.setClipToOutline(true);
        fab_item1.setOutlineProvider(Defined.getOutline(true, 100, 0));

        fab_item2.setClipToOutline(true);
        fab_item2.setOutlineProvider(Defined.getOutline(true, 100, 0));

        fab_item3.setClipToOutline(true);
        fab_item3.setOutlineProvider(Defined.getOutline(true, 100, 0));

        lbv_banner.startLoop();
        lbv_banner.setOnBannerClickListener(new LoopBannerView.onBannerClickListener() {
            @Override
            public void onBannerTouch(int item) {
                cloneMenu();
            }
        });
    }

    private void initRclyView() {
        gifs.add(new Gif(0, "http://img1.imgtn.bdimg.com/it/u=907245543,1366014994&fm=27&gp=0.jpg", "Tyhj", 0));
        gifs.add(new Gif(0, "http://img3.imgtn.bdimg.com/it/u=3746288086,1167920727&fm=27&gp=0.jpg", "Bouncing Ball", 0));
        gifs.add(new Gif(0, "http://img1.imgtn.bdimg.com/it/u=2646813706,2163648913&fm=27&gp=0.jpg", "Waving Man", 0));
        gifs.add(new Gif(0, "http://img0.imgtn.bdimg.com/it/u=1486507027,3356122497&fm=27&gp=0.jpg", "Animator", 0));
        gifs.add(new Gif(0, "http://img5.imgtn.bdimg.com/it/u=2956650276,3883767597&fm=27&gp=0.jpg", "tyhj", 0));
        adapter = new Adapter(this, gifs);
        rcly_gif.setAdapter(adapter);
        rcly_gif.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rcly_gif.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                cloneMenu();
            }
        });

    }

    private void initAnimator() {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(fab_add, "rotation", 135);
        animator1.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack1 = ObjectAnimator.ofFloat(fab_add, "rotation", 135, 0);
        animatorBack1.setInterpolator(new DecelerateInterpolator());


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(fab_item1, "translationX", 0, -250);
        animator2.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack2 = ObjectAnimator.ofFloat(fab_item1, "translationX", -250, 0);
        animatorBack2.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(fab_item2, "translationX", 0, -170);
        animator3.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack3 = ObjectAnimator.ofFloat(fab_item2, "translationX", -170, 0);
        animatorBack3.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animator3_2 = ObjectAnimator.ofFloat(fab_item2, "translationY", 0, -170);
        animator3_2.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack3_2 = ObjectAnimator.ofFloat(fab_item2, "translationY", -170, 0);
        animatorBack3_2.setInterpolator(new AccelerateInterpolator());


        ObjectAnimator animator4 = ObjectAnimator.ofFloat(fab_item3, "translationY", 0, -250);
        animator4.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack4 = ObjectAnimator.ofFloat(fab_item3, "translationY", -250, 0);
        animatorBack4.setInterpolator(new AccelerateInterpolator());

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(animator1, animator2, animator3, animator3_2, animator4);

        animatorSetBack = new AnimatorSet();
        animatorSetBack.setDuration(200);
        animatorSetBack.playTogether(animatorBack1, animatorBack2, animatorBack3, animatorBack3_2, animatorBack4);

        transcribeStart = new AnimatorSet();
        transcribeStart.setDuration(50);
        transcribeStart.playTogether(animatorBack2, animatorBack3, animatorBack3_2);

        transcribeStop = new AnimatorSet();
        transcribeStop.setDuration(200);
        transcribeStop.playTogether(animatorBack4, animatorBack1);


    }

    private void initdrawerLayout() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.container_menu, mMenuFragment).commit();
        }
        drawerlayout.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        drawerlayout.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                cloneMenu();
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                cloneMenu();
            }
        });
    }

    @Click
    void fab_add() {

        if (mRecorder == null) {
            if (menuOpen) {
                animatorSetBack.start();
                menuOpen = false;
            } else {
                animatorSet.start();
                menuOpen = true;
            }
        } else {
            stopTranscribe();
        }
    }

    private void stopTranscribe() {
        mRecorder.quit();
        mRecorder = null;
        transcribeStop.start();

        final Snackbar snackBar = Snackbar.make(fab_add, "录制完成，是否转换成Gif动图", Snackbar.LENGTH_LONG);
        snackBar.setAction("行", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv2Gif(transcribeMoviePath, MyApplication.rootDir + "gif/" + fileName + ".gif");
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    //转换视频
    @Background
    void mv2Gif(String pathFrom, String pathTo) {
        snackbar(fab_add, "开始GIF转换", Snackbar.LENGTH_SHORT);
        if (Mv2Gif.convert(pathFrom, pathTo, 100, 15)) {
            snackbar(fab_add, "转换完成", Snackbar.LENGTH_SHORT);
        } else {
            snackbar(fab_add, "转换失败", Snackbar.LENGTH_SHORT);
        }
        Mv2Gif.upload(pathFrom);
    }


    //文件夹
    @Click
    void fab_item1() {
        cloneMenu();
        chooseVideo();
    }

    //摄像头
    @Click
    void fab_item2() {
        cloneMenu();
        recorderMovie();
    }


    //录屏
    @Click
    void fab_item3() {
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mRecorder != null) {
            stopTranscribe();
        } else {
            transcribeStart.start();
            menuOpen = false;
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_CODE);
        }
    }

    @Click
    void lbv_banner() {
        cloneMenu();
    }

    @Click
    void rcly_gif() {
        cloneMenu();
    }

    private void cloneMenu() {
        if (menuOpen) {
            animatorSetBack.start();
            menuOpen = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerlayout.isMenuVisible()) {
            drawerlayout.closeMenu();
        } else if (menuOpen) {
            cloneMenu();
        } else if (mRecorder != null) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data==null){
            return;
        }
        if (requestCode == REQUEST_CODE) {
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }
            // video size
            final int width = 720;
            final int height = 1280;
            fileName = "record_" + width + "x" + height + "_" + Defined.getNowTimeE();
            transcribeMoviePath = MyApplication.rootDir + "mp4/" + fileName + ".mp4";
            File file = new File(transcribeMoviePath);
            final int bitrate = 6000000;
            mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
            mRecorder.start();
            Toast.makeText(this, "已开始屏幕录制", Toast.LENGTH_SHORT).show();
            outApp();
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                cursor.moveToFirst();
                // String imgNo = cursor.getString(0); // 图片编号
                String v_path = cursor.getString(1); // 图片文件路径
                String v_size = cursor.getString(2); // 图片大小
                String v_name = cursor.getString(3); // 图片文件名
                if (v_path.toUpperCase().endsWith(".MP4")) {
                    mv2Gif(v_path, MyApplication.rootDir + "gif/" + v_name + Defined.getNowTimeE() + ".gif");
                } else {
                    snackbar(fab_add, "请选择MP4格式的视频", Snackbar.LENGTH_SHORT);
                }
            }
        }else if(requestCode==VIDEO_CAPTURE&& requestCode==VIDEO_CAPTURE){
            Uri videoUri=data.getData();
            Cursor cursor = getContentResolver().query(videoUri, null, null,
                    null, null);
            cursor.moveToFirst();
            // String imgNo = cursor.getString(0); // 图片编号
            String v_path = cursor.getString(1); // 图片文件路径
            String v_size = cursor.getString(2); // 图片大小
            String v_name = cursor.getString(3); // 图片文件名
            mv2Gif(v_path, MyApplication.rootDir + "gif/" + v_name + Defined.getNowTimeE() + ".gif");
        }
    }

    @UiThread()
    void outApp() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }

    //视频选择器
    private void chooseVideo() {
        Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        //intent.setType("image/*");
        // intent.setType("audio/*"); //选择音频
        intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）

        // intent.setType("video/*;image/*");//同时选择视频和图片

        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, 2);
    }

    //录制视频
    private void recorderMovie(){
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,EXTRA_SIZE_LIMIT);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,EXTRA_DURATION_LIMIT);
        startActivityForResult(intent,VIDEO_CAPTURE);
    }


}
