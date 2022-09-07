package com.decagon.decapay.controller;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.reference.UserSettingReferencesDto;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.service.reference.country.CountryService;
import com.decagon.decapay.service.reference.language.LanguageService;
import com.decagon.decapay.utils.ApiResponseUtil;
import com.decagon.decapay.utils.ConverterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "References Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class ReferencesController {

    private final LanguageService languageService;
    private final CountryService countryService;
    private final CurrencyService currencyService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Country, Language, Currency References", description = "Country, Language, Currency References")
    @GetMapping("/references")
    public ResponseEntity<ApiDataResponse<UserSettingReferencesDto>> listReferences() {

        UserSettingReferencesDto referencesDto = new UserSettingReferencesDto();

        List<Language> languagesList = languageService.getLanguages();
        referencesDto.setLanguages(ConverterUtil.convertLanguages(languagesList));

        List<Country> countryList = countryService.listCountries();
        referencesDto.setCountries(ConverterUtil.convertCountries(countryList));

        List<Currency> currencies = currencyService.findAllByOrderByName();
        referencesDto.setCurrencies(ConverterUtil.convertCurrency(currencies));

        return ApiResponseUtil.response(HttpStatus.OK, referencesDto, RESOURCE_RETRIEVED_SUCCESSFULLY);
    }

}
