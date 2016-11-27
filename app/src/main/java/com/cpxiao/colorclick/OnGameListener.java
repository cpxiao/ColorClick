package com.cpxiao.colorclick;

/**
 * OnGameListener
 *
 * @author cpxiao on 2016/8/25.
 */
public interface OnGameListener {

    void onLifeChange(int life);

    void onGameOver();

    void onScoreChange(int score);
}
