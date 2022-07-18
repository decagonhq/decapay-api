package com.decagon.decapay.util;

import java.text.MessageFormat;
import java.util.HashMap;

public class ErrorMsgUtils {

    public static final HashMap<String, String> ERR_MSGS = new HashMap<>();

    static {
        ERR_MSGS.put("role.not.found", "Role with key - {0} does not exist.");
        ERR_MSGS.put("user.not.found", "User with id - {0} does not exist.");
        ERR_MSGS.put("admin_user.not.found", "User with id - {0} does not exist.");
        ERR_MSGS.put("admin_user.duplicate", "User with email - {0} already exist.");
        ERR_MSGS.put("user.duplicate", "User with email - {0} already exist.");

    }

    public static String formatMsg(String entityName,String exceptionType, String ...args) {
        String templateKey=entityName.concat(".").concat(exceptionType).toLowerCase();
        String template=ERR_MSGS.get(templateKey);
        if(template!=null) {
            return MessageFormat.format(template, (Object[]) args);
        }
       return templateKey;
    }
}
