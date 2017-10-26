package com.yorhp.transcribescreen.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.litesuits.orm.log.OrmLog;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.module.Setting;
import com.yorhp.transcribescreen.module.UserInfo;
import com.yorhp.transcribescreen.utils.ChineseName;
import com.yorhp.transcribescreen.utils.CommonUtil;
import com.yorhp.transcribescreen.utils.MLiteOrm;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import static android.R.id.list;

@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterView() {
        List settings = MLiteOrm.getInstance().query(Setting.class);
        OrmLog.i("tag", list);
        if (settings.size() == 0) {
            MyApplication.isFirstLog = true;
            Setting setting = new Setting(true, true, 720, 1028, 0, 0, 15, 0, 100);
            MLiteOrm.getInstance().save(setting);
            MyApplication.setting = setting;
        } else {
            MyApplication.setting = (Setting) settings.get(0);
        }
        MyApplication.userInfo = new UserInfo(
                ChineseName.getName(),
                CommonUtil.getAppVersion(this),
                CommonUtil.getModel(),
                CommonUtil.getAndroidVersion(),
                "China",
                CommonUtil.getDeviceIMEI(this)
        );
        start();
    }

    @UiThread(delay = 2000)
    void start() {
        startActivity(new Intent(this, MainActivity_.class));
        finish();
    }

}
