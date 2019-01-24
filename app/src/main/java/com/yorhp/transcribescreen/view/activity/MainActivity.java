package com.yorhp.transcribescreen.view.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingMenuLayout;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.BaseActivity;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.dagger.component.DaggerMainComponent;
import com.yorhp.transcribescreen.dagger.module.GetGifsModule;
import com.yorhp.transcribescreen.dagger.module.VersionModule;
import com.yorhp.transcribescreen.module.App;
import com.yorhp.transcribescreen.module.Gif;
import com.yorhp.transcribescreen.presenter.AppVersionListener;
import com.yorhp.transcribescreen.presenter.GifListener;
import com.yorhp.transcribescreen.presenter.ShowDownloadFile;
import com.yorhp.transcribescreen.presenter.impl.CheckVerionPresenter;
import com.yorhp.transcribescreen.presenter.impl.DownloadPresenter;
import com.yorhp.transcribescreen.presenter.impl.GetGifPresenter;
import com.yorhp.transcribescreen.presenter.impl.PushUserInfoPresenter;
import com.yorhp.transcribescreen.service.Mv2Gif;
import com.yorhp.transcribescreen.service.MvHandle;
import com.yorhp.transcribescreen.utils.AppUtil;
import com.yorhp.transcribescreen.utils.CommonUtil;
import com.yorhp.transcribescreen.utils.Defined;
import com.yorhp.transcribescreen.utils.GetImagePath;
import com.yorhp.transcribescreen.utils.MLiteOrm;
import com.yorhp.transcribescreen.utils.ScreenRecorder;
import com.yorhp.transcribescreen.view.adapter.GifAdapter;
import com.yorhp.transcribescreen.view.fragement.MenuListFragment;
import com.yorhp.transcribescreen.view.myView.GlideRecycleView;
import com.yorhp.transcribescreen.view.myView.LoopBannerView;
import com.yorhp.transcribescreen.view.myView.MyDialog;
import com.yorhp.transcribescreen.view.myView.loadingview.LoadingDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import static android.provider.MediaStore.EXTRA_DURATION_LIMIT;
import static com.yorhp.transcribescreen.app.MyApplication.isFirstLog;
import static com.yorhp.transcribescreen.app.MyApplication.setting;


