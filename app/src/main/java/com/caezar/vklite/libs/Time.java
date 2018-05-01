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
        DAY_NAME_MONTH("dd MMMM"),
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
        if (isToday(unixTimestamp)) {
            return getDateTime(unixTimestamp, Format.HOURS_MINUTES);
        }

        if (isYesterday(unixTimestamp)) {
            return context.getString(R.string.yesterday);
        }

        return getDateTime(unixTimestamp, Format.DAY_MONTH);
    }

    public static boolean isDateBefore24hours(int unixTimestamp) {
        long milliseconds = unixTimestampToMilliseconds(unixTimestamp);
        Date date = new Date(milliseconds);

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        return date.before(yesterday);
    }

    private static boolean isToday(long unixTimestamp) {
        long milliseconds = unixTimestampToMilliseconds(unixTimestamp);
        Date date = new Date(milliseconds);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date todayMidnight = calendar.getTime();
        return !date.before(todayMidnight);
    }

    private static boolean isYesterday(long unixTimestamp) {
        long milliseconds = unixTimestampToMilliseconds(unixTimestamp);
        Date date = new Date(milliseconds);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DATE, -1);
        Date yesterdayMidnight = calendar.getTime();
        return !date.before(yesterdayMidnight);
    }

    public static boolean isDifferentDays(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(unixTimestampToMilliseconds(date1));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(unixTimestampToMilliseconds(date2));
        return calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    public static String constructDate(long unixTimestamp, Context context) {
        if (isToday(unixTimestamp)) {
            return context.getString(R.string.today);
        }

        if (isYesterday(unixTimestamp)) {
            return context.getString(R.string.yesterday);
        }

        return getDateTime(unixTimestamp, Format.DAY_NAME_MONTH);
    }

    private static long unixTimestampToMilliseconds(long unixTimestamp) {
        return unixTimestamp * 1000;
    }
}
