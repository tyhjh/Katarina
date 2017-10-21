package com.yorhp.transcribescreen.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import static android.support.design.widget.Snackbar.make;

@EActivity
public class BaseActivity extends AppCompatActivity {

    private Toast mToast;
    private int what = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @UiThread
    public void toast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }


    @UiThread
    public void snackbar(View view, String msg, int time) {
        make(view, msg, time).show();
    }

    public void log(String msg){
        Log.e(getClass().getName(),msg);
    }

    protected void backToResult(String key[],String value[]){
        Intent intent = new Intent();
        //把返回数据存入Intent
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i],value[i]);
        }
        setResult(RESULT_OK, intent);
        finish();
    }


}
