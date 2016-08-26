package com.cpxiao.colorclick.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.colorclick.ColorClickView;
import com.cpxiao.colorclick.KeyExtra;
import com.cpxiao.colorclick.OnGameListener;
import com.cpxiao.colorclick.R;
import com.cpxiao.commonlibrary.utils.PreferencesUtils;
import com.cpxiao.minigamelib.activity.BaseActivity;
import com.cpxiao.minigamelib.views.DialogUtils;

/**
 * Created by cpxiao on 8/25/16.
 * GameActivity
 */
public class GameActivity extends BaseActivity {

    private String mGameModel = KeyExtra.KEY_CLASSIC_BEST_SCORE;

    private int mBestScore = 0;
    private TextView mScoreView, mBestScoreView;

    private ImageView mLifeBar0, mLifeBar1, mLifeBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getIntent() != null && getIntent().getStringExtra(KeyExtra.INTENT_NAME_GAME_MODEL) != null) {
            mGameModel = getIntent().getStringExtra(KeyExtra.INTENT_NAME_GAME_MODEL);
        }
        initWidget();
        initSmallAds("167302960362723_167303267029359");
    }

    private void initWidget() {
        mBestScore = PreferencesUtils.getInt(this, mGameModel, 0);
        mBestScoreView = (TextView) findViewById(R.id.best_score);
        mScoreView = (TextView) findViewById(R.id.score);
        setScoreAndBestScore(0);

        mLifeBar0 = (ImageView) findViewById(R.id.life_bar_0);
        mLifeBar1 = (ImageView) findViewById(R.id.life_bar_1);
        mLifeBar2 = (ImageView) findViewById(R.id.life_bar_2);
        ImageView settingsBtn = (ImageView) findViewById(R.id.btn_settings);


        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        ColorClickView view = new ColorClickView(this);
        view.setOnGameListener(mOnGameListener);
        layout.addView(view);

    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferencesUtils.putInt(this, mGameModel, mBestScore);
    }

    private OnGameListener mOnGameListener = new OnGameListener() {

        boolean isGameOver = false;

        @Override
        public void onLifeChange(final int life) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLifeBar(life);
                }
            });
        }

        @Override
        public void onGameOver() {
            if (isGameOver) {
                return;
            }
            isGameOver = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.createGameOverDialog(GameActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GameActivity.comeToMe(GameActivity.this, mGameModel);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }
            });
        }

        @Override
        public void onScoreChange(final int score) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setScoreAndBestScore(score);
                }
            });

        }
    };


    private void setLifeBar(int life) {
        if (life <= 0) {
            mLifeBar0.setImageResource(R.drawable.life_dead);
            mLifeBar1.setImageResource(R.drawable.life_dead);
            mLifeBar2.setImageResource(R.drawable.life_dead);
        } else if (life == 1) {
            mLifeBar0.setImageResource(R.drawable.life_alive);
            mLifeBar1.setImageResource(R.drawable.life_dead);
            mLifeBar2.setImageResource(R.drawable.life_dead);
        } else if (life == 2) {
            mLifeBar0.setImageResource(R.drawable.life_alive);
            mLifeBar1.setImageResource(R.drawable.life_alive);
            mLifeBar2.setImageResource(R.drawable.life_dead);
        } else if (life >= 3) {
            mLifeBar0.setImageResource(R.drawable.life_alive);
            mLifeBar1.setImageResource(R.drawable.life_alive);
            mLifeBar2.setImageResource(R.drawable.life_alive);
        }
    }

    private void setScoreAndBestScore(int score) {
        mScoreView.setText(String.valueOf(score));
        if (score > mBestScore) {
            mBestScore = score;
        }
        mBestScoreView.setText(String.valueOf(mBestScore));
    }

    public static void comeToMe(Context context, String gameModel) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(KeyExtra.INTENT_NAME_GAME_MODEL, gameModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
