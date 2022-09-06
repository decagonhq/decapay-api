package com.decagon.decapay.service.reference.country;


import com.decagon.decapay.model.reference.country.Country;
import java.util.List;

public interface CountryService  {

	Country getByCode(String code);
	List<Country> listCountries();
	boolean existCountries();
	void create(Country country);
}
