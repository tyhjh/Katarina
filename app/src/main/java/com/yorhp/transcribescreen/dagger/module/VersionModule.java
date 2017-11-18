package com.yorhp.transcribescreen.dagger.module;

import com.yorhp.transcribescreen.module.DownloadFile;
import com.yorhp.transcribescreen.presenter.AppVersionListener;
import com.yorhp.transcribescreen.presenter.ShowDownloadFile;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tyhj on 2017/10/26.
 */

@Module
public class VersionModule {
    AppVersionListener listener;
    ShowDownloadFile downloadListener;
    DownloadFile file;

    public VersionModule(AppVersionListener appVersionListener,ShowDownloadFile downloadListener,String url,String path) {
        this.listener = appVersionListener;
        this.downloadListener = downloadListener;
        file=new DownloadFile(url,path);
    }

    @Provides
    AppVersionListener appVersionListenerProvider() {
        return listener;
    }

    @Provides
    ShowDownloadFile showDownloadFileProvider(){
        return downloadListener;
    }


    @Provides
    DownloadFile providerFile(){
        return file;
    }


}
