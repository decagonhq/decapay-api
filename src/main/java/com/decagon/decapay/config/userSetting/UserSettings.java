package com.decagon.decapay.config.userSetting;

import lombok.Data;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Data
public class UserSettings implements JSONAware {
    private String countryCode;
    private String currencyCode;
    private String language;

    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("countryCode", this.getCountryCode());
        obj.put("currencyCode", this.getCurrencyCode());
        obj.put("language", this.getLanguage());
        return obj.toJSONString();
    }
}
