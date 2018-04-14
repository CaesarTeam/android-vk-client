package com.caezar.vklite;

import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.DialogsRequest;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Config {
    public static int peerIdConstant = 2000000000;
    public static int minItemsToRequestChat = new ChatRequest().getCount() / 2;
    public static int minItemsToRequestDialogs = new DialogsRequest().getCount() / 10;
    public static boolean LOG_ENABLE = true;
    private static int myselfId;
    private static String token;

    public static int getMyselfId() {
        return myselfId;
    }

    public static void setMyselfId(int myselfId) {
        Config.myselfId = myselfId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Config.token = token;
    }

    public Config() {
    }
}