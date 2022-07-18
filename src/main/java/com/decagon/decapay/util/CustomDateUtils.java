package com.decagon.decapay.util;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * FourFourTwoDateUtils Object for transforming date to String and back
 *
 */

public class CustomDateUtils {


    public static LocalDate now() {
        return LocalDate.now();
    }

    public static LocalDate convertDateToLocalDate(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate formatStringToLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        return LocalDate.parse(dateString);

    }

    public static LocalDate formatStringToLocalDate(String dateString, String pattern) {
        if (dateString == null||dateString.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));

    }


    public static String formatLocalDateToString(LocalDate date, String pattern) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static boolean isValidFormat(String value, String format) {
        if(value==null){
            return false;
        }
        LocalDate date = null;
        date = LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
        if (!value.equals(date.toString())) {
            date = null;
        }
        return date != null;
    }
}
