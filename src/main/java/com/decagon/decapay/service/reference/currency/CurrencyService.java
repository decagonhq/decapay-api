package com.decagon.decapay.service.reference.currency;

import com.decagon.decapay.model.reference.currency.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface CurrencyService {
    String formatAmount(BigDecimal amount);

    String formatAmount(BigDecimal amount, Locale locale, java.util.Currency currency);

    void create(Currency currency);

    boolean existCurrencies();

    List<Currency> findAllByOrderByName();
}
