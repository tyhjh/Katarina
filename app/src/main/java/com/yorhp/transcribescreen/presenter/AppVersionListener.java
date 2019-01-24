package com.yorhp.transcribescreen.presenter;

import com.yorhp.transcribescreen.module.App;

/**
 * Created by Tyhj on 2017/10/26.
 */

public interface AppVersionListener {
    void hasNewVersion(App app);
    void checkVersionFail(String msg);
    void lastVersion();
}
