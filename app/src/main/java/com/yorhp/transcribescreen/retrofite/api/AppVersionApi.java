package com.yorhp.transcribescreen.retrofite.api;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Tyhj on 2017/10/26.
 */

public interface AppVersionApi {
    @GET("appVersion")
    Observable<String> checkUpdate();
}
