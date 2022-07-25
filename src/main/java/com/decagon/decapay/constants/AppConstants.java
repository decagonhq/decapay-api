package com.decagon.decapay.constants;

public class AppConstants {
    public static final String DEFAULT_APP_NAME = "DecaPay";
    public static final String DEFAULT_COMPANY_WEBSITE = "https://decagon.com";
    public static final String DEFAULT_COMPANY_EMAIL = "petero@decagonhq.com";
    public static final String DEFAULT_COMPANY_NAME = "Decagon";
    public static final String TEMPLATE_FILE_EXT=".ftl";
    public static final String TEMPLATE_PATH = "/templates/mail/";
    public static final String EMAIL_DISCLAIMER_MSG = "This email address was given to us by you or by one of our customers. If you feel that you have received this email in error, please send an email to %s for de-activation";
    public static final String EMAIL_SPAM_DISCLAIMER_MSG = "This email is sent in accordance with the US CAN-SPAM Law in effect 2004-01-01. Removal requests can be sent to this address and will be honored and respected";

    public static final int PASSWORD_RESET_TOKEN_VALIDITY_PERIOD = 168;//in hours(7days)
    public static final int PASSWORD_RESET_CODE_VALIDITY_PERIOD = 10;//in Minutes
    public static final String ANDROID_DEVICE_ID = "1";
    public static final String WEB_DEVICE_ID = "2";
    public static final String PASSWORD_RESET_URI = "/reset-password";
    public static final String USER_URI = "/user";

    // Template Tokens
    public static final String LOGO_PATH = "LOGO_PATH";
    public static final String EMAIL_FOOTER_COPYRIGHT = "EMAIL_FOOTER_COPYRIGHT";
    public static final String EMAIL_DISCLAIMER = "EMAIL_DISCLAIMER";
    public static final String EMAIL_SPAM_DISCLAIMER = "EMAIL_SPAM_DISCLAIMER";
    public static final String EMAIL_FROM_EMAIL = "EMAIL_FROM_EMAIL";
    public static final String EMAIL_PASSWORD_RESET_URL = "EMAIL_PASSWORD_RESET_URL";
    public static final String EMAIL_PASSWORD_RESET_CODE = "EMAIL_PASSWORD_RESET_CODE";
    public static final String EMAIL_FULL_NAME = "EMAIL_FULL_NAME";

    // Email Subject
    public static final String EMAIL_SUBJ_PASSWORD_RESET_EMAIL = "Password Reset Email Subject";

    // Email Templates
    public static final String EMAIL_PASSWORD_RESET_TMPL = "email_password_reset";
    public static final String EMAIL_PASSWORD_RESET_ANDROID_TMPL = "email_password_reset_android";
}
