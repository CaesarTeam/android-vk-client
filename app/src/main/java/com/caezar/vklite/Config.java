package com.caezar.vklite;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Config {
    public static final int peerIdConstant = 2000000000;
    public static final int countItemsToRequestDialogs = 60;
    public static final int minItemsToRequestDialogs = countItemsToRequestDialogs / 10;
    public static final int countItemsToRequestChat = 80;
    public static final int minItemsToRequestChat = countItemsToRequestChat / 2;
    public static final boolean LOG_ENABLE = false;
    public static final boolean ONLINE_MODE = true;

    private static int myselfId;
    private static String token;
    private static Context applicationContext;

    private static String key;
    private static String server;
    private static int ts;
    private static int pts;

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

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context applicationContext) {
        Config.applicationContext = applicationContext;
    }

    public static File getApplicationDownloadDir() {
        String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
        String appName = applicationContext.getString(R.string.app_name);
        return new File( externalStorageDirectory + "/" + appName);
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        Config.key = key;
    }

    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
        Config.server = server;
    }

    public static int getTs() {
        return ts;
    }

    public static void setTs(int ts) {
        Config.ts = ts;
    }

    public static int getPts() {
        return pts;
    }

    public static void setPts(int pts) {
        Config.pts = pts;
    }

    public Config() {
    }
}