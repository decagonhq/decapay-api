package com.decagon.decapay.integration.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class UserControllerTest {

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

	UserDTO userDTO;

	ObjectMapper objectMapper = new ObjectMapper();

	@Value("${api.basepath-api}")
	private String path;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
	}


	@Test
	void givenNoUserSettingsExists_WhenUserCreateAccountWithInvalidUserSettingRequestData_ShouldReturnInvalidRequest() throws Exception {
		userDTO.setCurrencyCode("KKKK");
		userDTO.setLanguageCode("KKK");
		userDTO.setCountryCode("KKK");

		mockMvc.perform(
				post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
						objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().is(400));
	}

	@Test
	void givenNoUserSettingsExists_WhenUserCreateAccountWithNonExistingUserSettingRequestCountry_ShouldReturnResourceNotFound() throws Exception {

		Currency currency = new Currency();
		java.util.Currency c = java.util.Currency.getInstance("BBD");
		currency.setCode(c.getCurrencyCode());
		currency.setName(c.getDisplayName());
		currency.setCurrency(c);
		currencyRepository.save(currency);

		Language language = new Language();
		language.setTitle("English");
		language.setCode("en");
		languageRepository.save(language);

		userDTO.setCountryCode("ZZ");
		userDTO.setLanguageCode("en");
		userDTO.setCurrencyCode("BBD");

		mockMvc.perform(
				post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
						objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for country with code " + userDTO.getCountryCode()))
				.andDo(print()).andDo(print());

	}

	@Test
	void givenNoUserSettingsExists_WhenUserCreateAccountWithNonExistingUserSettingRequestPreferredLanguage_ShouldReturnResourceNotFound() throws Exception {

		Country country = new Country();
		country.setName("Nigeria");
		country.setIsoCode("NG");
		countryRepository.save(country);

		Currency currency = new Currency();
		java.util.Currency c = java.util.Currency.getInstance("USD");
		currency.setCode(c.getCurrencyCode());
		currency.setName(c.getDisplayName());
		currency.setCurrency(c);
		currencyRepository.save(currency);

		userDTO.setCountryCode("NG");
		userDTO.setCurrencyCode("USD");

		userDTO.setLanguageCode("mm");

		mockMvc.perform(
						post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
								objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for language with code " + userDTO.getLanguageCode()))
				.andDo(print()).andDo(print());
	}

	@Test
	void givenNoUserSettingsExists_WhenUserCreateAccountWithNonExistingUserSettingRequestPreferredCurrency_ShouldReturnResourceNotFound() throws Exception {

		Country country = new Country();
		country.setName("Ghana");
		country.setIsoCode("GN");
		countryRepository.save(country);

		Language language = new Language();
		language.setTitle("Germany");
		language.setCode("gm");
		languageRepository.save(language);

		userDTO.setCountryCode("GN");
		userDTO.setLanguageCode("gm");
		userDTO.setCurrencyCode("CUP");

		mockMvc.perform(
						post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
								objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for currency with code " + userDTO.getCurrencyCode()))
				.andDo(print()).andDo(print());
	}


	@Test
	void registerUser() throws Exception {

		Country country = new Country();
		country.setName("France");
		country.setIsoCode("FR");
		countryRepository.save(country);

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

		userDTO.setCountryCode("FR");
		userDTO.setCurrencyCode("GMD");
		userDTO.setLanguageCode("au");

		ResultActions response = mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(userDTO))).andExpect(status().is(201))
				.andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)));

		User user = userRepository.findByEmail(userDTO.getEmail()).get();


		assertEquals("firstName", user.getFirstName());
		assertEquals("lastName", user.getLastName());
		assertTrue(passwordEncoder.matches("Password1!", user.getPassword()));
		assertEquals("a@b.com", user.getEmail());
		assertEquals("0123456789", user.getPhoneNumber());
		assertNotNull(user.getId());

		UserSettings settings = new UserSettings();
		settings.setLanguage("au");
		settings.setCountryCode("FR");
		settings.setCurrencyCode("GMD");

		assertEquals(settings.toJSONString(), user.getUserSetting());
	}

	@Test
	void registerUserFailsWithIncompleteDTO() throws Exception {
		mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new UserDTO()))).andExpect(status().is(400));
	}

	@Test
	void registerUserFailsWhenUserAlreadyExists() throws Exception {

		User user = new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("a@b.com");
		user.setPassword("Password1!");
		user.setPhoneNumber("0123456789");
		userRepository.save(user);

		mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(userDTO))).andExpect(status().is(409));
	}
}
