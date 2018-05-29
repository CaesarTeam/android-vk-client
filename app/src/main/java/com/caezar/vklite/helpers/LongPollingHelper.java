package com.caezar.vklite.helpers;

import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.response.PollingNewMessage;

import java.util.LinkedHashMap;

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

    public static PollingNewMessage constructMessage(Object[] objects) {
        int messageId = (int) objects[1];
        int flags = (int) objects[2];
        int peerId = (int) objects[3];
        int timestamp = (int) objects[4];
        String message = (String) objects[5];

        LinkedHashMap linkedHashMap = (LinkedHashMap) objects[6];
        int fromId = 0;
        if (linkedHashMap.containsKey("from")) {
            fromId = Integer.parseInt((String) linkedHashMap.get("from"));
        }

        return new PollingNewMessage(messageId, flags, peerId, timestamp, message, fromId);
    }
}
