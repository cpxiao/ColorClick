package com.cpxiao.colorclick.ads;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.cpxiao.colorclick.Config;
import com.cpxiao.colorclick.ads.ad.FbBannerAd;
import com.cpxiao.colorclick.ads.ad.FbNativeAd;
import com.cpxiao.colorclick.ads.core.Advertisement;
import com.cpxiao.colorclick.ads.core.Advertiser;
import com.cpxiao.colorclick.ads.core.ZAdListener;
import com.cpxiao.colorclick.ads.core.ZAdType;
import com.cpxiao.colorclick.ads.utils.ThreadUtils;
import com.cpxiao.colorclick.ads.utils.UMeng;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cpxiao.colorclick.ads.core.ZAdPosition.POSITION_BEST_SCORE_ACTIVITY;
import static com.cpxiao.colorclick.ads.core.ZAdPosition.POSITION_GAME_ACTIVITY;
import static com.cpxiao.colorclick.ads.core.ZAdPosition.POSITION_HOME_ACTIVITY;


/**
 * @author cpxiao on 2016/11/24.
 */
public class ZAdManager {

    public static final String TAG = ZAdManager.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;


    //广告配置数据
    private ArrayMap<Integer, List<Advertiser>> mListArrayMap = null;

    //成功获取广告的list
    private ArrayMap<Integer, Advertisement> mViewArrayMap = null;
    private ArrayMap<Integer, Boolean> mLoadingArrayMap = null;

    //是否正在请求服务器广告位置配置信息
    private AtomicBoolean mRequesting = new AtomicBoolean(false);

    /**
     * “双重检查锁”--兼顾线程安全和效率的单例写法，注意关键字volatile
     */
    private static volatile ZAdManager mInstance = null;

    private ZAdManager() {
        if (DEBUG) {
            Log.d(TAG, "ZAdManager: ............................");
        }
    }

    public static ZAdManager getInstance() {
        if (mInstance == null) {
            synchronized (ZAdManager.class) {
                if (mInstance == null) {
                    mInstance = new ZAdManager();
                }
            }
        }
        return mInstance;
    }

