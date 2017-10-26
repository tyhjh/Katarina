package com.yorhp.transcribescreen.dagger.module;

import com.yorhp.transcribescreen.presenter.AppVersionListener;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tyhj on 2017/10/26.
 */

@Module
public class VersionModule {
    AppVersionListener listener;

    public VersionModule(AppVersionListener listener) {
        this.listener = listener;
    }

    @Provides
    AppVersionListener appVersionListenerProvider(){
        return listener;
    }

}
