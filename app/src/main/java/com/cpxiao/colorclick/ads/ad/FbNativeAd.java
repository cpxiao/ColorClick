package com.cpxiao.colorclick.ads.ad;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cpxiao.colorclick.Config;
import com.cpxiao.colorclick.R;
import com.cpxiao.colorclick.ads.core.Advertisement;
import com.cpxiao.colorclick.ads.core.Advertiser;
import com.cpxiao.colorclick.ads.core.ZAdSize;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.Queue;

/**
 * @author cpxiao on 2016/11/24.
 *         坑：在无vpn的情况下长时间不回调成功或失败
 */

public class FbNativeAd extends ZBaseAd {
    private static final boolean DEBUG = Config.DEBUG;
    private static final String TAG = "FbNativeAd";

    private NativeAdsManager mManager;

    public FbNativeAd(Advertiser advertiser) {
        super(advertiser);
    }


    @Override
    public void load(final Context c, final Queue<Advertisement> next) {
        //参数校验
        if (c == null || TextUtils.isEmpty(mPlaceId) || mAdCacheCount <= 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("param error!");
            }
            return;
        }

        if (mLoading.get()) {
            if (DEBUG) {
                Log.d(TAG, "ad is loading! mPlaceId = " + mPlaceId);
            }

            return;
        }
        mLoading.set(true);

        if (DEBUG) {
            Log.d(TAG, "load: mPlaceId = " + mPlaceId);
        }
        mManager = new NativeAdsManager(c, mPlaceId, mAdCacheCount);

        mManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                if (DEBUG) {
                    if (mManager != null) {
                        Log.d(TAG, "onNativeAdsLoaded: mPlaceId = " + mPlaceId + ", list.size() = " + mManager.getUniqueNativeAdCount());
                    }
                }
                mLoading.set(false);
                NativeAd ad;
                int index = 0;
                if (mManager != null) {
                    while ((ad = mManager.nextNativeAd()) != null && index < mAdCacheCount) {
                        index++;
                        View view = generateView(c, ad);
                        if (mAdViewQueue != null && view != null) {
                            mAdViewQueue.add(view);
                        }
                    }
                    onLoadZAdSuccess(get());
                } else {
                    if (DEBUG) {
                        throw new IllegalArgumentException("error! mManager == null");
                    }
                }
            }

            @Override
            public void onAdError(AdError adError) {
                if (DEBUG) {
                    Log.d(TAG, "onNativeAdFailed: mPlaceId = " + mPlaceId + ", " + adError.getErrorMessage());
                }
                mLoading.set(false);
                onLoadZAdFail(get(), adError.getErrorMessage(), next);

            }
        });
        if (DEBUG) {
            //            AdSettings.addTestDevice("cb0c8a003984d4e58e122ee73e0b4f1d");//htc
            //            AdSettings.addTestDevice("e2aec15df12cd8b4571af5f1275a34ec");//htc native?
            AdSettings.addTestDevice("55c4f301d7c1183f1fa6ede6b3f2fe2e");//坚果 native?
        }

        mManager.loadAds();

    }



    @Override
    protected View getLastAdView() {
        if (mLastView != null) {
            NativeAd ad = (NativeAd) mLastView.getTag(R.id.tag_info);
            if (ad != null) {
                ad.registerViewForInteraction(mLastView);
            }
        }
        return mLastView;
    }

    @Override
    public void destroyLastView() {
        if (mLastView != null) {
            NativeAd ad = (NativeAd) mLastView.getTag(R.id.tag_info);
            if (ad != null) {
                ad.unregisterView();
                ad.destroy();
            }
        }
        super.destroyLastView();
    }

    @Override
    public void destroyAllView() {
        if (mManager != null) {
            mManager.setListener(null);
            mManager = null;
        }
        super.destroyAllView();
    }

    @Override
    public String toString() {
        return TAG;
    }

//    private FbNativeAd get() {
//        return this;
//    }


    private View generateView(Context c, NativeAd ad) {
        String picture = null;
        String icon = null;
        try {
            picture = ad.getAdCoverImage().getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            icon = ad.getAdIcon().getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Log.d(TAG, "generateView: picture = " + picture);
            Log.d(TAG, "generateView: icon = " + icon);
        }

        if (mAdSize == ZAdSize.BANNER_300X250) {
            if (TextUtils.isEmpty(picture)) {
                return null;
            }
            ImageView view = new ImageView(c);
            Glide.with(c).load(picture).into(view);
            view.setTag(R.id.tag_info, ad);
            return view;
        } else {
            ZBannerView view = new ZBannerView(c);
            view.bindData(c, icon, ad.getAdTitle(), ad.getAdSubtitle());
            view.setTag(R.id.tag_info, ad);
            return view;
        }
    }
}
