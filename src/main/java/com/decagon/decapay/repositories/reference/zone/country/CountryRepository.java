package com.decagon.decapay.repositories.reference.zone.country;


import com.decagon.decapay.model.reference.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country,Long> {
	
	Country findByIsoCode(String code);

	List<Country> findAllByOrderByName();
}
