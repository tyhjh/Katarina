package com.yorhp.transcribescreen.dagger.component;

import com.yorhp.transcribescreen.dagger.module.VersionModule;
import com.yorhp.transcribescreen.view.activity.SetActivity;

import dagger.Component;

/**
 * Created by Tyhj on 2017/10/28.
 */
@Component(modules = VersionModule.class)
public interface SetComponent {
    void inject(SetActivity activity);
}
