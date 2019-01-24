package com.yorhp.transcribescreen.retrofite.api;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Tyhj on 2017/10/26.
 */

public interface UserInfoApi {
    @POST("userInfo")
    Observable<String> pullUserInfo(@Query("userName") String userName,
                                    @Query("appVersion") String appVersion,
                                    @Query("model") String model,
                                    @Query("androidVersion") String androidVersion,
                                    @Query("position") String position,
                                    @Query("imei") String imei);
}
