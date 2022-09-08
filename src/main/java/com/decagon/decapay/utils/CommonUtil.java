package com.decagon.decapay.utils;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.constants.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
@Slf4j
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


    public static Object[] getLocaleAndCurrency(String userSettings, ObjectMapper objectMapper){
        Locale finalLocale;
        Currency finalCurrency;

        if (userSettings == null){
            finalLocale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
            finalCurrency = AppConstants.DEFAULT_CURRENCY;
        }else {
            UserSettings currentUserSettings = null;
            try {
                currentUserSettings = objectMapper.readValue(userSettings, UserSettings.class);
                finalLocale = new Locale(currentUserSettings.getLanguage(), currentUserSettings.getCountryCode());
                finalCurrency = Currency.getInstance(currentUserSettings.getCurrencyCode());
            } catch (Exception e) {
                log.error("error while converting user setting JSON String to object ", e );
                finalLocale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
                finalCurrency = AppConstants.DEFAULT_CURRENCY;
            }
        }
        return new Object[]{finalLocale, finalCurrency};
    }


}



