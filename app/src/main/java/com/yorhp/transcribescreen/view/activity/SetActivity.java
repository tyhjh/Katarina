package com.yorhp.transcribescreen.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.BaseActivity;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.dagger.component.DaggerSetComponent;
import com.yorhp.transcribescreen.dagger.module.VersionModule;
import com.yorhp.transcribescreen.module.App;
import com.yorhp.transcribescreen.presenter.AppVersionListener;
import com.yorhp.transcribescreen.presenter.ShowDownloadFile;
import com.yorhp.transcribescreen.presenter.impl.CheckVerionPresenter;
import com.yorhp.transcribescreen.presenter.impl.DownloadPresenter;
import com.yorhp.transcribescreen.utils.AppUtil;
import com.yorhp.transcribescreen.utils.CommonUtil;
import com.yorhp.transcribescreen.utils.MLiteOrm;
import com.yorhp.transcribescreen.view.myView.MyDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import javax.inject.Inject;

import static com.yorhp.transcribescreen.app.MyApplication.isFirstLog;
import static com.yorhp.transcribescreen.app.MyApplication.setting;

@EActivity(R.layout.activity_set)
public class SetActivity extends BaseActivity implements AppVersionListener, ShowDownloadFile {

    @Inject
    DownloadPresenter downloadPresenter;

    @Inject
    CheckVerionPresenter checkVerionPresenter;

    TextView tv_progress;
    AppCompatCheckBox checkBox;
    boolean canDownload = true;
    int maxProgress = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSetComponent.builder()
                .versionModule(new VersionModule(this, this, "", MyApplication.rootDir + "/katarina.apk"))
                .build()
                .inject(this);
    }

    @ViewById
    TextView tv_title;

    @ViewById
    TextView tv_screen_record_resolution, tv_screen_record_way, tv_gif_resolution, tv_gif_skip, tv_gif_fps, tv_gif_time;

    @ViewById
    TextView tv_version;

    @ViewById
    Switch switch_isshare;


    @AfterViews
    void afterView() {
        tv_title.setText("设置");
        initView();
        if (isFirstLog == true) {
            toast("你可以在这里设置相关参数已达到你想要的效果");
        }
        isFirstLog = false;
    }

    private void initView() {
        tv_screen_record_resolution.setText(setting.getRecordWidth() + "x" + setting.getRecordHeight() + "P");
        if (setting.getScreenDirection())
            tv_screen_record_way.setText("横屏");
        else
            tv_screen_record_way.setText("竖屏");
        if (setting.getGifResolutionHeight() == 0)
            tv_gif_resolution.setText("原视频分辨率");
        else
            tv_gif_resolution.setText(setting.getGifResolutionWidth() + "x" + setting.getGifResolutionHeight() + "P");
        tv_gif_fps.setText(setting.getGifFrameRates() + "");
        tv_gif_skip.setText(setting.getSkip() + "s");
        tv_gif_time.setText(setting.getMp4Time() + "s");
        switch_isshare.setChecked(setting.getShare());
        tv_version.setText("Version " + CommonUtil.getAppVersion(this));
    }

    @Click
    void iv_back() {
        this.finish();
    }

    @Click
//录屏分辨率
    void ll_screen_record_resolution() {
        AlertDialog.Builder di = new AlertDialog.Builder(this);
        di.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_resolution_mv, null);
        di.setView(layout);
        final Dialog dialog = di.show();
        final Button[] buttons;
        Button bt1, bt2, bt3, bt4, bt5;
        bt1 = (Button) layout.findViewById(R.id.bt1);
        bt2 = (Button) layout.findViewById(R.id.bt2);
        bt3 = (Button) layout.findViewById(R.id.bt3);
        bt4 = (Button) layout.findViewById(R.id.bt4);
        bt5 = (Button) layout.findViewById(R.id.bt5);
        buttons = new Button[]{bt1, bt2, bt3, bt4, bt5};
        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int recordWidth = Integer.parseInt(buttons[finalI].getText().toString().split("x")[0]);
                    int recordHeight = Integer.parseInt(buttons[finalI].getText().toString().split("x")[1].split("P")[0]);
                    tv_screen_record_resolution.setText(recordWidth + "x" + recordHeight + "P");
                    setting.setRecordWidth(recordWidth);
                    setting.setRecordHeight(recordHeight);
                    dialog.dismiss();
                }
            });
        }
    }

    @Click
