package com.caezar.vklite;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Config {
    public static int peerIdConstant = 2000000000;
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