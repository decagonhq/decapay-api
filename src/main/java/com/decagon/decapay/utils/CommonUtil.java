package com.decagon.decapay.utils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class CommonUtil {


    public static boolean fieldChanged(String val1, String val2) {
        return !val1.equals(val2);
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int generateOTP() {
        Random random = new Random();
        int min = 1000;
        int max = 9999;
        return random.nextInt(max-min) + min;
    }

    public static int parseToInt(String value){
        return Integer.parseInt(value);
    }

    /**
     * Locale per country iso codes
     */
    public static HashMap<String, Locale> getLocales(){

        HashMap<String, Locale> LOCALES = new HashMap<String, Locale>();
        for (Locale locale : Locale.getAvailableLocales()) {
            LOCALES.put(locale.getCountry(), locale);
        }
        return LOCALES;
    }



}



