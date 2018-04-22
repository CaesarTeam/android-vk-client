package com.caezar.vklite;

import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.DialogsRequest;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Config {
    public static final int peerIdConstant = 2000000000;
    public static final int minItemsToRequestChat = new ChatRequest().getCount() / 2;
    public static final int minItemsToRequestDialogs = new DialogsRequest().getCount() / 10;
    public static final boolean LOG_ENABLE = true;
    public static final boolean ONLINE_MODE = true;
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