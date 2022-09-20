package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.UserDto;
import com.decagon.decapay.dto.user.UserResponseDto;
import com.decagon.decapay.dto.user.ChangePasswordRequestDto;
import com.decagon.decapay.dto.user.SignUpRequestDTO;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.InvalidRequestException;
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
import com.decagon.decapay.service.auth.TokenBlacklistService;
import com.decagon.decapay.utils.UserInfoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
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
    private final TokenBlacklistService tokenBlacklistService;

    public UserServiceImpl(UserRepository userRepository, CountryRepository countryRepository, CurrencyRepository currencyRepository,
                           LanguageRepository languageRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, UserInfoUtil userInfoUtil, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.currencyRepository = currencyRepository;
        this.languageRepository = languageRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper=objectMapper;
        this.userInfoUtil = userInfoUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public IdResponseDto registerUser(final SignUpRequestDTO signUpRequestDTO) throws ResourceConflictException {

        if (this.userEmailTaken(signUpRequestDTO.getEmail())) {
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
        UserPopulator userPopulator = new UserPopulator(passwordEncoder);
        userPopulator.setObjectMapper(objectMapper);
        userPopulator.populate(dto, user);
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

        if (this.userEmailTaken(userDto.getEmail(),currentUser.getId())) {
            throw new ResourceConflictException();
        }
        this.populateUserModel(userDto, currentUser);
        return currentUser;
    }

    @Override
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto, String token) {

        User currentUser = this.userInfoUtil.getCurrAuthUser();

        if (!this.checkIfOldPasswordIsValid(changePasswordRequestDto.getPassword(), currentUser.getPassword())) {
            throw new InvalidRequestException("Invalid old password");
        }

        this.saveNewPassword(currentUser, changePasswordRequestDto.getNewPassword());

        this.invalidateToken(token);
    }

    private void saveNewPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    private void invalidateToken(String token) {
        this.tokenBlacklistService.blackListToken(token);
    }

    private boolean checkIfOldPasswordIsValid(final String oldPassword, final String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }

    private boolean userEmailTaken(String email) {
        return this.userRepository.existsByEmail(email);
    }

    /**
     * use to check if user email exist on editing by excluding the user from
     * the query using the user id.
     * @param email
     * @param id
     * @return
     */
    private boolean userEmailTaken(String email, Long id) {
        return this.userRepository.existsByEmailAndIdNot(email, id);
    }




}
