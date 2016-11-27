package com.cpxiao.colorclick.ads.ad;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cpxiao.colorclick.ads.core.Advertisement;
import com.cpxiao.colorclick.ads.core.Advertiser;
import com.cpxiao.colorclick.ads.core.ZAdSize;
import com.cpxiao.colorclick.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.Queue;

/**
 * @author cpxiao on 2016/11/24.
 */

public class FbBannerAd extends ZBaseAd {
    private static final String TAG = "FbBannerAd";
    private static final boolean DEBUG = Config.DEBUG;


    private AdView mAdManager;
    private View mLastView;
    private View mAdView;


    public FbBannerAd(Advertiser advertiser) {
        super(advertiser);
    }

    @Override
    public View getLastView() {
        if (!hasAd()) {
            return mLastView;
        } else {
            //先把上一次view释放掉
            destroyLastView();
        }
        mLastView = mAdView;
        mAdView = null;

        return getLastAdView();
    }

    @Override
    protected View getLastAdView() {
        removeFromParent(mLastView);
        return mLastView;
    }

    @Override
    public boolean hasAd() {
        return mAdView != null;
    }


    @Override
    public void load(final Context c, final Queue<Advertisement> next) {
        //参数校验
        if (c == null || TextUtils.isEmpty(mPlaceId)) {
            if (DEBUG) {
                throw new IllegalArgumentException("param error!");
            }
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "load: mPlaceId = " + mPlaceId);
        }
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
        mAdManager = new AdView(c, mPlaceId, getAdSize(mAdSize));

        mAdManager.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                if (DEBUG) {
                    Log.d(TAG, "onError: ");
                }
                if (mZAdListener != null) {
                    mZAdListener.onLoadFailed(get(), "errorCode = " + adError.getErrorCode() + ", errorMsg = " + adError.getErrorMessage(), next);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: ");
                }
                mAdView = generateView(c, mAdManager, mAdSize);
                if (mZAdListener != null) {
                    mZAdListener.onLoadSuccess(get());
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClicked: ");
                }
                if (mZAdListener != null) {
                    mZAdListener.onAdClick(get());
                }
            }
        });
        if (DEBUG) {
            AdSettings.addTestDevice("e2aec15df12cd8b4571af5f1275a34ec");//htc
        }
        mAdManager.loadAd();
    }

    @Override
    public void destroyLastView() {
        removeFromParent(mLastView);

        mLastView = null;
    }

    @Override
    public void destroyAllView() {
        destroyLastView();
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
        removeFromParent(mAdView);
        mAdView = null;
    }

    private View generateView(Context c, AdView bannerView, int size) {
        if (c == null || bannerView == null) {
            return null;
        }
        removeFromParent(bannerView);

        if (size == ZAdSize.BANNER_300X250) {
            LinearLayout view = new LinearLayout(c);
            view.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (250 * Resources.getSystem().getDisplayMetrics().density + 0.5f));
            view.addView(bannerView, params);
            return view;
        } else {
            if (size != ZAdSize.BANNER_320X50) {
                if (DEBUG) {
                    throw new IllegalArgumentException("No Size found in " + TAG);
                }
            }
            LinearLayout view = new LinearLayout(c);
            view.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (50 * Resources.getSystem().getDisplayMetrics().density + 0.5f));
            view.addView(bannerView, params);
            return view;
        }
    }

    private AdSize getAdSize(int size) {
        if (size == ZAdSize.BANNER_300X250) {
            return AdSize.RECTANGLE_HEIGHT_250;
        } else if (size == ZAdSize.BANNER_320X50) {
            return AdSize.BANNER_HEIGHT_50;
        } else {
            if (DEBUG) {
                throw new IllegalArgumentException("No Size found in " + TAG);
            }
            return AdSize.BANNER_320_50;
        }
    }

    @Override
    public String toString() {
        return TAG;
    }

}