    public void init(final Context context) {
        //fb的原生广告在无vpn的时候不会回调成功或失败，在此处new ArrayMap，防止卡在正在进行
        mViewArrayMap = new ArrayMap<>();
        mLoadingArrayMap = new ArrayMap<>();

        if (mRequesting.get()) {
            if (DEBUG) {
                Log.d(TAG, "初始化配置正在进行中！");
            }
            return;
        }
        mRequesting.set(true);

        mListArrayMap = ZAdDefaultConfig.getDefaultConfig();
        mRequesting.set(false);
        ThreadUtils.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                //初始化广告配置之后加载首页、搜索页、详情页广告
                getAd(context, POSITION_HOME_ACTIVITY);
                getAd(context, POSITION_GAME_ACTIVITY);
                getAd(context, POSITION_BEST_SCORE_ACTIVITY);
            }
        });
    }


    /**
     * @param appCxt   传入Application Context
     * @param position 广告位置
     * @return View
     */
    public View getAd(Context appCxt, int position) {
        if (appCxt == null || mViewArrayMap == null) {
            if (DEBUG) {
                //                throw new IllegalArgumentException("error! appCxt == null || mViewArrayMap == null");
            }
            return null;
        }
        Advertisement advertisement = mViewArrayMap.get(position);
        //1)获取广告
        View adView = null;
        if (advertisement != null && advertisement.hasAd()) {
            adView = advertisement.getLastView();
            UMeng.postStat(appCxt, UMeng.SDK_AD_IMPRESSION, advertisement.toString() + position);
        }

        //2)判断是否要加载
        if (advertisement == null || !advertisement.hasAd()) {
            if (DEBUG) {
                Log.d(TAG, "无广告缓存View，开始请求广告");
            }
            requestAd(appCxt, position);
        }

        return adView;
    }

    /**
     * 清空所有广告，包含缓存广告
     */
    public void destroyAll() {
        destroyAll(POSITION_HOME_ACTIVITY);
        destroyAll(POSITION_GAME_ACTIVITY);
        destroyAll(POSITION_BEST_SCORE_ACTIVITY);
    }

    /**
     * 清空所有广告，包含缓存广告
     *
     * @param position 广告位
     */
    public void destroyAll(int position) {
        if (mViewArrayMap == null) {
            if (DEBUG) {
                Log.d(TAG, "destroyAllView->有点奇怪的，没有View！");
            }
            return;
        }
        Advertisement advertisement = mViewArrayMap.get(position);
        if (advertisement == null) {
            if (DEBUG) {
                Log.d(TAG, "无广告，请找开发人员！！！position = " + position);
            }
            return;
        }
        advertisement.destroyAllView();
    }

    private void requestAd(final Context appCxt, final int position) {
        if (appCxt == null || mListArrayMap == null) {
            if (DEBUG) {
                Log.d(TAG, "广告未进行初始化配置！!!");
            }
            return;
        }
        boolean isLoading = false;
        try {
            //部分手机莫名其妙报空指针,可能是还没set就get导致的。一脸蒙逼
            isLoading = mLoadingArrayMap.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isLoading) {
            if (DEBUG) {
                Log.d(TAG, "广告正在进行加载，返回！position = " + position);
            }
            return;
        }
        List<Advertiser> list = mListArrayMap.get(position);
        if (list == null || list.isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "服务器广告配置信息空！position = " + position);
            }
            return;
        }
        //先将当前广告位的广告商排队，之后依次请求
        Queue<Advertisement> queue = new ConcurrentLinkedQueue<>();
        for (Advertiser advertiser : list) {
            Advertisement advertisement = getAdvertisement(appCxt, advertiser);
            if (advertisement != null) {
                queue.add(advertisement);
            }
        }
        //请求第一个,如果失败请求下一个
        mLoadingArrayMap.put(position, true);
        final Advertisement advertisement = queue.poll();

        //此处有个坑，注意回调的时候要在主线程中，否则会crash！
        advertisement.setListener(new ZAdListener() {
            @Override
            public void onLoadSuccess(Advertisement ad) {
                if (DEBUG) {
                    Log.d(TAG, "onLoadSuccess: " + position);
                    Log.d(TAG, position + ":" + ad.toString() + "广告请求成功");
                }
                mViewArrayMap.put(position, ad);
                mLoadingArrayMap.put(position, false);
                UMeng.postStat(appCxt, UMeng.SDK_AD_SUCCESS, ad.toString() + position);
            }

            @Override
            public void onLoadFailed(Advertisement ad, String message, Queue<Advertisement> next) {
                if (DEBUG) {
                    Log.d(TAG, "onLoadFailed: " + position);
                    Log.d(TAG, position + "位置失败：" + message);
                }
                UMeng.postStat(appCxt, UMeng.SDK_AD_FAIL, ad.toString() + position);
                //获取下一个广告商并加载
                Advertisement ads = next.poll();
                if (ads == null) {
                    if (DEBUG) {
                        Log.d(TAG, "位置" + position + "全部失败，结束");
                    }
                    mLoadingArrayMap.put(position, false);
                    return;
                }
                ads.setListener(this);
                ads.load(appCxt, next);
                UMeng.postStat(appCxt, UMeng.SDK_AD_LOAD, ads.toString() + position);
                if (DEBUG) {
                    Log.d(TAG, position + "进行下一个广告：" + ads.toString());
                }
            }

            @Override
            public void onAdClick(Advertisement ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClick: ");
                }
                UMeng.postStat(appCxt, UMeng.SDK_AD_CLICK, ad.toString() + position);
            }
        });
        advertisement.load(appCxt, queue);
        if (DEBUG) {
            Log.d(TAG, "requestAd First Ad: ");
            Log.d(TAG, position + "请求广告：" + advertisement.toString());
        }
        UMeng.postStat(appCxt, UMeng.SDK_AD_LOAD, advertisement.toString() + position);
    }

    private Advertisement getAdvertisement(Context context, Advertiser advertiser) {
        switch (advertiser.advertiser) {
            case ZAdType.AD_FB_NATIVE: {
                return new FbNativeAd(advertiser);
            }
            case ZAdType.AD_FB: {
                return new FbBannerAd(advertiser);
            }
            default:
                if (DEBUG) {
                    throw new IllegalArgumentException("ZAdType error!advertiser.advertiser = " + advertiser.advertiser);
                }
                return null;
        }
    }

}
