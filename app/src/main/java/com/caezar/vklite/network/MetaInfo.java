package com.caezar.vklite.network;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class MetaInfo {
    private static int myselfId;
    private static String token;

    public static int getMyselfId() {
        return myselfId;
    }

    public static void setMyselfId(int myselfId) {
        MetaInfo.myselfId = myselfId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        MetaInfo.token = token;
    }

    public MetaInfo() {
    }
}