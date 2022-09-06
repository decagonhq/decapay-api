package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.reference.country.Country;
import com.decagon.decapay.model.reference.currency.Currency;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.UserAccountPopulator;
import com.decagon.decapay.populator.UserSettingPopulator;
import com.decagon.decapay.repositories.reference.currency.CurrencyRepository;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import com.decagon.decapay.repositories.reference.zone.country.CountryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
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

    public UserServiceImpl(UserRepository userRepository, CountryRepository countryRepository, CurrencyRepository currencyRepository,
                           LanguageRepository languageRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.currencyRepository = currencyRepository;
        this.languageRepository = languageRepository;
        this.passwordEncoder = passwordEncoder;
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
        UserAccountPopulator userAccountPopulator = new UserAccountPopulator(passwordEncoder);
        userAccountPopulator.populate(userDTO, user);
        return user;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }
}
