package com.cpxiao.colorclick.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cpxiao.colorclick.Config;
import com.cpxiao.colorclick.R;
import com.cpxiao.colorclick.ads.ZAdManager;
import com.umeng.analytics.MobclickAgent;


/**
 * BaseActivity
 *
 * @author cpxiao on 2016/6/13
 */
public class BaseActivity extends Activity {
    protected static final boolean DEBUG = Config.DEBUG;
    protected final String TAG = "CPXIAO--" + getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏状态栏部分（电池电量、时间等部分）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void initSmallAds(Context context, int adPosition) {
        if (DEBUG) {
            Log.d(TAG, "initSmallAds: ");
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.ads_layout);
        if (layout == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("error! layout == null");
            }
            return;
        }
        View view = ZAdManager.getInstance().getAd(context, adPosition);
        if (view != null) {
            layout.removeAllViews();
            layout.addView(view);
        }
    }
}
