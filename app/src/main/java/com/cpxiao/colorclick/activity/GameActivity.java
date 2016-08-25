package com.cpxiao.colorclick.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.colorclick.ColorClickView;
import com.cpxiao.colorclick.OnGameListener;
import com.cpxiao.colorclick.R;
import com.cpxiao.commonlibrary.utils.PreferencesUtils;

/**
 * Created by cpxiao on 8/25/16.
 * GameActivity
 */
public class GameActivity extends BaseActivity {
    private static final String KEY_BEST_SCORE = "KEY_BEST_SCORE";

    private int mBestScore;
    private TextView mScoreView, mBestScoreView;


    private ImageView mLifeBar0, mLifeBar1, mLifeBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initWidget();
    }

    private void initWidget() {
        mBestScore = PreferencesUtils.getInt(this, KEY_BEST_SCORE, 0);
        mBestScoreView = (TextView) findViewById(R.id.best_score);
        mScoreView = (TextView) findViewById(R.id.score);

        mLifeBar0 = (ImageView) findViewById(R.id.life_bar_0);
        mLifeBar1 = (ImageView) findViewById(R.id.life_bar_1);
        mLifeBar2 = (ImageView) findViewById(R.id.life_bar_2);
        ImageView menu = (ImageView) findViewById(R.id.menu);


        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        ColorClickView view = new ColorClickView(this);
        view.setOnGameListener(mOnGameListener);
        layout.addView(view);

    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferencesUtils.putInt(this, KEY_BEST_SCORE, mBestScore);
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
                    AlertDialog dialog = new AlertDialog.Builder(GameActivity.this)
                            .setTitle("Game Over")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
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
        mScoreView.setText(getResources().getString(R.string.score) + score);
        if (score > mBestScore) {
            mBestScore = score;
            mBestScoreView.setText(getResources().getString(R.string.best_score) + mBestScore);
        }
    }

    public static void comeToMe(Context context) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
