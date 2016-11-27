package com.cpxiao.colorclick.ads.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * @author cpxiao on 2016/11/24.
 */
public class UMeng {

    public static final String SDK_AD_LOAD = "SDK_AD_LOAD";
    public static final String SDK_AD_SUCCESS = "SDK_AD_SUCCESS";
    public static final String SDK_AD_FAIL = "SDK_AD_FAIL";
    public static final String SDK_AD_IMPRESSION = "SDK_AD_IMPRESSION";
    public static final String SDK_AD_CLICK = "SDK_AD_CLICK";


    public static void postStat(Context context, String event, String value) {
        MobclickAgent.onEvent(context, event, value);
    }
}
