package com.yorhp.transcribescreen.module;

/**
 * Created by Tyhj on 2017/10/20.
 */

public class Gif {
    private int id;
    private String url;
    private String userName;
    private int userId;

    public Gif(int id, String url, String userName, int userId) {
        this.id = id;
        this.url = url;
        this.userName = userName;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
