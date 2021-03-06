package com.cpxiao.lib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cpxiao.androidutils.library.utils.StringUtils;
import com.cpxiao.colorclick.R;
import com.cpxiao.lib.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;


/**
 * @author cpxiao on 2016/3/14.
 */
public class BaseActivity extends Activity {
    protected static final boolean DEBUG = Config.DEBUG;
    protected final String TAG = "CPXIAO--" + getClass().getSimpleName();

    protected AdView mFbAdView;
    protected com.google.android.gms.ads.AdView mAdMobAdView;

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
    protected void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, "onDestroy: ");
        }
        if (mFbAdView != null) {
            mFbAdView.destroy();
            mFbAdView = null;
        }
        if (mAdMobAdView != null) {
            mAdMobAdView.destroy();
            mAdMobAdView = null;
        }
        super.onDestroy();
    }

    protected void initFbAds50(String placementId) {
        initFbAds(placementId, AdSize.BANNER_HEIGHT_50);
    }

    protected void initFbAds250(String placementId) {
        initFbAds(placementId, AdSize.RECTANGLE_HEIGHT_250);
    }

    private void initFbAds(String placeId, AdSize adSize) {
        if (DEBUG) {
            Log.d(TAG, "initFbAds: ");
        }

        if (StringUtils.isEmpty(placeId)) {
            if (DEBUG) {
                throw new IllegalArgumentException("placeId is empty!");
            }
            return;
        }

        mFbAdView = new AdView(this, placeId, adSize);

        mFbAdView.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (DEBUG) {
                    Log.d(TAG, "onError: " + error.getErrorCode() + "," + error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: ");
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClicked: ");
                }
            }

        });
        if (DEBUG) {
            //            AdSettings.addTestDevice("7d7fcc8ff3a053e48671f85990f1ab6d");//nexus 5
            AdSettings.addTestDevice("55c4f301d7c1183f1fa6ede6b3f2fe2e");//坚果
        }
        if (DEBUG) {
            Log.d(TAG, "initFbAds:  mFbAdView.loadAd();");
        }
        mFbAdView.loadAd();

        addToLayout(mFbAdView);
    }

    protected void initAdMobAds50(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.SMART_BANNER);
    }

    protected void initAdMobAds100(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.LARGE_BANNER);
    }

    protected void initAdMobAds250(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
    }

    private void initAdMobAds(String unitId, com.google.android.gms.ads.AdSize adSize) {
        if (DEBUG) {
            Log.d(TAG, "initAdMobAds: ");
        }

        if (StringUtils.isEmpty(unitId)) {
            if (DEBUG) {
                throw new IllegalArgumentException("unitId is empty!");
            }
            return;
        }
        mAdMobAdView = new com.google.android.gms.ads.AdView(this);
        mAdMobAdView.setAdUnitId(unitId);
        mAdMobAdView.setAdSize(adSize);
        mAdMobAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (DEBUG) {
                    Log.d(TAG, "onAdClosed: ");
                }

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (DEBUG) {
                    Log.d(TAG, "onAdFailedToLoad: i = " + i);
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                if (DEBUG) {
                    Log.d(TAG, "onAdLeftApplication: ");
                }

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (DEBUG) {
                    Log.d(TAG, "onAdOpened: ");
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: ");
                }

            }
        });
        AdRequest adRequest;
        if (DEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// All emulators
                    .addTestDevice("E89B10531C8CCB95C447A97261F6AA0E")//坚果
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        if (DEBUG) {
            Log.d(TAG, "initAdMobAds: mAdMobAdView.loadAd(adRequest);");
        }
        mAdMobAdView.loadAd(adRequest);
        addToLayout(mAdMobAdView);
    }

    private void addToLayout(View view) {
        if (DEBUG) {
            Log.d(TAG, "addToLayout: ");
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.ads_layout);
        layout.removeAllViews();
        layout.addView(view);
    }
}
