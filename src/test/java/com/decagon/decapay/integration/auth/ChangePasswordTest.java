package com.decagon.decapay.integration.auth;


import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.model.auth.PasswordReset;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.PASSWORD_CHANGED_SUCCESSFULLY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class ChangePasswordTest {
    @Value("${api.basepath-api}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;
    private HttpHeaders headers;
    private final UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");

    @BeforeAll
    public void setup() {
    }


    @BeforeEach
    public void setUpBeforeEach() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private User createUser() throws JsonProcessingException {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        return userRepository.save(user);
    }

    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    private void addWebIdToHeaders(){
        headers.add(DEVICE_KEY_HEADER, WEB_DEVICE_ID);
    };


    @Test
    void givenAUserExist_WhenUserChangePasswordWithInvalidData_SystemShouldFailWith400() throws Exception {
        //given
        User user = createUser();
        //when
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("","","");

        //then
        this.setAuthHeader(user);
        this.addWebIdToHeaders();

        this.validateExpectation(dto, status().isBadRequest());
    }

    @Test
    void givenAUserExist_WhenUserChangePasswordWithValidDataButWrongPassword_SystemShouldFailWith400() throws Exception {
        //given
        User user = createUser();
        //when
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("oldPassword","newPassword","newPassword");

        //then
        this.setAuthHeader(user);
        this.addWebIdToHeaders();

        this.validateExpectation(dto, status().isBadRequest());
    }

    @Test
    void givenAUserExist_WhenUserChangePasswordWithValidDataButMisMatchedPassword_SystemShouldFailWith400() throws Exception {
        //given
        User user = createUser();
        //when
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("oldPassword","newPassword","newPassword1");

        //then
        this.setAuthHeader(user);
        this.addWebIdToHeaders();

        this.validateExpectation(dto, status().isBadRequest());
    }

    @Test
    void givenAUserExist_WhenUserChangePasswordWithValidData_SystemShouldChangeUserPasswordSuccessfully() throws Exception {
        //given
        User user = createUser();

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        passwordReset = this.passwordResetRepository.save(passwordReset);

        //when
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("password","newPassword","newPassword");

        //then
        this.setAuthHeader(user);
        this.addWebIdToHeaders();

        this.validateExpectation(dto, status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_CHANGED_SUCCESSFULLY));

        //assert user can't log in with old password
        LoginDto loginRequest = new LoginDto();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(dto.getPassword());

        this.mockMvc.perform(post(path + "/signin").content(TestUtils.asJsonString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.token").doesNotHaveJsonPath());

        //assert user can log in with new password
        loginRequest = new LoginDto();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(dto.getNewPassword());

        this.mockMvc.perform(post(path + "/signin").content(TestUtils.asJsonString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    private ResultActions validateExpectation(ChangePasswordRequestDto dto, ResultMatcher expectedResult) throws Exception {
        return this.mockMvc.perform(post(path + "/change-password")
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(expectedResult);
    }
}
