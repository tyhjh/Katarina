package com.yorhp.transcribescreen.dagger.module;

import com.yorhp.transcribescreen.presenter.GifListener;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tyhj on 2017/10/26.
 */
@Module
public class GetGifsModule {
    GifListener listener;

    public GetGifsModule(GifListener listener) {
        this.listener = listener;
    }

    @Provides
    GifListener gifListenerPrivider(){
        return listener;
    }

}
