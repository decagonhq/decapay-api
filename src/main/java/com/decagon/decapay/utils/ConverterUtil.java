package com.decagon.decapay.utils;

import com.decagon.decapay.dto.reference.ReferenceListingDto;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConverterUtil {
    public static Collection<ReferenceListingDto> convertCurrency(List<Currency> currencies) {
        return currencies.stream().map(currency -> {
            ReferenceListingDto dto = new ReferenceListingDto();
            dto.setId(currency.getId());
            dto.setName(currency.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    public static Collection<ReferenceListingDto> convertCountries(List<Country> countryList) {
        return countryList.stream().map(country -> {
            ReferenceListingDto dto = new ReferenceListingDto();
            dto.setId(country.getId());
            dto.setName(country.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    public static Collection<ReferenceListingDto> convertLanguages(List<Language> languagesList) {
        return   languagesList.stream().map(language -> {
            ReferenceListingDto dto = new ReferenceListingDto();
            dto.setId(language.getId());
            dto.setName(language.getTitle());
            return dto;
        }).collect(Collectors.toList());
    }

}
