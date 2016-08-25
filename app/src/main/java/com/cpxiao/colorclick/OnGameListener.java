package com.cpxiao.colorclick;

/**
 * Created by cpxiao on 8/25/16.
 * OnGameListener
 */
public interface OnGameListener {

    void onLifeChange(int life);

    void onGameOver();

    void onScoreChange(int score);
}
