package com.yorhp.transcribescreen.utils;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.yorhp.transcribescreen.app.MyApplication;

/**
 * Created by Tyhj on 2017/6/19.
 */

public class MLiteOrm {
    private volatile static LiteOrm instance=null;

    public static LiteOrm getInstance(){
        if(instance==null){
            synchronized (MLiteOrm.class){
                if(instance==null){
                   DataBaseConfig config = new DataBaseConfig(MyApplication.getAppContexts(), "translasion.db");
                    //"liteorm.db"是数据库名称，名称里包含路径符号"/"则将数据库建立到该路径下，可以使用sd卡路径。 不包含则在系统默认路径下创建DB文件。
                    //例如 public static final String DB_NAME = SD_CARD + "/lite/orm/liteorm.db";     DataBaseConfig config = new DataBaseConfig(this, DB_NAME);
                    config.dbVersion = 1; // set database version
                    config.onUpdateListener = null; // set database update listener
                    //独立操作，适用于没有级联关系的单表操作，
                    instance = LiteOrm.newSingleInstance(config);
                    //级联操作,适用于多表级联操作
                    // liteOrm=LiteOrm.newCascadeInstance(config);
                    instance.setDebugged(true);
                }
            }
        }
        return instance;
    }

}
