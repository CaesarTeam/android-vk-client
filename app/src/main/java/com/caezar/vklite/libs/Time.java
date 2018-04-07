package com.caezar.vklite.libs;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by seva on 07.04.18 in 12:06.
 */
public class Time {
    public static String getTime(long UnixTimestamp) {
//        long offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long milliseconds = unixTimestampToMilliseconds(UnixTimestamp);
        Date date = new Date(milliseconds);
        // todo: remove suppress
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    public static int currentDate() {
        return (int) millisecondsToUnixTimestamp(System.currentTimeMillis());
    }

    private static long unixTimestampToMilliseconds(long UnixTimestamp) {
        return UnixTimestamp * 1000;
    }

    private static long millisecondsToUnixTimestamp(long milliseconds) {
        return milliseconds / 1000;
    }
}
