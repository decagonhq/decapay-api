package com.decagon.decapay.service.reference.init;

import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.service.reference.country.CountryService;
import com.decagon.decapay.service.reference.language.LanguageService;
import com.decagon.decapay.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service("initializationDatabase")
public class InitializationDatabaseImpl implements InitializationDatabase {

    private final CountryService countryService;
    private final CurrencyService currencyService;
    private final LanguageService languageService;
    private String name;

    @Transactional
    public void populate(String contextName) {
        this.name = contextName;
        createCountries();
        createCurrencies();
        createLanguages();
    }

    private void createLanguages()  {
        if (!languageService.existLanguages()) {
            log.info(String.format("%s : Populating Languages ", name));
            HashMap<String, Locale> locales = CommonUtil.getLocales();
            for (String code : SchemaConstants.LANGUAGE_ISO_CODE) {
                try {
                    Language language = new Language(code,new Locale(code).getDisplayLanguage());
                    languageService.create(language);
                } catch (Exception e) {
                    log.warn("Error while populating languages", e);
                }
            }
        }
    }

    private void createCountries() {
        if (!countryService.existCountries()) {
            log.info(String.format("%s : Populating Countries ", name));
            HashMap<String, Locale> locales = CommonUtil.getLocales();
            for (String code : SchemaConstants.getCountryIsoCode()) {
                try {
                    Locale locale = locales.get(code);
                    if (locale != null) {
                        Country country = new Country();
                        country.setIsoCode(code);
                        String name = locale.getDisplayCountry(new Locale("en"));
                        country.setName(name);
                        countryService.create(country);
                    }
                } catch (Exception e) {
                    log.warn("Error while populating countries", e);
                }
            }
        }
    }
    private void createCurrencies() {

        if (!currencyService.existCurrencies()) {

            log.info(String.format("%s : Populating Currencies ", name));

            HashMap<String, String> currenciesMap = SchemaConstants.getCurrenciesMap();

            for (String code : currenciesMap.keySet()) {
                try {
                    Currency c = Currency.getInstance(code);
                    if (c == null) {
                        log.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
                    }
                    //check if it exist
                    com.decagon.decapay.model.reference.currency.Currency currency = new com.decagon.decapay.model.reference.currency.Currency();
                    currency.setName(c.getCurrencyCode());
                    currency.setCurrency(c);
                    currencyService.create(currency);

                } catch (IllegalArgumentException e) {
                    log.warn(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
                } catch (Exception e) {
                    log.warn("Error while populating currecies", e);
                }
            }
        }

    }

}
