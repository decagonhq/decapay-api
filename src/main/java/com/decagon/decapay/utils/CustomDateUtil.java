package com.decagon.decapay.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

}
