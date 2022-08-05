package com.decagon.decapay.service.currency;

import com.decagon.decapay.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService{
    @Override
    public String formatAmount(BigDecimal amount) {
        if(amount==null){
            amount=BigDecimal.ZERO;
        }
        java.util.Currency currency = AppConstants.DEFAULT_CURRENCY;
        Locale locale ;
        String defaultLanguage= AppConstants.DEFAULT_LANGUAGE;
        String countryCode= AppConstants.DEFAULT_COUNTRY;

        try {
            locale = new Locale(defaultLanguage,countryCode);

        } catch (Exception e) {
            log.error("Cannot create currency or locale instance",e);
            return amount.toString();
        }

        NumberFormat currencyInstance = null;

        if(true) {
            currencyInstance = NumberFormat.getCurrencyInstance(locale);//national
        } else {
            currencyInstance = NumberFormat.getCurrencyInstance();//international
        }
        currencyInstance.setCurrency(currency);

        return currencyInstance.format(amount.doubleValue());

    }

}
