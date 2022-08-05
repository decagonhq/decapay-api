package com.decagon.decapay.service.currency;

import java.math.BigDecimal;

public interface CurrencyService {
    String formatAmount(BigDecimal amount);
}
