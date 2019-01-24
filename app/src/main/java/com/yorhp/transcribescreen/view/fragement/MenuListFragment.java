
package com.yorhp.transcribescreen.view.fragement;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.view.activity.MyGifActivity_;
import com.yorhp.transcribescreen.view.activity.SetActivity_;
import com.yorhp.transcribescreen.view.myView.CircleTransformation;

/**
 * Created by mxn on 2016/12/13.
 * MenuListFragment
 */

public class MenuListFragment extends Fragment {

    private ImageView iv_head;
    private NavigationView vNavigation;
    private EditText tv_usr_name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        vNavigation = (NavigationView) view.findViewById(R.id.vNavigation);
        iv_head = (ImageView) vNavigation.getHeaderView(0).findViewById(R.id.userheadImage);
        tv_usr_name = (EditText) vNavigation.getHeaderView(0).findViewById(R.id.tv_usr_name);
        com.ant.liao.GifView gifView = (com.ant.liao.GifView) vNavigation.getHeaderView(0).findViewById(R.id.gif);
        gifView.setGifImage(R.mipmap.gif1);
        gifView.setGifImageType(com.ant.liao.GifView.GifImageType.COVER);
        gifView.setShowDimension(900, 820);

        tv_usr_name.setText(MyApplication.userInfo.getUserName());

        Picasso.with(getActivity())
                .load(R.mipmap.defult)
                .transform(new CircleTransformation())
                .into(iv_head);
        navigtion();
        tv_usr_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyApplication.setting.setUserName(tv_usr_name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    //侧栏菜单
    public void navigtion() {
        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_feed:
                        intent = new Intent(getActivity(), MyGifActivity_.class);
                        intent.putExtra("who", true);
                        getActivity().startActivity(intent);
                        break;
                    case R.id.menu_direct:
                        intent = new Intent(getActivity(), MyGifActivity_.class);
                        intent.putExtra("who", false);
                        getActivity().startActivity(intent);
                        break;
                    case R.id.menu_news:
                        Toast.makeText(getContext(), "暂无通知", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_group_2:
                        break;
                    case R.id.menu_settings:
                        startActivity(new Intent(getActivity(), SetActivity_.class));
                        break;
                    //分享
                    case R.id.menu_share:
                        ClipboardManager clipboardmanager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardmanager.setText(MyApplication.setting.getApkUrl());
                        Toast.makeText(getContext(), "已将分享链接复制到粘贴板", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_about:
                        Toast.makeText(getContext(), "开发者：Tyhj", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_twocode:

                        break;
                }
                return false;
            }
        });
    }

}
