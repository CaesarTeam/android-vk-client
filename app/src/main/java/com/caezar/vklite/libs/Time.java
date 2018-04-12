package com.caezar.vklite.libs;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
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

    public static String getStringTime(long UnixTimestamp) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        long milliseconds = unixTimestampToMilliseconds(UnixTimestamp);
        Date date = new Date(milliseconds);

        if (date.before(today)) {
            if (date.before(yesterday)) {
                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd.MM");
                return formatter.format(date);
            } else {
                return "вчера";
            }
        } else {
            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(date);
        }
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
