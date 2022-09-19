package com.decagon.decapay.integration.user;

import com.decagon.decapay.config.userSetting.UserSettings;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
