package com.caezar.vklite;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Config {
    public static final int peerIdConstant = 2000000000;
    public static final int countItemsToRequestDialogs = 60;
    public static final int minItemsToRequestDialogs = countItemsToRequestDialogs / 10;
    public static final int countItemsToRequestChat = 80;
    public static final int minItemsToRequestChat = countItemsToRequestChat / 2;
    public static final boolean LOG_ENABLE = true;
    public static boolean ONLINE_MODE = false;

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