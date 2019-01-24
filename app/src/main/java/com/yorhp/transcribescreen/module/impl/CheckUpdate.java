package com.yorhp.transcribescreen.module.impl;

import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.module.App;
import com.yorhp.transcribescreen.presenter.AppVersionListener;
import com.yorhp.transcribescreen.retrofite.api.AppVersionApi;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yorhp.transcribescreen.app.MyApplication.log;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class CheckUpdate {
    AppVersionListener listener;

    @Inject
    public CheckUpdate(AppVersionListener listener) {
        this.listener = listener;
    }

    public void checkUpdate(final String version) {
        AppVersionApi api = MyApplication.getRetrofit().create(AppVersionApi.class);
        api.checkUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(String s) {
                        log("CheckUpdate", s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("code") == 200) {
                                if (jsonObject.getString("version").equals(version)) {
                                    String apkUrl = jsonObject.getString("apkUrl");
                                    MyApplication.setting.setApkUrl(apkUrl);
                                    listener.lastVersion();
                                } else {
                                    boolean must = jsonObject.getBoolean("must");
                                    String version = jsonObject.getString("version");
                                    String apkUrl = jsonObject.getString("apkUrl");
                                    String info = jsonObject.getString("info");
                                    String imageUrl = jsonObject.getString("imageUrl");
                                    App app = new App(must, version, apkUrl, info, imageUrl);
                                    MyApplication.setting.setApkUrl(apkUrl);
                                    listener.hasNewVersion(app);
                                }
                            } else {
                                listener.checkVersionFail("网络出错");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.checkVersionFail("网络出错");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
