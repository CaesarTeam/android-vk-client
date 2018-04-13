package com.caezar.vklite.libs;

import android.annotation.SuppressLint;
import android.content.Context;

import com.caezar.vklite.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by seva on 07.04.18 in 12:06.
 */

public class Time {

    public enum Format {
        HOURS_MINUTES_SECONDS("HH:mm:ss"),
        DAY_MONTH("dd.MM"),
        HOURS_MINUTES("HH:mm");

        private final String format;

        Format(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public static String getDateTime(long UnixTimestamp, Format format) {
        long milliseconds = unixTimestampToMilliseconds(UnixTimestamp);
        Date date = new Date(milliseconds);
        // todo: remove suppress
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(format.getFormat());
        return formatter.format(date);
    }

    public static String getDateTimeForDialog(long unixTimestamp, Context context) {
        long milliseconds = unixTimestampToMilliseconds(unixTimestamp);
        Date dialogDate = new Date(milliseconds);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date todayMidnight = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterdayMidnight = calendar.getTime();

        if (dialogDate.before(todayMidnight)) {
            if (dialogDate.before(yesterdayMidnight)) {
                return getDateTime(unixTimestamp, Format.DAY_MONTH);
            }

            return context.getString(R.string.yesterday);
        }

        return getDateTime(unixTimestamp, Format.HOURS_MINUTES);
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
