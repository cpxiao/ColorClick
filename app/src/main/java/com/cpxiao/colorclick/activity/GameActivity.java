package com.cpxiao.colorclick.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.colorclick.R;
import com.cpxiao.colorclick.imps.OnGameListener;
import com.cpxiao.colorclick.mode.Extra;
import com.cpxiao.colorclick.views.ColorClickView;
import com.cpxiao.colorclick.utils.DialogUtils;
import com.cpxiao.lib.activity.BaseActivity;

/**
 * GameActivity
 *
 * @author cpxiao on 2016/8/25.
 */
public class GameActivity extends BaseActivity {

    private String mGameModel = Extra.KEY_CLASSIC_BEST_SCORE;

    /**
     * 分数
     */
    protected int mBestScore = 0;
    /**
     * 当前分数view
     */
    protected TextView mScoreView;
    /**
     * 最高分view
     */
    protected TextView mBestScoreView;
    /**
     * 生命条
     */
    protected LinearLayout mLifeBar;
    protected ImageView mLifeBarLife0;
    protected ImageView mLifeBarLife1;
    protected ImageView mLifeBarLife2;
    /**
     * Game View Layout
     */
    protected LinearLayout mGameViewLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        if (getIntent() != null) {
            String model = getIntent().getStringExtra(Extra.INTENT_NAME_GAME_MODEL);
            if (model != null) {
                mGameModel = model;
            }
        }
        initWidget();
        initFbAds50("167302960362723_167303267029359");
    }

    protected void initWidget() {

        mScoreView = (TextView) findViewById(R.id.score);
        mBestScoreView = (TextView) findViewById(R.id.best_score);
        mLifeBar = (LinearLayout) findViewById(R.id.layout_life_bar);
        mLifeBarLife0 = (ImageView) findViewById(R.id.life_bar_0);
        mLifeBarLife1 = (ImageView) findViewById(R.id.life_bar_1);
        mLifeBarLife2 = (ImageView) findViewById(R.id.life_bar_2);
        mGameViewLayout = (LinearLayout) findViewById(R.id.game_view_layout);

        mBestScore = PreferencesUtils.getInt(this, mGameModel, 0);
        setScoreAndBestScore(0);

        ColorClickView view = new ColorClickView(this);
        view.setOnGameListener(mOnGameListener);
        mGameViewLayout.addView(view);

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

    private void setScoreAndBestScore(int score) {
        setScore(score);
        if (score > mBestScore) {
            mBestScore = score;
            PreferencesUtils.putInt(this, mGameModel, mBestScore);
        }
        setBestScore(mBestScore);
    }

    protected void setScore(int score) {
        if (mScoreView != null) {
            mScoreView.setText(String.valueOf(score));
        }
    }

    protected void setBestScore(int bestScore) {
        if (mBestScoreView != null) {
            String bestScoreText = getResources().getText(R.string.best_score) + ": " + String.valueOf(bestScore);
            mBestScoreView.setText(bestScoreText);
        }
    }

    protected void setLifeBar(int life) {
        if (life <= 0) {
            mLifeBarLife0.setImageResource(R.drawable.life_dead);
            mLifeBarLife1.setImageResource(R.drawable.life_dead);
            mLifeBarLife2.setImageResource(R.drawable.life_dead);
        } else if (life == 1) {
            mLifeBarLife0.setImageResource(R.drawable.life_alive);
            mLifeBarLife1.setImageResource(R.drawable.life_dead);
            mLifeBarLife2.setImageResource(R.drawable.life_dead);
        } else if (life == 2) {
            mLifeBarLife0.setImageResource(R.drawable.life_alive);
            mLifeBarLife1.setImageResource(R.drawable.life_alive);
            mLifeBarLife2.setImageResource(R.drawable.life_dead);
        } else {
            mLifeBarLife0.setImageResource(R.drawable.life_alive);
            mLifeBarLife1.setImageResource(R.drawable.life_alive);
            mLifeBarLife2.setImageResource(R.drawable.life_alive);
        }

    }

    public static void comeToMe(Context context, String gameModel) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(Extra.INTENT_NAME_GAME_MODEL, gameModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
