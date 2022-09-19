package com.decagon.decapay.integration.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.UserDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class UserProfileTest {

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

	ObjectMapper objectMapper = new ObjectMapper();

	@Value("${api.basepath-api}")
	private String path;


	private UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");

	private void setAuthHeader(User user){
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtUtil.generateToken(userDetails);
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + token);
	}


	@Test
	void givenUserProfileExist_WhenUserViewProfile_ShouldReturnUserProfileSuccessfully() throws Exception {

		User user = TestModels.user("ola", "dip", "ola@gmail.com",
				passwordEncoder.encode("password"), "08067644805");
		user.setUserStatus(UserStatus.ACTIVE);
		user.setUserSetting(objectMapper.writeValueAsString(userSettings));
		userRepository.save(user);

		setAuthHeader(user);

		this.mockMvc.perform(get(path + "/profile")
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

		this.mockMvc.perform(put(path + "/profile")
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

		this.mockMvc.perform(put(path + "/profile")
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

		this.mockMvc.perform(put(path + "/profile")
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

		this.mockMvc.perform(put(path + "/profile")
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
