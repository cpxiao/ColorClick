package com.cpxiao.colorclick.ads.core;

import android.content.Context;
import android.view.View;

import java.util.Queue;

/**
 * @author cpxiao on 2016/11/24.
 */
public interface Advertisement {

    /**
     * 加载广告
     *
     * @param c    Application Context
     * @param next 下一个广告商
     */
    void load(Context c, Queue<Advertisement> next);

    /**
     * 清除最后一次使用的广告，不清空缓存广告
     */
    void destroyLastView();

    /**
     * 清空所有广告，包含缓存广告
     */
    void destroyAllView();

    /**
     * 设置广告回调
     *
     * @param listener ZAdListener
     */
    void setListener(ZAdListener listener);

    /**
     * 获取广告
     *
     * @return View
     */
    View getLastView();

    /**
     * 判断是否有缓存广告
     *
     * @return boolean
     */
    boolean hasAd();
}
