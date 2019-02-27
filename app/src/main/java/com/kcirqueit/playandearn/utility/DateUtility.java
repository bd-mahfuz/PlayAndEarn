package com.kcirqueit.playandearn.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtility {

    public static long timeStringToMilli(String startAfter) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = dateFormat.parse(startAfter);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static String milliToHour(long timeInMilli) {
        Date date = new Date(timeInMilli);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);

    }

}
