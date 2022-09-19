package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.UserDTO;
import com.decagon.decapay.dto.user.UserResponseDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.user.UserPopulator;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.UserInfoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public IdResponseDto registerUser(final UserDTO userDTO) throws ResourceConflictException {

        if (userRepository.findByEmail(userDTO.getEmail().toLowerCase()).isPresent()) {
            throw new ResourceConflictException();
        }

        Country country = countryRepository.findByIsoCode(userDTO.getCountryCode());
        if (country == null){
            throw new ResourceNotFoundException("Resource not found for country with code " + userDTO.getCountryCode());
        }

        Language language = languageRepository.findByCode(userDTO.getLanguageCode());
        if (language == null){
            throw new ResourceNotFoundException("Resource not found for language with code " + userDTO.getLanguageCode());
        }

        Currency currency = currencyRepository.getByCode(userDTO.getCurrencyCode());
        if (currency == null){
            throw new ResourceNotFoundException("Resource not found for currency with code " + userDTO.getCurrencyCode());
        }

        User user = this.createModelEntity(userDTO);
        return new IdResponseDto(userRepository.save(user).getId());
    }

    private User createModelEntity(UserDTO userDTO) {
        User user = new User();
        UserPopulator userAccountPopulator = new UserPopulator(passwordEncoder);
        userAccountPopulator.setObjectMapper(objectMapper);
        userAccountPopulator.populate(userDTO, user);
        return user;
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
}
