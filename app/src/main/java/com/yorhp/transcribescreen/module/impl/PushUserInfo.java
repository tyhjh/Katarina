package com.yorhp.transcribescreen.module.impl;

import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.retrofite.api.UserInfoApi;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yorhp.transcribescreen.app.MyApplication.log;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class PushUserInfo {

    @Inject
    public PushUserInfo() {
    }

    public void pushUserInfo(String userName, String appVersion, String model,
                             String androidVersion, String position, String imei) {
        UserInfoApi userInfo = MyApplication.getRetrofit().create(UserInfoApi.class);
        userInfo.pullUserInfo(userName, appVersion, model, androidVersion, position, imei)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        log("pushUserInfo",s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
