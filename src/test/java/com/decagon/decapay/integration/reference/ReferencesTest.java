package com.decagon.decapay.integration.reference;

import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class ReferencesTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	LanguageRepository languageRepository;

	@Autowired
	CurrencyRepository currencyRepository;

	@Autowired
	UserService userService;

	@Value("${api.basepath-api}")
	private String path;


	@Test
	void givenNoReferencesExist_WhenUserCreateAccount_ShouldReturnEmptyReferences() throws Exception {

		mockMvc.perform(get(path + "/references").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data.countries.size()").value(0))
				.andExpect(jsonPath("$.data.languages.size()").value(0))
				.andExpect(jsonPath("$.data.currencies.size()").value(0));
	}

	@Test
	void givenReferencesExist_WhenUserCreateAccount_ShouldReturnReferences() throws Exception {

		Country country = new Country();
		country.setName("France");
		country.setIsoCode("FR");

		Country country2 = new Country();
		country2.setName("Nigeria");
		country2.setIsoCode("NG");
		countryRepository.saveAll(List.of(country,country2));

		Language language = new Language();
		language.setTitle("Austria");
		language.setCode("au");
		languageRepository.save(language);

		Currency currency = new Currency();
		java.util.Currency c = java.util.Currency.getInstance("GMD");
		currency.setCode(c.getCurrencyCode());
		currency.setName(c.getDisplayName());
		currency.setCurrency(c);
		currencyRepository.save(currency);

		mockMvc.perform(get(path + "/references").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data.countries.size()").value(2))
				.andExpect(jsonPath("$.data.languages.size()").value(1))
				.andExpect(jsonPath("$.data.currencies.size()").value(1));
	}

}
