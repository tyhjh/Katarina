package com.yorhp.transcribescreen.presenter.impl;

import com.yorhp.transcribescreen.module.impl.GetGifs;

import javax.inject.Inject;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class GetGifPresenter {
    GetGifs getGifs;

    @Inject
    public GetGifPresenter(GetGifs getGifs) {
        this.getGifs = getGifs;
    }

    public void getGetGifs() {
        getGifs.getGifs();
    }

}
