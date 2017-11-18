package com.yorhp.transcribescreen.module;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by Tyhj on 2017/10/24.
 */
@Table("setting")
public class Setting implements Serializable {

    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("id")
    private int id;

    @Default("true")
    @Column("screenDirection")
    private Boolean screenDirection;

    @Default("true")
    @Column("isShare")
    private Boolean isShare;

    @Default("720")
    @Column("recordWidth")
    private int recordWidth;

    @Default("1280")
    @Column("recordHeight")
    private int recordHeight;

    @Default("0")
    @Column("gifResolutionWidth")
    private int gifResolutionWidth;

    @Default("0")
    @Column("gifResolutionHeight")
    private int gifResolutionHeight;

    @Default("15")
    @Column("gifFrameRates")
    private int gifFrameRates;

    @Default("0")
    @Column("skip")
    private int skip;

    @Default("100")
    @Column("mp4Time")
    private int mp4Time;

    @Column("userName")
    private String userName;

    @Column("apkUrl")
    private String apkUrl;


    public int getUseRecordWidth() {
        if (screenDirection)
            return recordWidth;
        else
            return recordHeight;
    }

    public int getUseRecordHeight() {
        if (screenDirection)
            return recordHeight;
        else
            return recordWidth;
    }

    public String getGifResolution() {

        if (gifResolutionHeight == 0) {
            return "";
        }

        if (screenDirection) {
            return " -vf scale=" + gifResolutionWidth + ":-1";
        } else {
            return " -vf scale=" + gifResolutionHeight + ":-1";
        }
    }


    public Setting(Boolean screenDirection, Boolean isShare, int recordWidth, int recordHeight, int gifResolutionWidth, int gifResolutionHeight, int gifFrameRates, int skip, int mp4Time, String userName, String apkUrl) {
        this.screenDirection = screenDirection;
        this.isShare = isShare;
        this.recordWidth = recordWidth;
        this.recordHeight = recordHeight;
        this.gifResolutionWidth = gifResolutionWidth;
        this.gifResolutionHeight = gifResolutionHeight;
        this.gifFrameRates = gifFrameRates;
        this.skip = skip;
        this.mp4Time = mp4Time;
        this.userName = userName;
        this.apkUrl = apkUrl;
    }

    public Boolean getScreenDirection() {
        return screenDirection;
    }

    public void setScreenDirection(Boolean screenDirection) {
        this.screenDirection = screenDirection;
    }

    public Boolean getShare() {
        return isShare;
    }

    public void setShare(Boolean sharee) {
        isShare = sharee;
    }

    public int getRecordWidth() {
        return recordWidth;
    }

    public void setRecordWidth(int recordWidth) {
        this.recordWidth = recordWidth;
    }

    public int getRecordHeight() {
        return recordHeight;
    }

    public void setRecordHeight(int recordHeight) {
        this.recordHeight = recordHeight;
    }

    public int getGifResolutionWidth() {
        return gifResolutionWidth;
    }

    public void setGifResolutionWidth(int gifResolutionWidth) {
        this.gifResolutionWidth = gifResolutionWidth;
    }

    public int getGifResolutionHeight() {
        return gifResolutionHeight;
    }

    public void setGifResolutionHeight(int gifResolutionHeight) {
        this.gifResolutionHeight = gifResolutionHeight;
    }

    public int getGifFrameRates() {
        return gifFrameRates;
    }

    public void setGifFrameRates(int gifFrameRates) {
        this.gifFrameRates = gifFrameRates;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getMp4Time() {
        return mp4Time;
    }

    public void setMp4Time(int mp4Time) {
        this.mp4Time = mp4Time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
