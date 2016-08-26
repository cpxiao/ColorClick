package com.cpxiao.colorclick.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cpxiao.colorclick.KeyExtra;
import com.cpxiao.colorclick.R;
import com.cpxiao.commonlibrary.utils.PreferencesUtils;
import com.cpxiao.minigamelib.activity.BaseActivity;

/**
 * Created by cpxiao on 8/26/16.
 * BestScoreActivity
 */
public class BestScoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);
        initWidget();

        initBigAds("167302960362723_167359617023724");

    }

    private void initWidget() {
        int classicBestScore = PreferencesUtils.getInt(this, KeyExtra.KEY_CLASSIC_BEST_SCORE, 0);
        int extraBestScore = PreferencesUtils.getInt(this, KeyExtra.KEY_EXTRA_BEST_SCORE, 0);
        int timeBestScore = PreferencesUtils.getInt(this, KeyExtra.KEY_EXTRA_BEST_SCORE, 0);

        TextView classicBestScoreView = (TextView) findViewById(R.id.classic_best_score);
        TextView extraBestScoreView = (TextView) findViewById(R.id.extra_best_score);
        TextView timeBestScoreView = (TextView) findViewById(R.id.time_best_score);

        classicBestScoreView.setText(String.valueOf(classicBestScore));
        extraBestScoreView.setText(String.valueOf(extraBestScore));
        timeBestScoreView.setText(String.valueOf(timeBestScore));

        Button okBtn = (Button) findViewById(R.id.ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void comeToMe(Context context) {
        Intent intent = new Intent(context, BestScoreActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
