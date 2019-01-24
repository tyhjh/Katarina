package com.yorhp.transcribescreen.presenter.impl;

import com.yorhp.transcribescreen.module.UserInfo;
import com.yorhp.transcribescreen.module.impl.PushUserInfo;

import javax.inject.Inject;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class PushUserInfoPresenter {
    PushUserInfo pushUserInfo;

    @Inject
    public PushUserInfoPresenter(PushUserInfo pushUserInfo) {
        this.pushUserInfo = pushUserInfo;
    }

    public void push(UserInfo userInfo) {
        if (userInfo != null)
            pushUserInfo.pushUserInfo(userInfo.getUserName(), userInfo.getAppVersion(), userInfo.getModel(), userInfo.getAndroidVersion(), userInfo.getPosition(), userInfo.getImei());
    }

}
