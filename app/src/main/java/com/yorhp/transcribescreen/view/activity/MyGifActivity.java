package com.yorhp.transcribescreen.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.BaseActivity;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.view.adapter.LocalGifAdapter;
import com.yorhp.transcribescreen.view.myView.SpaceItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;

@EActivity(R.layout.activity_my_gif)
public class MyGifActivity extends BaseActivity {

    boolean who;
    ArrayList<String> gifs = new ArrayList<>();
    File dirs;
    LocalGifAdapter adapter;

    @ViewById
    TextView tv_title;

    @ViewById
    RecyclerView rycl_gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        who = getIntent().getBooleanExtra("who", true);
    }

    @AfterViews
    void afterView() {
        if (who) {
            tv_title.setText("我的图片");
            dirs = new File(MyApplication.rootDir + "/gif");
        } else {
            tv_title.setText("我的下载");
            dirs = new File(MyApplication.rootDir + "/download");
        }
        File[] array = dirs.listFiles();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile() && (array[i].getName().endsWith(".gif") || array[i].getName().endsWith(".GIF"))) {
                gifs.add(array[i].getPath());
                //log(array[i].getName()+"："+array[i].getPath());
            }
        }
        adapter = new LocalGifAdapter(this, gifs);
        rycl_gif.setAdapter(adapter);
        rycl_gif.addItemDecoration(new SpaceItemDecoration(6));
        rycl_gif.setLayoutManager(new GridLayoutManager(this, 4));

    }

    @Click
    void iv_back() {
        finish();
    }


}
