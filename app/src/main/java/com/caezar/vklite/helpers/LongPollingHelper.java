package com.caezar.vklite.helpers;

import com.caezar.vklite.Config;

/**
 * Created by seva on 28.05.18 in 20:53.
 */

public class LongPollingHelper {
    public static String getLongPollingUrl() {
        StringBuilder url = new StringBuilder();
        url.append("https://");
        url.append(Config.getServer());
        url.append("?act=a_check&key=");
        url.append(Config.getKey());
        url.append("&ts=");
        url.append(Config.getTs());
        url.append("&wait=25&mode=10&version=3");
        return url.toString();
    }
}
