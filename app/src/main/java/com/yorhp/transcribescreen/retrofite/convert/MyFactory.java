package com.yorhp.transcribescreen.retrofite.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Tyhj on 2017/6/12.
 */

public class MyFactory extends Converter.Factory {
    public static final MyFactory INSTANCE = new MyFactory();

    public static MyFactory create() {
        return INSTANCE;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return StringConvert.INSTANCE;
    }
}
