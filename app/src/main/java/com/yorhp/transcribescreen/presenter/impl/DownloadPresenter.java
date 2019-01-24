package com.yorhp.transcribescreen.presenter.impl;

import com.yorhp.transcribescreen.module.impl.DownLoadImpl;

import javax.inject.Inject;

/**
 * Created by Tyhj on 2017/5/26.
 */

public class DownloadPresenter {

    DownLoadImpl downLoadPaper;

    @Inject
    public DownloadPresenter(DownLoadImpl downLoadPaper) {
        this.downLoadPaper=downLoadPaper;
    }

    public void downLoadToshow(){
        downLoadPaper.onDownload();
    }

    public void setDownLoadUrl(String url){
        downLoadPaper.setPaper(url);
    }

}
