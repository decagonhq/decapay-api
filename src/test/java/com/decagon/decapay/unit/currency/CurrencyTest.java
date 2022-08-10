package com.decagon.decapay.unit.currency;


import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.service.currency.CurrencyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class CurrencyTest {
    @InjectMocks
    CurrencyServiceImpl currencyService;


    @Test
    void testShouldFormatAmountSuccessfullyUsingDefaultCurrency() throws Exception {

        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
        Currency currency = Currency.getInstance("NGN");
        BigDecimal amount = BigDecimal.valueOf(200000);
        String formattedAmount = this.currencyService.formatAmount(amount);
        assertEquals(currency.getSymbol(locale) + "200,000.00", formattedAmount);
    }

    @Test
    void testShouldFormatZeroAmountSuccessfullyUsingDefaultCurrency() throws Exception {

        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
        Currency currency = Currency.getInstance("NGN");
        BigDecimal amount = BigDecimal.ZERO;
        String formattedAmount = this.currencyService.formatAmount(amount);
        assertEquals(currency.getSymbol(locale) + "0.00", formattedAmount);
    }


    @ParameterizedTest
    @MethodSource("amountProvider")
    void testShouldFormatAmountSuccessfullyUsingDefaultCurrency(BigDecimal amount, String expectedFormattedAmount) throws Exception {
        String formattedAmount = this.currencyService.formatAmount(amount);
        assertEquals(expectedFormattedAmount, formattedAmount);
    }

    static Stream<Arguments> amountProvider() {
        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
        Currency currency = Currency.getInstance("NGN");
        return Stream.of(
                arguments(BigDecimal.valueOf(2), currency.getSymbol(locale) + "2.00"),
                arguments(BigDecimal.valueOf(200), currency.getSymbol(locale) + "200.00"),
                arguments(BigDecimal.valueOf(2000), currency.getSymbol(locale) + "2,000.00"),
                arguments(BigDecimal.valueOf(20000), currency.getSymbol(locale) + "20,000.00"),
                arguments(BigDecimal.ZERO, currency.getSymbol(locale) + "0.00"),
                arguments(null, currency.getSymbol(locale) + "0.00")
        );
    }


}