//屏幕方向
    void ll_screen_record_way() {
        setting.setScreenDirection(!setting.getScreenDirection());
        if (setting.getScreenDirection()) {
            tv_screen_record_way.setText("横屏");
        } else {
            tv_screen_record_way.setText("竖屏");
        }
    }

    @Click
//GIF分辨率
    void ll_gif_resolution() {
        AlertDialog.Builder di = new AlertDialog.Builder(this);
        di.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_resolution_gif, null);
        di.setView(layout);
        final Dialog dialog = di.show();
        final Button[] buttons;
        Button bt0, bt1, bt2, bt3, bt4, bt5;
        bt0 = (Button) layout.findViewById(R.id.bt0);
        bt1 = (Button) layout.findViewById(R.id.bt1);
        bt2 = (Button) layout.findViewById(R.id.bt2);
        bt3 = (Button) layout.findViewById(R.id.bt3);
        bt4 = (Button) layout.findViewById(R.id.bt4);
        bt5 = (Button) layout.findViewById(R.id.bt5);
        buttons = new Button[]{bt0, bt1, bt2, bt3, bt4, bt5};
        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = buttons[finalI].getText().toString();
                    if (msg.equals("原视频分辨率")) {
                        setting.setGifResolutionWidth(0);
                        setting.setGifResolutionHeight(0);
                        tv_gif_resolution.setText("原视频分辨率");
                    } else {
                        int gif_resolution_width = Integer.parseInt(msg.split("x")[0]);
                        int gif_resolution_height = Integer.parseInt(msg.split("x")[1].split("P")[0]);
                        setting.setGifResolutionWidth(gif_resolution_width);
                        setting.setGifResolutionHeight(gif_resolution_height);
                        tv_gif_resolution.setText(gif_resolution_width + "x" + gif_resolution_height + "P");
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    @Click
//跳过时间
    void ll_gif_skip() {
        Intent intent = new Intent(SetActivity.this, ChooseTimeActivity_.class);
        intent.putExtra("getTime", 2);
        startActivityForResult(intent, 2);
    }

    @Click
//更新
    void ll_version() {
        checkVerionPresenter.checkUpdate(CommonUtil.getAppVersion(this));
    }

    @Click
//GIF帧率
    void ll_gif_fps() {
        Intent intent = new Intent(SetActivity.this, ChooseTimeActivity_.class);
        intent.putExtra("getTime", 1);
        startActivityForResult(intent, 1);
    }

    @Click
//最大转换时间
    void ll_gif_time() {
        Intent intent = new Intent(SetActivity.this, ChooseTimeActivity_.class);
        intent.putExtra("getTime", 3);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 1:
                int gif_fps = data.getIntExtra("time", 15);
                tv_gif_fps.setText(gif_fps + "s");
                setting.setGifFrameRates(gif_fps);
                break;
            case 2:
                int gif_skip = data.getIntExtra("time", 0);
                tv_gif_skip.setText(gif_skip + "s");
                setting.setSkip(gif_skip);
                break;
            case 3:
                int mp4Time = data.getIntExtra("time", 100);
                tv_gif_time.setText(mp4Time + "s");
                setting.setMp4Time(mp4Time);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        setting.setShare(switch_isshare.isChecked());
        MLiteOrm.getInstance().update(setting);
        super.onDestroy();
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
        toast("下载失败");
    }

    @Override
    public void hasNewVersion(App app) {
        update(app, downloadPresenter, checkBox, tv_progress);
    }

    @Override
    public void checkVersionFail(String msg) {
        toast(getString(R.string.txt_erro));
    }

    @Override
    public void lastVersion() {
        toast("你的APP是最新版本");
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

}
