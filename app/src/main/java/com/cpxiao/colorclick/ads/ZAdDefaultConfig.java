package com.cpxiao.colorclick.ads;

import android.support.v4.util.ArrayMap;

import com.cpxiao.colorclick.ads.core.Advertiser;
import com.cpxiao.colorclick.ads.core.ZAdPosition;
import com.cpxiao.colorclick.ads.core.ZAdSize;
import com.cpxiao.colorclick.ads.core.ZAdType;

import java.util.ArrayList;
import java.util.List;


/**
 * 广告本地默认配置
 *
 * @author cpxiao on 2016/11/24.
 */
public class ZAdDefaultConfig {
    /**
     * 未获取到服务器数据时的默认配置
     *
     * @return arrayMap
     */
    public static ArrayMap<Integer, List<Advertiser>> getDefaultConfig() {
        ArrayMap<Integer, List<Advertiser>> arr = new ArrayMap<>(5);

        //Home Activity
        List<Advertiser> adList0 = new ArrayList<>();
        Advertiser ad0 = new Advertiser();
        ad0.advertiser = ZAdType.AD_FB_NATIVE;
        ad0.publishId = "";
        ad0.placeId = "167302960362723_167303233696029";
        ad0.adSize = ZAdSize.BANNER_320X50;
        ad0.adCacheCount = 3;
        ad0.adCacheTime = 180;
        //        adList0.add(ad0);
        Advertiser ad0_1 = new Advertiser();
        ad0_1.advertiser = ZAdType.AD_FB;
        ad0_1.publishId = "";
        ad0_1.placeId = "167302960362723_167303233696029";
        ad0_1.adSize = ZAdSize.BANNER_300X250;
        ad0_1.adCacheCount = 3;
        ad0_1.adCacheTime = 180;
        adList0.add(ad0_1);
        arr.put(ZAdPosition.POSITION_HOME_ACTIVITY, adList0);

        //Game Activity
        List<Advertiser> adList1 = new ArrayList<>();
        Advertiser ad1 = new Advertiser();
        ad1.advertiser = ZAdType.AD_FB_NATIVE;
        ad1.publishId = "";
        ad1.placeId = "167302960362723_167303267029359";
        ad1.adSize = ZAdSize.BANNER_320X50;
        ad1.adCacheCount = 3;
        ad1.adCacheTime = 180;
        adList1.add(ad1);
        Advertiser ad1_1 = new Advertiser();
        ad1_1.advertiser = ZAdType.AD_FB;
        ad1_1.publishId = "";
        ad1_1.placeId = "167302960362723_167303267029359";
        ad1_1.adSize = ZAdSize.BANNER_300X250;
        ad1_1.adCacheCount = 3;
        ad1_1.adCacheTime = 180;
        //        adList1.add(ad1_1);
        arr.put(ZAdPosition.POSITION_GAME_ACTIVITY, adList1);

        //Best Score Activity
        List<Advertiser> adList2 = new ArrayList<>();
        Advertiser ad2 = new Advertiser();
        ad2.advertiser = ZAdType.AD_FB_NATIVE;
        ad2.publishId = "";
        ad2.placeId = "167302960362723_167359617023724";
        ad2.adSize = ZAdSize.BANNER_320X50;
        ad2.adCacheCount = 3;
        ad2.adCacheTime = 180;
        adList2.add(ad2);
        Advertiser ad2_1 = new Advertiser();
        ad2_1.advertiser = ZAdType.AD_FB;
        ad2_1.publishId = "";
        ad2_1.placeId = "167302960362723_167359617023724";
        ad2_1.adSize = ZAdSize.BANNER_300X250;
        ad2_1.adCacheCount = 3;
        ad2_1.adCacheTime = 180;
        //        adList2.add(ad2_1);
        arr.put(ZAdPosition.POSITION_BEST_SCORE_ACTIVITY, adList2);


        return arr;
    }
}
