package com.decagon.decapay.repositories.reference.currency;

import com.decagon.decapay.model.reference.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

	Currency getByCode(String code);

	List<Currency> findAllByOrderByName();
}
