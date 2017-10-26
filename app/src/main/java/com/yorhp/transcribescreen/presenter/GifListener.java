package com.yorhp.transcribescreen.presenter;

import com.yorhp.transcribescreen.module.Gif;

import java.util.ArrayList;

/**
 * Created by Tyhj on 2017/10/26.
 */

public interface GifListener {

    void gifLoading();

    void getGifOk(ArrayList<Gif> gifs);

    void getGifFail(String msg);
}
