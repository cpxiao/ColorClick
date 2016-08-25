package com.cpxiao.colorclick.activity;

import android.os.Bundle;

import com.cpxiao.colorclick.R;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        GameActivity.comeToMe(this);
    }
}
