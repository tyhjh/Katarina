package com.yorhp.transcribescreen.module;

/**
 * Created by Tyhj on 2017/10/26.
 */

public class App {
    boolean must;
    String version;
    String apkUrl;
    String info;
    String imageUrl;

    public App(boolean must, String version, String apkUrl, String info, String imageUrl) {
        this.must = must;
        this.version = version;
        this.apkUrl = apkUrl;
        this.info = info;
        this.imageUrl = imageUrl;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
