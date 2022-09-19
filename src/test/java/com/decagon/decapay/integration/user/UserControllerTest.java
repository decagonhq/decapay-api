package com.decagon.decapay.integration.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDto;
import com.decagon.decapay.dto.SignUpRequestDTO;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	private HttpHeaders headers;

	SignUpRequestDTO signUpRequestDTO;

	ObjectMapper objectMapper = new ObjectMapper();

	@Value("${api.basepath-api}")
	private String path;

	@BeforeEach
	void setUp() {
		signUpRequestDTO = new SignUpRequestDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
	}

	private UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");

	private void setAuthHeader(User user){
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtUtil.generateToken(userDetails);
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + token);
	}

	@Test
	void givenNoUserSettingsExists_WhenUserCreateAccountWithInvalidUserSettingRequestData_ShouldReturnInvalidRequest() throws Exception {
		signUpRequestDTO.setCurrencyCode("KKKK");
		signUpRequestDTO.setLanguageCode("KKK");
		signUpRequestDTO.setCountryCode("KKK");

		mockMvc.perform(
				post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
						objectMapper.writeValueAsString(signUpRequestDTO)))
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

		signUpRequestDTO.setCountryCode("ZZ");
		signUpRequestDTO.setLanguageCode("en");
		signUpRequestDTO.setCurrencyCode("BBD");

		mockMvc.perform(
				post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
						objectMapper.writeValueAsString(signUpRequestDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for country with code " + signUpRequestDTO.getCountryCode()))
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

		signUpRequestDTO.setCountryCode("NG");
		signUpRequestDTO.setCurrencyCode("USD");

		signUpRequestDTO.setLanguageCode("mm");

		mockMvc.perform(
						post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
								objectMapper.writeValueAsString(signUpRequestDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for language with code " + signUpRequestDTO.getLanguageCode()))
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

		signUpRequestDTO.setCountryCode("GN");
		signUpRequestDTO.setLanguageCode("gm");
		signUpRequestDTO.setCurrencyCode("CUP");

		mockMvc.perform(
						post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
								objectMapper.writeValueAsString(signUpRequestDTO)))
				.andExpect(status().is(404))
				.andExpect(jsonPath("$.message").value("Resource not found for currency with code " + signUpRequestDTO.getCurrencyCode()))
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

		signUpRequestDTO.setCountryCode("FR");
		signUpRequestDTO.setCurrencyCode("GMD");
		signUpRequestDTO.setLanguageCode("au");

		ResultActions response = mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(signUpRequestDTO))).andExpect(status().is(201))
				.andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)));

		User user = userRepository.findByEmail(signUpRequestDTO.getEmail()).get();


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

		assertEquals(objectMapper.writeValueAsString(settings), user.getUserSetting());
	}

	@Test
	void registerUserFailsWithIncompleteDTO() throws Exception {
		mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new SignUpRequestDTO()))).andExpect(status().is(400));
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
				objectMapper.writeValueAsString(signUpRequestDTO))).andExpect(status().is(409));
	}


	@Test
	void givenUserProfileExist_WhenUserViewProfile_ShouldReturnUserProfileSuccessfully() throws Exception {

		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		user.setUserSetting(objectMapper.writeValueAsString(userSettings));
		userRepository.save(user);

		setAuthHeader(user);

		this.mockMvc.perform(get(path + "/user")
						.contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.firstName").value("ola"))
				.andExpect(jsonPath("$.data.lastName").value("dip"))
				.andExpect(jsonPath("$.data.email").value("ola@gmail.com"))
				.andExpect(jsonPath("$.data.phoneNumber").value("08067644805"));
	}

	@Test
	void givenUserProfileExist_WhenUserUpdateProfileWithInvalidData_ShouldReturn400() throws Exception {
		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		userRepository.save(user);

		UserDto dto = new UserDto();
		dto.setFirstName("");
		dto.setLastName("");
		dto.setEmail("og");
		dto.setPhoneNumber("070");

		setAuthHeader(user);

		this.mockMvc.perform(put(path + "/user/edit")
						.content(TestUtils.asJsonString(dto))
						.contentType(MediaType.APPLICATION_JSON).headers(headers))
				.andExpect(status().isBadRequest());
	}

	@Test
	void givenUserProfileExist_WhenUserUpdateProfileWithValidDataButExistingEmail_shouldReturn409() throws Exception {
		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		userRepository.save(user);

		User user2 = TestModels.user("king", "john", "og@gmail.com",
				passwordEncoder.encode("password"), "08067655805");
		user.setUserStatus(UserStatus.ACTIVE);
		userRepository.save(user2);


		UserDto dto = new UserDto();
		dto.setFirstName("Goodluck");
		dto.setLastName("Nwoko");
		dto.setEmail("og@gmail.com");
		dto.setPhoneNumber("07056755667");

		setAuthHeader(user);

		this.mockMvc.perform(put(path + "/user/edit")
						.content(TestUtils.asJsonString(dto))
						.contentType(MediaType.APPLICATION_JSON).headers(headers))
				.andExpect(status().isConflict());
	}

	@Test
	void givenUserProfileExist_WhenUserUpdateProfileWithValidDataButWExistingEmail_ShouldUpdateProfileSuccessfully() throws Exception {
		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		userRepository.save(user);


		UserDto dto = new UserDto();
		dto.setFirstName("Goodluck");
		dto.setLastName("Nwoko");
		dto.setEmail("ola@gmail.com");
		dto.setPhoneNumber("07056755667");

		setAuthHeader(user);

		this.mockMvc.perform(put(path + "/user/edit")
						.content(TestUtils.asJsonString(dto))
						.contentType(MediaType.APPLICATION_JSON).headers(headers))
				.andExpect(status().isOk());
	}

	@Test
	void givenUserProfileExist_WhenUserUpdateProfileWithValidData_ShouldUpdateProfileSuccessfully() throws Exception {
		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		userRepository.save(user);

		UserDto dto = new UserDto();
		dto.setFirstName("Goodluck");
		dto.setLastName("Nwoko");
		dto.setEmail("og@gmail.com");
		dto.setPhoneNumber("07056755667");

		setAuthHeader(user);

		this.mockMvc.perform(put(path + "/user/edit")
						.content(TestUtils.asJsonString(dto))
						.contentType(MediaType.APPLICATION_JSON).headers(headers))
				.andExpect(status().isOk());

		User updatedUser = userRepository.findByEmail("og@gmail.com").get();
		assertEquals(updatedUser.getEmail(), dto.getEmail());
		assertEquals(updatedUser.getFirstName(), dto.getFirstName());
		assertEquals(updatedUser.getLastName(), dto.getLastName());
		assertEquals(updatedUser.getPhoneNumber(), dto.getPhoneNumber());
	}
}
