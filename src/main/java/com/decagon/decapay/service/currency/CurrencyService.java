package com.decagon.decapay.service.currency;

import com.decagon.decapay.model.reference.currency.Currency;

import java.math.BigDecimal;

public interface CurrencyService {
    String formatAmount(BigDecimal amount);

    void create(Currency currency);

    boolean existCurrencies();
}
