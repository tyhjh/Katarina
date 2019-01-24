package com.yorhp.transcribescreen.module.impl;

import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.retrofite.api.PutGifApi;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Tyhj on 2017/10/29.
 */

public class PutGif {
    public void putGif(String userName, String gifUrl, String imei) {
        if (imei.equals("869011025169639")) {
            PutGifApi api = MyApplication.getRetrofit().create(PutGifApi.class);
            api.putGif(userName,imei,gifUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {

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
}
