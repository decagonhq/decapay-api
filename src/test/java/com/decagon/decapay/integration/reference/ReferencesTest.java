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
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

		Country country2 = new Country();
		country2.setName("Nigeria");
		country2.setIsoCode("NG");

		Country country = new Country();
		country.setName("France");
		country.setIsoCode("FR");

		countryRepository.saveAll(List.of(country,country2));

		Language language2 = new Language();
		language2.setTitle("French");
		language2.setCode("fr");

		Language language = new Language();
		language.setTitle("English");
		language.setCode("en");

		languageRepository.saveAll(List.of(language,language2));

		Currency currency = new Currency();
		java.util.Currency c = java.util.Currency.getInstance("GMD");
		currency.setCode(c.getCurrencyCode());
		currency.setName(c.getDisplayName());
		currency.setCurrency(c);

		Currency currency2 = new Currency();
		c = java.util.Currency.getInstance("GBP");
		currency2.setCode(c.getCurrencyCode());
		currency2.setName(c.getDisplayName());
		currency2.setCurrency(c);
		currencyRepository.saveAll(List.of(currency,currency2));

		mockMvc.perform(get(path + "/references").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data.countries.size()").value(2))
				.andExpect(jsonPath("$.data.countries[*].code", Matchers.containsInRelativeOrder("FR","NG")))
				.andExpect(jsonPath("$.data.countries[0].code").value("FR"))
				.andExpect(jsonPath("$.data.countries[0].name").value("France"))
				.andExpect(jsonPath("$.data.languages.size()").value(2))
				.andExpect(jsonPath("$.data.languages[*].code", Matchers.containsInRelativeOrder("en","fr")))
				.andExpect(jsonPath("$.data.languages[0].code").value("en"))
				.andExpect(jsonPath("$.data.languages[0].name").value("English"))
				.andExpect(jsonPath("$.data.currencies.size()").value(2))
				.andExpect(jsonPath("$.data.currencies[*].code", Matchers.containsInRelativeOrder("GBP","GMD")))
				.andExpect(jsonPath("$.data.currencies[0].code").value("GBP"))
				.andExpect(jsonPath("$.data.currencies[0].name").value(currency2.getName()))
				.andDo(print());
	}

}