@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements GifListener, AppVersionListener, ShowDownloadFile {

    private static final int CAMERA_REQUEST_CODE = 4;
    private String transcribeMoviePath;
    private String fileName;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_CHOOSE_MVTOGIF = 2;
    private static final int VIDEO_CAPTURE = 3;
    private static final int REQUEST_CODE_CHOOSE_MVTOCROP = 5;
    private static final long EXTRA_SIZE_LIMIT = 10485760L * 500;
    private AnimatorSet animatorSet, animatorSetBack, transcribeStart, transcribeStop;
    //比特率
    private int screenRecordBitrate = 32 * 1024 * 1024;
    ;
    private String mp4path;

    MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private boolean menuOpen;
    private LoadingDialog dialog;

    TextView tv_progress;
    AppCompatCheckBox checkBox;
    boolean canDownload = true;
    int maxProgress = 100;

    @Inject
    GetGifPresenter gifPresenter;

    @Inject
    CheckVerionPresenter checkVerionPresenter;

    @Inject
    DownloadPresenter downloadPresenter;

    @Inject
    PushUserInfoPresenter pushUserInfoPresenter;


    GifAdapter adapter;

    ArrayList<Gif> gifs = new ArrayList<>();

    @ViewById
    FlowingDrawer drawerlayout;

    @ViewById
    GlideRecycleView rcly_gif;

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
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidthDip = dm.widthPixels;// 屏幕宽（dip，如：320dip）
        Defined.scrWidth = screenWidthDip;
        DaggerMainComponent.builder()
                .getGifsModule(new GetGifsModule(this))
                .versionModule(new VersionModule(this, this, "", MyApplication.rootDir + "/katarina.apk"))
                .build()
                .inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @AfterViews
    void afterView() {
        pushUserInfoPresenter.push(MyApplication.userInfo);
        checkVerionPresenter.checkUpdate(CommonUtil.getAppVersion(this));
        gifPresenter.getGetGifs();
        initView();
        initdrawerLayout();
        initAnimator();
        initRclyView();
        if (isFirstLog == true) {
            startActivity(new Intent(this, SetActivity_.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        dialog = new LoadingDialog.Builder(this)
                .loadText("GIF转换中")
                .build();

        dialog.setCancelable(true);

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
        gifs.add(new Gif(0, "http://ac-fgtnb2h8.clouddn.com/673a3e8635731bc31f24.gif", "tyhj", null));
        gifs.add(new Gif(0, "http://ac-fgtnb2h8.clouddn.com/1e073e3d618211af98ad.gif", "tyhj", null));
        gifs.add(new Gif(0, "http://ac-fgtnb2h8.clouddn.com/087818d1c6851befc7db.gif", "tyhj", null));
        gifs.add(new Gif(0, "http://ac-fgtnb2h8.clouddn.com/e7e75ca5c83c50143005.gif", "tyhj", null));
        gifs.add(new Gif(0, "http://ac-fgtnb2h8.clouddn.com/88da253584ed0e6aed15.gif", "tyhj", null));
        adapter = new GifAdapter(this, gifs);
        rcly_gif.setAdapter(adapter);
        final StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rcly_gif.setLayoutManager(mStaggeredLayoutManager);
        rcly_gif.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mStaggeredLayoutManager.invalidateSpanAssignments();
                cloneMenu();
            }
        });
    }

    private void initAnimator() {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(fab_add, "rotation", 135);
        animator1.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorBack1 = ObjectAnimator.ofFloat(fab_add,
                "rotation", 135, 0);
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

    @LongClick(R.id.fab_add)
    void add() {

        /*if (MyApplication.setting.getUserName().equals("Tyhjh")) {
            final AlertDialog.Builder di = new AlertDialog.Builder(this);
            di.setCancelable(true);
            LayoutInflater inflater = LayoutInflater.from(this);
            View layout = inflater.inflate(R.layout.dialog_put_gif, null);
            di.setView(layout);
            final Dialog dialog = di.show();
            final EditText edt_url = (EditText) layout.findViewById(R.id.edt_url);
            Button btn_ok = (Button) layout.findViewById(R.id.btn_ok);
            final EditText edt_name = (EditText) layout.findViewById(R.id.edt_name);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = edt_url.getText().toString();
                    String name = edt_name.getText().toString();
                    if (!url.equals("") && !url.equals("")) {
                        new PutGif().putGif(name, url, CommonUtil.getDeviceIMEI(MainActivity.this));
                    }
                    dialog.dismiss();
                }
            });
        }*/

        chooseVideo(REQUEST_CODE_CHOOSE_MVTOCROP);

    }


    @Background
    void cropMv(String pathForm, String pathTo, Point startPoint, int width, int height) {
        if (MvHandle.cropMvSize(pathForm, pathTo, startPoint, width, height)) {
            snackbar(fab_add, "视频剪裁完成", Snackbar.LENGTH_SHORT);
        } else {
            snackbar(fab_add, "视频剪裁失败", Snackbar.LENGTH_SHORT);
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
        statrAnimator();
        snackbar(fab_add, "转换进行中", Snackbar.LENGTH_INDEFINITE);
        if (Mv2Gif.convert(pathFrom, pathTo, CommonUtil.getDeviceIMEI(this))) {
            snackbar(fab_add, "转换完成", Snackbar.LENGTH_SHORT);
            stopAnimator();
        } else {
            snackbar(fab_add, "转换失败", Snackbar.LENGTH_SHORT);
        }
    }

    //文件夹
    @Click
    void fab_item1() {
        cloneMenu();
        chooseVideo(REQUEST_CODE_CHOOSE_MVTOGIF);
    }

    //摄像头
    @Click
    void fab_item2() {
        cloneMenu();
        recorderMovie();
    }

    //录屏
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }
            // video size
            fileName = "record_" + "_" + Defined.getNowTimeE();
            transcribeMoviePath = MyApplication.rootDir + "mp4/" + fileName + ".mp4";
            File file = new File(transcribeMoviePath);
            mRecorder = new ScreenRecorder(setting.getUseRecordWidth(), setting.getUseRecordHeight(), screenRecordBitrate, 1, mediaProjection, file.getAbsolutePath());
            //log(setting.getUseRecordWidth()+"x"+setting.getUseRecordHeight()+":"+setting.getScreenDirection());
            mRecorder.start();
            Toast.makeText(this, "已开始屏幕录制", Toast.LENGTH_SHORT).show();
            outApp();
        } else if (requestCode == REQUEST_CODE_CHOOSE_MVTOGIF) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String v_path = GetImagePath.getPath(MainActivity.this, uri);
                log(v_path + "");
                if (v_path.toUpperCase().endsWith(".MP4")) {
                    mv2Gif(v_path, MyApplication.rootDir + "gif/" + "G_" + Defined.getNowTimeE() + ".gif");
                } else if (MyApplication.setting.getUserName().equals("Tyhjh")) {
                    Mv2Gif.upload(v_path, CommonUtil.getDeviceIMEI(MainActivity.this));
                } else {
                    snackbar(fab_add, "请选择MP4格式的视频", Snackbar.LENGTH_SHORT);
                    log(v_path);
                }
            }
        } else if (requestCode == VIDEO_CAPTURE && requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                File file = new File(mp4path);
                mv2Gif(file.getPath(), MyApplication.rootDir + "gif/" + "mv_" + Defined.getNowTimeE() + ".gif");
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_MVTOCROP) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String v_path = GetImagePath.getPath(MainActivity.this, uri);
                if (v_path.toUpperCase().endsWith(".MP4")) {
                    fileName = "record_" + "_" + Defined.getNowTimeE();
                    transcribeMoviePath = MyApplication.rootDir + "mp4/" + fileName + ".mp4";
                    cropMv(v_path, transcribeMoviePath, new Point(0, 660), 1440, 1655);
                } else {
                    snackbar(fab_add, "请选择MP4格式的视频", Snackbar.LENGTH_SHORT);
                }


            }
        }
    }

    @UiThread()
    void outApp() {
        moveTaskToBack(true);
    }

    @UiThread
    void statrAnimator() {
        dialog.show();
        fab_add.setEnabled(false);
    }

    @UiThread
    void stopAnimator() {
        dialog.dismiss();
        fab_add.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
        MLiteOrm.getInstance().save(MyApplication.setting);
    }

    //视频选择器
    private void chooseVideo(int requestCode) {
        Intent intent = new Intent();

        /* 开启Pictures画面Type设定为image */
        //intent.setType("image/*");
        // intent.setType("audio/*"); //选择音频
        intent.setType("video/*;image/*"); //选择视频 （mp4 3gp 是android支持的视频格式）

        // intent.setType("video/*;image/*");//同时选择视频和图片

        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, requestCode);
    }

    //录制视频
    private void recorderMovie() {


        final String permission = Manifest.permission.CAMERA;  //相机权限
        final String permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE; //写入数据权限
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission1) != PackageManager.PERMISSION_GRANTED) {  //先判断是否被赋予权限，没有则申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {  //给出权限申请说明
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            } else { //直接申请权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE); //申请权限，可同时申请多个权限，并根据用户是否赋予权限进行判断
            }
        } else {  //赋予过权限，则直接调用相机拍照
            recordMv();
        }

    }

    private void recordMv() {
        mp4path = MyApplication.rootDir + "mp4/" + "mv_" + Defined.getNowTimeE() + ".mp4";
        Uri imageUri;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.yorhp.transcribescreen.fileProvider", new File(mp4path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(new File(mp4path));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, EXTRA_SIZE_LIMIT);
        intent.putExtra(EXTRA_DURATION_LIMIT, 1000);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {  //申请权限的返回值
            case CAMERA_REQUEST_CODE:
                int length = grantResults.length;
                final boolean isGranted = length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[length - 1];
                if (isGranted) {  //如果用户赋予权限，则调用相机
                    recordMv();
                } else { //未赋予权限，则做出对应提示

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void gifLoading() {

    }

    @Override
    public void getGifOk(ArrayList<Gif> gif) {
        gifs.addAll(gif);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getGifFail(String msg) {
        //toast(msg);
    }

    @Override
    public void hasNewVersion(App app) {
        update(app, downloadPresenter, checkBox, tv_progress);
    }

    private void update(App app, DownloadPresenter presenter, AppCompatCheckBox checkBox, TextView tv_progress) {
        presenter.setDownLoadUrl(app.getApkUrl());
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_update, null);
        final MyDialog dialog = new MyDialog(this, layout, R.style.dialog);
        dialog.setCancelable(app.isMust());
        TextView tv_version = (TextView) layout.findViewById(R.id.tv_version);
        TextView tv_why = (TextView) layout.findViewById(R.id.tv_why);
        tv_version.setText(app.getVersion());
        tv_why.setText(app.getInfo());
        checkBox = (AppCompatCheckBox) layout.findViewById(R.id.ck_download);
        tv_progress = (TextView) layout.findViewById(R.id.tv_progress);
        tv_progress.setText(0 + "%");
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && canDownload) {
                    downloadPresenter.downLoadToshow();
                    canDownload = false;
                }
            }
        });
        dialog.show();
    }

    @Override
    public void checkVersionFail(String msg) {
        //toast(msg);
    }

    @Override
    public void lastVersion() {

    }

    @Override
    public void downloadStart(int progress) {
        maxProgress = progress;
    }

    @Override
    public void downloading(int progress) {
        tv_progress.setText(100 * progress / maxProgress + "%");
    }

    @Override
    public void downFinish(String path) {
        tv_progress.setText(100 + "%");
        AppUtil.installApk(path, this);
    }

    @Override
    public void downCancel(int progress) {

    }

    @Override
    public void downLoadErro(String path) {
        checkBox.setChecked(false);
        canDownload = true;
        File file = new File(path);
        if (file.exists())
            file.delete();
        //toast("下载失败");
    }

}
