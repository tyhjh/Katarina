package com.yorhp.transcribescreen.presenter;

public interface ShowDownloadFile {
    public void downloadStart(int progress);
    public void downloading(int progress);
    public void downFinish(String path);
    public void downCancel(int progress);
    public void downLoadErro(String path);
}