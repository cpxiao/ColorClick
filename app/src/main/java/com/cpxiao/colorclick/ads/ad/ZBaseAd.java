package com.cpxiao.colorclick.ads.ad;

import android.view.View;
import android.view.ViewGroup;

import com.cpxiao.colorclick.ads.core.Advertisement;
import com.cpxiao.colorclick.ads.core.Advertiser;
import com.cpxiao.colorclick.ads.core.ZAdListener;
import com.cpxiao.colorclick.ads.utils.ThreadUtils;
import com.cpxiao.colorclick.Config;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author cpxiao on 2016/11/24.
 */
public abstract class ZBaseAd implements Advertisement {


    protected static final boolean DEBUG = Config.DEBUG;

    /**
     * 最后一次成功加载广告的时间
     */
    protected long mLastGetAdTimeMillis;
    /**
     * ZAdListener
     */
    protected ZAdListener mZAdListener;
    /**
     * 广告相关配置参数
     */
    private static final int AD_CACHE_COUNT_DEFAULT = 3;//默认广告缓存数量
    private static final int AD_CACHE_TIME_DEFAULT = 180;//默认广告缓存时间，单位：秒
    protected String mPublishId;
    protected String mPlaceId;
    protected int mAdSize;
    protected int mAdCacheCount = AD_CACHE_COUNT_DEFAULT;
    protected int mAdCacheTime = AD_CACHE_TIME_DEFAULT;

    /**
     * 是否正在加载
     */
    protected AtomicBoolean mLoading = new AtomicBoolean(false);

    /**
     * 最后使用的广告View
     */
    protected View mLastView;

    /**
     * 未使用的广告缓存
     */
    protected Queue<View> mAdViewQueue = new ConcurrentLinkedQueue<>();

    private ZBaseAd() {

    }

    public ZBaseAd(Advertiser advertiser) {
        mPublishId = advertiser.publishId;
        mPlaceId = advertiser.placeId;
        mAdSize = advertiser.adSize;
        if (advertiser.adCacheTime > 0) {
            mAdCacheCount = advertiser.adCacheCount;
        }
        if (advertiser.adCacheTime > 0) {
            mAdCacheTime = advertiser.adCacheTime;
        }
    }

    @Override
    public boolean hasAd() {
        return !(mAdViewQueue == null || mAdViewQueue.size() <= 0);
    }

    @Override
    public void setListener(ZAdListener listener) {
        mZAdListener = listener;
    }

    /**
     * 判断缓存的广告是否超时，若超时，将缓存清空，但不要设为null
     */
    public void clearCache() {
        if (mAdViewQueue != null) {
            mAdViewQueue.clear();
        }
    }

    @Override
    public void destroyLastView() {
        removeFromParent(mLastView);
        mLastView = null;
    }

    public void destroyAllView() {
        destroyLastView();
        clearCache();
        mAdViewQueue = null;
    }

    @Override
    public View getLastView() {
        if (!hasAd()) {
            // 如果没有缓存广告，就把最后一次使用的广告返回
            removeFromParent(mLastView);
            return mLastView;
        } else {
            // 为防止广告View可能导致内存泄露，先把上一次view释放掉
            destroyLastView();
        }
        mLastView = mAdViewQueue.poll();

        // 判断缓存广告是否超时，超时就清空，以便发起下次请求
        if (System.currentTimeMillis() - mLastGetAdTimeMillis > mAdCacheTime * 1000) {
            clearCache();
        }
        return getLastAdView();
    }

    /**
     * 获得最后的广告View
     *
     * @return View
     */
    protected abstract View getLastAdView();

    /**
     * 加载成功
     *
     * @param advertisement ad
     */
    protected void onLoadZAdSuccess(final Advertisement advertisement) {
        //回调必须在主线程中,否则next的请求会崩溃
        ThreadUtils.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mZAdListener != null) {
                    mLastGetAdTimeMillis = System.currentTimeMillis();
                    mZAdListener.onLoadSuccess(advertisement);
                }
            }
        });
    }

    /**
     * 加载失败
     *
     * @param advertisement ad
     * @param msg           message
     * @param next          next ad
     */
    protected void onLoadZAdFail(final Advertisement advertisement, final String msg, final Queue<Advertisement> next) {
        //回调必须在主线程中,否则next的请求会崩溃
        ThreadUtils.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mZAdListener != null) {
                    mZAdListener.onLoadFailed(advertisement, msg, next);
                }
            }
        });
    }

    /**
     * 从父容器中移除
     *
     * @param view View
     */
    protected void removeFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    protected Advertisement get() {
        return this;
    }
}
