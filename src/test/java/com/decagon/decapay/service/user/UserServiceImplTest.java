package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private LanguageRepository languageRepository;

	@Mock
	private CurrencyRepository currencyRepository;

	@Mock
	BCryptPasswordEncoder passwordEncoder;

	private Country country;
	private Language language;
	private Currency currency;

	@InjectMocks
	UserServiceImpl userService;

	UserDTO userDTO;

	User user;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
        user=new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("a@b.com");
		user.setPassword("Password1!");
		user.setPhoneNumber("0123456789");

		country = new Country();
		country.setName("France");
		country.setIsoCode("FR");
		countryRepository.save(country);

		language = new Language();
		language.setTitle("Austria");
		language.setCode("au");
		languageRepository.save(language);

		currency = new Currency();
		java.util.Currency c = java.util.Currency.getInstance("GMD");
		currency.setCode(c.getCurrencyCode());
		currency.setName(c.getDisplayName());
		currency.setCurrency(c);
		currencyRepository.save(currency);

	}

	@Test
	@DisplayName("Should return user already exist response if email already registered")
	void whenRegisteringWithExistingEmail() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
		assertThrows(ResourceConflictException.class, () -> userService.registerUser(userDTO));
	}


	@Test
	@DisplayName("Should register user successfully")
	void registerUser() {

		UserDTO userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
		userDTO.setCountryCode("FR");
		userDTO.setCurrencyCode("GMD");
		userDTO.setLanguageCode("au");
		AtomicReference<User> savedUser=new AtomicReference<>();
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		when(this.countryRepository.findByIsoCode(userDTO.getCountryCode())).thenReturn(country);
		when(this.languageRepository.findByCode(userDTO.getLanguageCode())).thenReturn(language);
		when(this.currencyRepository.getByCode(userDTO.getCurrencyCode())).thenReturn(currency);
		when(this.passwordEncoder.encode(anyString())).thenReturn("encodedPass");
		when(userRepository.save(any())).thenReturn(user);
		when(this.userRepository.save(any())).thenAnswer((Answer<User>) i -> {
			savedUser.set(i.getArgument(0));
			savedUser.get().setId(1L);
			return savedUser.get();
		});
		IdResponseDto idResponseDto = userService.registerUser(userDTO);
		User user1=savedUser.get();

		assertEquals(idResponseDto.getId(),user1.getId());
		assertEquals("firstName", user1.getFirstName());
		assertEquals("lastName", user1.getLastName());
		assertEquals("encodedPass", user1.getPassword());
		assertEquals("a@b.com", user1.getEmail());
		assertEquals("0123456789", user1.getPhoneNumber());

		UserSettings settings = new UserSettings();
		settings.setLanguage("au");
		settings.setCountryCode("FR");
		settings.setCurrencyCode("GMD");

		//assertEquals(settings.toJSONString(), user1.getUserSetting());

	}
}