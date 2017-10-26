package com.yorhp.transcribescreen.dagger.component;

import com.yorhp.transcribescreen.dagger.module.GetGifsModule;
import com.yorhp.transcribescreen.dagger.module.VersionModule;
import com.yorhp.transcribescreen.view.activity.MainActivity;

import dagger.Component;

/**
 * Created by Tyhj on 2017/9/5.
 */
@Component(modules = {GetGifsModule.class, VersionModule.class})
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
