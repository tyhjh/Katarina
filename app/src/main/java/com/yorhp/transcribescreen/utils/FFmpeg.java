package com.yorhp.transcribescreen.utils;

/**
 * Created by Tyhj on 2017/10/22.
 */

public class FFmpeg {

    private static FFmpeg sInstance ;

    public static FFmpeg getsInstance(){
        if(sInstance == null) {
            sInstance = new FFmpeg();
        }
        return sInstance;
    }

    public native int run(String[] commands);

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("swscale-4");
        System.loadLibrary("avformat-57");
    }
}
