package com.decagon.decapay.utils;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDateUtil {
    public static boolean isValidFormat(String format, String value) {
        if(value==null){
            return false;
        }
        LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
        if (!value.equals(date.toString())) {
            date = null;
        }
        return date != null;
    }

    public static String getPresentYear() {
        Date dt = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date(dt.getTime()));
    }

    public static String formatDateToString(LocalDate localDate, String pattern) {
        if (localDate == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return localDate.format(formatter);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static String formatLocalDateTimeToString(LocalDateTime localDate, String pattern) {
        if (localDate == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return localDate.format(formatter);
        } catch (DateTimeException e) {
            return null;
        }
    }


}
