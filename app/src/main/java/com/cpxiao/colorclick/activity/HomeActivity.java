package com.cpxiao.colorclick.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpxiao.colorclick.KeyExtra;
import com.cpxiao.colorclick.R;
import com.cpxiao.minigamelib.activity.BaseActivity;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initWidget();
        initSmallAds("167302960362723_167303233696029");
    }

    private void initWidget() {

        Button classicBtn = (Button) findViewById(R.id.btn_classic);
        Button extraBtn = (Button) findViewById(R.id.btn_extra);
        Button timeBtn = (Button) findViewById(R.id.btn_time);
        Button bestScoreBtn = (Button) findViewById(R.id.btn_best_score);
        Button settingsBtn = (Button) findViewById(R.id.btn_settings);
        Button quitBtn = (Button) findViewById(R.id.btn_quit);

        classicBtn.setOnClickListener(this);
        extraBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        bestScoreBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_classic) {
            GameActivity.comeToMe(this, KeyExtra.KEY_CLASSIC_BEST_SCORE);
        } else if (id == R.id.btn_extra) {
            GameActivity.comeToMe(this, KeyExtra.KEY_EXTRA_BEST_SCORE);
        } else if (id == R.id.btn_time) {
            GameActivity.comeToMe(this, KeyExtra.KEY_TIME_BEST_SCORE);
        } else if (id == R.id.btn_best_score) {
            BestScoreActivity.comeToMe(this);
        } else if (id == R.id.btn_settings) {

        } else if (id == R.id.btn_quit) {
            finish();
        }
    }
}
