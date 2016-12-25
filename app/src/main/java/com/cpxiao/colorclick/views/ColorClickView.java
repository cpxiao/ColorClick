package com.cpxiao.colorclick.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cpxiao.colorclick.R;
import com.cpxiao.colorclick.imps.OnGameListener;
import com.cpxiao.lib.Config;

import java.util.Random;

/**
 * ColorClickView
 *
 * @author cpxiao on 2016/8/23.
 */
public class ColorClickView extends BaseSurfaceViewFPS implements View.OnTouchListener {
    private static final boolean DEBUG = Config.DEBUG;
    private static final String TAG = ColorClickView.class.getSimpleName();

    private static final int COLOR_BG = 0xFFE2E2E2;
    private static final int COLOR_YELLOW = 0xFFFDA948;
    private static final int COLOR_RED = 0xFFFC458C;
    private static final int COLOR_GREEN = 0xFFBAE23B;


    /**
     * 横竖条目数
     */
    private int mX = 4, mY = 6;

    /**
     * 开始项
     */
    private int mStartX = -1, mStartY = -1;
    /**
     * 点击区域
     */
    private int mClickX = -1, mClickY = -1;

    private BaseCircle[][] mCircles;
    private Random mRandom = new Random();

    /**
     * 游戏控制
     */
    private boolean isGameOver = false;
    private int mLife = 3, mScore = 0;
    private OnGameListener mOnGameListener;

    /**
     * 游戏控制，生成可点击圆
     */
    private int mCircleControl = 0;
    private int mCircleControlMax = mFPS / 2;

    public ColorClickView(Context context) {
        super(context);
    }

    public ColorClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initWidget() {
        setBgColor(Color.WHITE);
        if (mX <= 0 || mY <= 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("value error! mX <= 0 || mY <= 0");
            }
            return;
        }
        mCircles = new BaseCircle[mY][mX];
        int circleR = (int) (0.8f * Math.min(mViewWidth / mX, mViewHeight / mY) / 2);

        mStartX = mRandom.nextInt(mX);
        mStartY = mRandom.nextInt(mY);
        for (int y = 0; y < mY; y++) {
            for (int x = 0; x < mX; x++) {
                int circleX = (int) ((0.5f + x) * mViewWidth / mX);
                int circleY = (int) ((0.5f + y) * mViewHeight / mY);
                if (y == mStartY && x == mStartX) {
                    mCircles[y][x] = new BlinkCircle(circleX, circleY, circleR, circleR, (int) (0.8f * circleR), COLOR_BG, COLOR_YELLOW, true);
                } else {
                    mCircles[y][x] = new BaseCircle(circleX, circleY, circleR);
                }
            }
        }

