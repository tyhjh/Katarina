package com.yorhp.transcribescreen.presenter.impl;

import com.yorhp.transcribescreen.module.impl.CheckUpdate;

import javax.inject.Inject;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class CheckVerionPresenter {
    CheckUpdate checkUpdate;

    @Inject
    public CheckVerionPresenter(CheckUpdate checkUpdate) {
        this.checkUpdate = checkUpdate;
    }

    public void checkUpdate(String version) {
        checkUpdate.checkUpdate(version);
    }

}
