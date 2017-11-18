package com.yorhp.transcribescreen.retrofite.api;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Tyhj on 2017/10/29.
 */

public interface PutGifApi {
    @POST("putGif")
    Observable<String> putGif(@Query("userName") String userName,@Query("imei") String imei,@Query("gifUrl") String gifUrl);
}
