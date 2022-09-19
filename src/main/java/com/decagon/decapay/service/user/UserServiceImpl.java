package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDto;
import com.decagon.decapay.dto.SignUpRequestDTO;
import com.decagon.decapay.dto.UserResponseDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.UserAccountPopulator;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.UserInfoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final UserInfoUtil userInfoUtil;

    public UserServiceImpl(UserRepository userRepository, CountryRepository countryRepository, CurrencyRepository currencyRepository,
                           LanguageRepository languageRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, UserInfoUtil userInfoUtil) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.currencyRepository = currencyRepository;
        this.languageRepository = languageRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper=objectMapper;
        this.userInfoUtil = userInfoUtil;
    }

    @Override
    public IdResponseDto registerUser(final SignUpRequestDTO signUpRequestDTO) throws ResourceConflictException {

        if (userRepository.findByEmail(signUpRequestDTO.getEmail().toLowerCase()).isPresent()) {
            throw new ResourceConflictException();
        }

        Country country = countryRepository.findByIsoCode(signUpRequestDTO.getCountryCode());
        if (country == null){
            throw new ResourceNotFoundException("Resource not found for country with code " + signUpRequestDTO.getCountryCode());
        }

        Language language = languageRepository.findByCode(signUpRequestDTO.getLanguageCode());
        if (language == null){
            throw new ResourceNotFoundException("Resource not found for language with code " + signUpRequestDTO.getLanguageCode());
        }

        Currency currency = currencyRepository.getByCode(signUpRequestDTO.getCurrencyCode());
        if (currency == null){
            throw new ResourceNotFoundException("Resource not found for currency with code " + signUpRequestDTO.getCurrencyCode());
        }

        User user = this.createModelEntity(signUpRequestDTO);
        return new IdResponseDto(userRepository.save(user).getId());
    }

    private User createModelEntity(SignUpRequestDTO signUpRequestDTO) {
        User user = new User();
        this.populateUserModel(signUpRequestDTO, user);
        return user;
    }

    private void populateUserModel(UserDto dto, User user){
        UserAccountPopulator userAccountPopulator = new UserAccountPopulator(passwordEncoder);
        userAccountPopulator.setObjectMapper(objectMapper);
        userAccountPopulator.populate(dto, user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    @Override
    public UserSettings getUserSettings() {

        return null;
    }

    @Override
    public UserResponseDto viewUserProfile() {
        User currentUser = this.userInfoUtil.getCurrAuthUser();
        return UserResponseDto.builder()
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .email(currentUser.getEmail())
                .phoneNumber(currentUser.getPhoneNumber())
                .build();
    }

    @Transactional
    @Override
    public User updateUserProfile(UserDto userDto) {
        User currentUser = this.userInfoUtil.getCurrAuthUser();
        if (userRepository.findByEmail(userDto.getEmail().toLowerCase()).isPresent()) {
            throw new ResourceConflictException();
        }
        this.populateUserModel(userDto, currentUser);
        return currentUser;
    }
}
