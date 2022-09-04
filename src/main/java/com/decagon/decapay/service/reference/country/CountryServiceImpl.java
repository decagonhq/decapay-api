package com.decagon.decapay.service.reference.country;

import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("countryService")
public class CountryServiceImpl implements CountryService {

	private final CountryRepository countryRepository;

	public CountryServiceImpl(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}
	
	public Country getByCode(String code) {
		return countryRepository.findByIsoCode(code);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Country> listCountries(){

	return countryRepository.findAllByOrderByName();
	}
	@Override
	public boolean existCountries() {
		return this.countryRepository.count()>0;
	}

	@Override
	public void create(Country country) {
       this.countryRepository.save(country);
	}


}