        setOnTouchListener(this);
    }

    @Override
    public void drawCache() {
        drawCircles(mCanvasCache);
    }

    private void drawCircles(Canvas canvas) {
        if (mCircles == null) {
            return;
        }
        for (int y = 0; y < mY; y++) {
            for (int x = 0; x < mX; x++) {
                BaseCircle circle = mCircles[y][x];
                if (circle != null) {
                    mPaint.setColor(circle.bgColor);
                    canvas.drawCircle(circle.x, circle.y, circle.bgR, mPaint);
                    mPaint.setColor(circle.color);
                    canvas.drawCircle(circle.x, circle.y, circle.r, mPaint);
                    if (circle instanceof BlinkCircle && ((BlinkCircle) circle).isStart) {
                        //写文字『开始』
                        Paint paint = new Paint();
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(circle.bgR / 3);
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(getResources().getString(R.string.start), circle.x, circle.y + circle.bgR / 5, paint);
                    }
                }
            }
        }
    }

    @Override
    protected void timingLogic() {
        if (mCircles == null) {
            return;
        }
        for (int y = 0; y < mY; y++) {
            for (int x = 0; x < mX; x++) {
                BaseCircle circle = mCircles[y][x];
                if (circle instanceof DisappearingCircle) {
                    DisappearingCircle dCircle = (DisappearingCircle) circle;
                    dCircle.r = dCircle.bgR * dCircle.showTime / dCircle.showTimeMax;
                    if (dCircle.showTime <= 0) {
                        if (!dCircle.isClicked) {
                            // 漏点了，挂了一条命
                            if (mOnGameListener != null) {
                                mLife--;
                                mOnGameListener.onLifeChange(mLife);
                                if (mLife <= 0) {
                                    isGameOver = true;
                                    mOnGameListener.onGameOver();
                                }
                            }
                        }
                        mCircles[y][x] = new BaseCircle(circle.x, circle.y, circle.bgR);
                    } else {
                        dCircle.showTime--;
                    }

                } else if (circle instanceof BlinkCircle) {
                    BlinkCircle bCircle = (BlinkCircle) circle;
                    bCircle.r = (bCircle.bgR - (bCircle.bgR - bCircle.minR) * bCircle.blinkTime / bCircle.blinkTimeMax);
                    if (bCircle.isEnlarge) {
                        bCircle.blinkTime++;
                    } else {
                        bCircle.blinkTime--;
                    }
                    if (bCircle.blinkTime > bCircle.blinkTimeMax) {
                        bCircle.isEnlarge = false;
                    } else if (bCircle.blinkTime <= 0) {
                        bCircle.isEnlarge = true;
                    }
                }
            }
        }

        /**
         * 随机生成
         */
        mCircleControl++;
        if (mCircleControl > mCircleControlMax) {
            createColorCircle();
            int tmp = mRandom.nextInt(10);
            if (tmp < 1) {
                createColorCircle();
            }
            mCircleControl = 0;
            mCircleControlMax = mFPS * 2 / 3 - mScore / 5 - mRandom.nextInt(mFPS / 10);
            if (mCircleControlMax <= 0) {
                mCircleControlMax = 5 + mRandom.nextInt(mFPS / 20);
            }
        }
    }

    private void createColorCircle() {
        if (isGameOver) {
            return;
        }
        if (mStartX >= 0 && mStartY >= 0) {
            return;
        }
        int x = mRandom.nextInt(mX);
        int y = mRandom.nextInt(mY);
        int circleR = (int) (0.8f * Math.min(mViewWidth / mX, mViewHeight / mY) / 2);
        int circleX = (int) ((0.5f + x) * mViewWidth / mX);
        int circleY = (int) ((0.5f + y) * mViewHeight / mY);
        mCircles[y][x] = new DisappearingCircle(circleX, circleY, circleR, circleR, COLOR_BG, COLOR_YELLOW, false);
    }


    private boolean checkX(int x) {
        return x >= 0 && x < mX;
    }

    private boolean checkY(int y) {
        return y >= 0 && y < mY;
    }

    private void clickLogic() {
        if (mCircles == null) {
            return;
        }
        if (!checkX(mClickX) || !checkY(mClickY)) {
            return;
        }
        if (checkX(mStartX) && checkY(mStartY)) {
            //还未未点击『开始』
            if (mStartX != mClickX || mStartY != mClickY) {
                //点击位置不是开始的位置，就直接返回
                return;
            }
        }
        BaseCircle circle = mCircles[mClickY][mClickX];

        if (mStartX == mClickX && mStartY == mClickY) {
            mStartX = -1;
            mStartY = -1;
            //点击了『开始』
            mCircles[mClickY][mClickX] = new DisappearingCircle(circle.x, circle.y, circle.bgR, circle.r, circle.bgColor, COLOR_GREEN, true);
            changeScore();
        } else if (circle instanceof DisappearingCircle && !((DisappearingCircle) circle).isClicked) {
            //点击了『可点处』,并且未点过此处
            mCircles[mClickY][mClickX] = new DisappearingCircle(circle.x, circle.y, circle.bgR, circle.r, circle.bgColor, COLOR_GREEN, true);
            changeScore();
        } else {
            //点击了『不可点处』，game over
            mCircles[mClickY][mClickX] = new BlinkCircle(circle.x, circle.y, circle.bgR, circle.r, 0, circle.bgColor, COLOR_RED, false);
            if (mOnGameListener != null) {
                isGameOver = true;
                mOnGameListener.onGameOver();
            }
        }
        mClickX = -1;
        mClickY = -1;
    }

    private void changeScore() {
        mScore++;
        if (mOnGameListener != null) {
            mOnGameListener.onScoreChange(mScore);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            mClickX = (int) (event.getX() * mX / mViewWidth);
            mClickY = (int) (event.getY() * mY / mViewHeight);
            if (DEBUG) {
                Log.d(TAG, "onTouch: mClickX = " + mClickX + ", mClickY = " + mClickY);
            }
            if (checkX(mClickX) && checkY(mClickY)) {
                clickLogic();
            } else {
                mClickX = -1;
                mClickY = -1;
            }
        }
        return true;
    }

    public void setOnGameListener(OnGameListener l) {
        mOnGameListener = l;
    }

    private class BlinkCircle extends BaseCircle {
        boolean isStart = false;
        int blinkTime = 10;
        int blinkTimeMax = 10;
        boolean isEnlarge = false;//扩大
        int minR;//缩小至最小时半径大小

        public BlinkCircle(int x, int y, int bgR, int r, int minR, int bgColor, int color, boolean isStart) {
            super(x, y, bgR, r, bgColor, color);
            this.minR = minR;
            this.isStart = isStart;
        }
    }

    private class DisappearingCircle extends BaseCircle {
        boolean isClicked = false;//是否已经点击过
        int showTime = 20;
        int showTimeMax = 20;

        public DisappearingCircle(int x, int y, int bgR, int r, int bgColor, int color, boolean isClicked) {
            super(x, y, bgR, r, bgColor, color);
            this.isClicked = isClicked;
            if (isClicked) {
                showTime = 9;
                showTimeMax = 10;
            } else {
                showTime = 30;
                showTimeMax = 30;
            }
        }
    }


    private class BaseCircle {
        int x;
        int y;
        int bgR;
        int r;
        int bgColor = COLOR_BG;
        int color = COLOR_BG;

        public BaseCircle(int x, int y, int bgR) {
            this.x = x;
            this.y = y;
            this.bgR = bgR;
            this.r = bgR;
        }

        public BaseCircle(int x, int y, int bgR, int r, int bgColor, int color) {
            this.x = x;
            this.y = y;
            this.bgR = bgR;
            this.r = r;
            this.bgColor = bgColor;
            this.color = color;
        }
    }
}
