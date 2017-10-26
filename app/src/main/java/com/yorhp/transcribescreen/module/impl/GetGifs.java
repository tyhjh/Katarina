package com.yorhp.transcribescreen.module.impl;

import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.module.Gif;
import com.yorhp.transcribescreen.presenter.GifListener;
import com.yorhp.transcribescreen.retrofite.api.GifsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yorhp.transcribescreen.app.MyApplication.log;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class GetGifs {
    private GifListener listener;

    @Inject
    public GetGifs(GifListener listener) {
        this.listener = listener;
    }

    public void getGifs() {
        GifsApi api = MyApplication.getRetrofit().create(GifsApi.class);
        api.update()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        log("GetGifs",s);
                        ArrayList<Gif> gifs = new ArrayList<Gif>();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("code") == 200) {
                                JSONArray array = jsonObject.getJSONArray("gifs");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject json = array.getJSONObject(i);
                                    Gif gif = new Gif(json.getInt("id"), json.getString("imageUrl"), json.getString("userName"), json.getString("imei"));
                                    gifs.add(gif);
                                }
                                listener.getGifOk(gifs);
                            } else {
                                listener.getGifFail("网络出错");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.getGifFail("网络出错");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
