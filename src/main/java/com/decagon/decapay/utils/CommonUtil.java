package com.decagon.decapay.utils;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

    public static int generateOTP() throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstanceStrong();
        var otp = 1000 + random.nextInt(9999);
        if(String.valueOf(otp).length() == 4) return otp;
        return generateOTP();
    }

}



