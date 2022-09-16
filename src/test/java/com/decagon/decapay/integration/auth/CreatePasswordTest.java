package com.decagon.decapay.integration.auth;


import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.auth.CreatePasswordRequestDto;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.VerifyPasswordResetCodeRequest;
import com.decagon.decapay.model.auth.PasswordReset;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.PASSWORD_CREATED_SUCCESSFULLY;
import static com.decagon.decapay.model.auth.ResetCodeStatus.UNVERIFIED;
import static com.decagon.decapay.model.auth.ResetCodeStatus.VERIFIED;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class CreatePasswordTest {
    @Value("${api.basepath-api}")
    private String path;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private PasswordResetRepository passwordResetRepository;
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

    private HttpHeaders addMobileIdToHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(DEVICE_KEY_HEADER, MOBILE_DEVICE_ID);
        return headers;
    };

    private HttpHeaders addWebIdToHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(DEVICE_KEY_HEADER, WEB_DEVICE_ID);
        return headers;
    };

    @Test
    void shouldThrowInvalidRequestForMobileDeviceWhenCreateNewPasswordWithNoNewPasswordProvided() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password", "08137640746");
        this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(MOBILE_DEVICE_ID);
        this.passwordResetRepository.save(passwordReset);

        //input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("", "password", "3215");

        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowInvalidRequestForWebDeviceWhenCreateNewPasswordWithNoNewPasswordProvided() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password", "08137640746");
        this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setExpiredAt(LocalDateTime.now().minusDays(1));
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        this.passwordResetRepository.save(passwordReset);

        //input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("", "password", "3215");

        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreateNewPasswordAndPasswordResetTokenDoesNotExist() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("password", "password", "1234");

        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowInvalidRequestWhenCreateNewPasswordAndPasswordResetTokenIsExpired() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password", "08137640746");
        this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setExpiredAt(LocalDateTime.now().minusDays(1));
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        this.passwordResetRepository.save(passwordReset);

        //input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("password", "password", "3215");

        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowInvalidRequestWhenCreateNewPasswordAndPasswordResetCodeStatusIsNotVerified() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setExpiredAt(LocalDateTime.now().plusDays(1));
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        passwordReset.setStatus(UNVERIFIED);
        this.passwordResetRepository.save(passwordReset);


        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("password", "password", "3215");

        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateNewPasswordForMobileDeviceSuccessfully() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(MOBILE_DEVICE_ID);
        passwordReset.setStatus(VERIFIED);
        passwordReset = this.passwordResetRepository.save(passwordReset);

        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("change", "change", "3215");

        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_CREATED_SUCCESSFULLY));

        //confirm that the password has been changed and user can sign in with the new password
        user = this.userRepository.findByEmail(user.getEmail()).get();
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user.getEmail());
        loginDto.setPassword("change");


        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/signin").content(TestUtils.asJsonString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void shouldCreateNewPasswordForWebDeviceSuccessfully() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        passwordReset = this.passwordResetRepository.save(passwordReset);

        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("change", "change", "3215");

        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_CREATED_SUCCESSFULLY));

        //confirm that the password has been changed and user can sign in with the new password
        user = this.userRepository.findByEmail(user.getEmail()).get();
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user.getEmail());
        loginDto.setPassword("change");


        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/signin").content(TestUtils.asJsonString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void shouldInvalidateResetCodeForMobileDeviceWhenCreateNewPasswordIsSuccessful() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(MOBILE_DEVICE_ID);
        passwordReset.setStatus(VERIFIED);
        passwordReset = this.passwordResetRepository.save(passwordReset);

        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("change", "change", "3215");

        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_CREATED_SUCCESSFULLY));

        // assert that the password reset token cannot be used anymore
        VerifyPasswordResetCodeRequest dto2 = new VerifyPasswordResetCodeRequest("fabiane@decagonhq.com","3215");
        //act
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/verify-code").content(TestUtils.asJsonString(dto2))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldInvalidateResetTokenForWebDeviceWhenCreateNewPasswordIsSuccessful() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(WEB_DEVICE_ID);
        passwordReset = this.passwordResetRepository.save(passwordReset);

        ///input
        CreatePasswordRequestDto dto = new CreatePasswordRequestDto("change", "change", "3215");

        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_CREATED_SUCCESSFULLY));

        passwordReset = this.passwordResetRepository.findByEmailAndDeviceId(user.getEmail(), WEB_DEVICE_ID).get();

        //should not be able to use same reset token twice
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/reset-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


}
