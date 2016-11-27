package com.cpxiao.colorclick.ads.core;

import java.util.Queue;

/**
 * @author cpxiao on 2016/11/24.
 */
public interface ZAdListener {

    void onLoadSuccess(Advertisement ad);

    void onLoadFailed(Advertisement ad, String message, Queue<Advertisement> next);

    void onAdClick(Advertisement ad);

}
