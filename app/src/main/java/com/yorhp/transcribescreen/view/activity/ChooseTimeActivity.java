package com.yorhp.transcribescreen.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.BaseActivity;
import com.yorhp.transcribescreen.view.adapter.TimeAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_choose_time)
public class ChooseTimeActivity extends BaseActivity implements TimeAdapter.OnItemClickListener {

    TimeAdapter adapter;
    ArrayList<Integer> timeList = new ArrayList<>();
    int request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time);
        request = getIntent().getIntExtra("getTime", 1);
    }

    @ViewById
    RecyclerView rcly_time;

    @ViewById
    TextView tv_title;

    @AfterViews
    void afterView() {
        switch (request) {
            case 1:
                tv_title.setText("帧率选择");
                timeList.clear();
                for (int i = 1; i <= 30; i++) {
                    timeList.add(i);
                }
                break;
            case 2:
                tv_title.setText("时间选择");
                timeList.clear();
                for (int i = 0; i <= 100; i++) {
                    timeList.add(i);
                }
                break;
            case 3:
                tv_title.setText("时间选择");
                timeList.clear();
                for (int i = 1; i <= 100; i++) {
                    timeList.add(i);
                }
                for(int k=0;k<100;k++){
                    timeList.add(100+k*10);
                }
                timeList.add(100000);
                break;
            default:break;
        }
        adapter = new TimeAdapter(this, timeList, this);
        rcly_time.setAdapter(adapter);
        rcly_time.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    @Click
    void iv_back() {
        finish();
    }

    @Override
    public void onItemClick(int time) {
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("time", time);
        //设置返回数据
        setResult(RESULT_OK, intent);//RESULT_OK为自定义常量
        //关闭Activity
        finish();
    }
}
