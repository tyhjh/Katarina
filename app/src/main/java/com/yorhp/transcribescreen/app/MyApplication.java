package com.yorhp.transcribescreen.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.yorhp.transcribescreen.module.Setting;
import com.yorhp.transcribescreen.module.UserInfo;
import com.yorhp.transcribescreen.retrofite.convert.MyFactory;
import com.yorhp.transcribescreen.utils.InternetUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * Created by Tyhj on 2017/5/23.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    public static final boolean ISDEBUG = true;
    public static ArrayList<Activity> activities = new ArrayList<>();
    public static String rootDir;
    public static Setting setting;
    public static boolean isFirstLog;
    public static UserInfo userInfo;
    private static Retrofit retrofit;


    public void onCreate() {
        super.onCreate();
        instance = this;
        initPicasso();
        initRetrofite();
        initDir();
    }

    //Picasso初始化
    private void initPicasso() {
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache(10 << 20))//设置内存缓存大小10M
                //.indicatorsEnabled(false) //设置左上角标记，主要用于测试
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    //文件夹初始化
    public void initDir() {
        rootDir = Environment.getExternalStorageDirectory() + "/ATranscribeScreen/";
        File f1 = new File(rootDir);
        if (!f1.exists()) {
            f1.mkdirs();
        }
        File f2 = new File(rootDir+"gif/");
        if (!f2.exists()) {
            f2.mkdirs();
        }
        File f3 = new File(rootDir+"mp4/");
        if (!f3.exists()) {
            f3.mkdirs();
        }
    }

    //打印初始化
    public static void log(String key, String value) {
        if (ISDEBUG)
            Log.e(key, value + "");
    }

    public static Context getAppContexts() {
        return instance;
    }

    public static void closeAllActivity() {
        int leanth = activities.size();
        for (int i = 0; i < leanth; i++) {
            activities.get(i).finish();
        }
    }


    private void initRetrofite() {
        //cache url
        File httpCacheDirectory = new File(getCacheDir(), "responses");

        int cacheSize = 50 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(getInterceptor())
                .addNetworkInterceptor(getNetWorkInterceptor())
                .cache(cache).build();

        retrofit = new Retrofit
                .Builder()
                .baseUrl("http://192.168.31.170:8080/api/")
                .client(client)
                .addConverterFactory(MyFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 设置返回数据的  Interceptor  判断网络   没网读取缓存
     */
    public Interceptor getInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!InternetUtil.isOnline(getBaseContext())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }


    /**
     * 设置连接器  设置缓存
     */
    public Interceptor getNetWorkInterceptor (){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if (InternetUtil.isOnline(getBaseContext())) {
                    int maxAge = 0 * 60;
                    // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    // 无网络时，设置超时为1周
                    int maxStale = 60 * 60 * 24 * 7;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }


}


//
//                               _oo0oo_
//                             o8888888o
//                              88" . "88
//                               (|  -_-  |)
//                              0\  =  /0
//                            ___/`---'\___
//                          .' \\|          |// '.
//                        / \\|||     :    |||// \
//                      / _ |||||    -:-   ||||| - \
//                     |   | \\\     -    /// |   |
//                     | \_ |  ''\ --- /''  |_ / |
//                     \  .- \__  '-'  ___/-. /
//                     ___'. .'  /--.--\  `. .'___
//              ."" '<  `.___\_<|>_/___.' >' "".
//            | | :  `- \ `.;` \     _    / `;.` / - ` : | |
//            \  \   `_.   \_  __\ /__  _/   .-`  /  /
// =====`-.____`.___ \_____/___.-`___.-'=====
//                             `=---='
//
//
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
//
//
//
