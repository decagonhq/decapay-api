package com.decagon.decapay.integration.auth;


import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class PasswordResetTest {

    @Value("${api.basepath-api}")
    private String path;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordResetRepository repository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setup() {
    }

    private HttpHeaders headers;

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
    void shouldPublishForgotPasswordEmailForWebUserSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        //input
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("fabiane@decagonhq.com");
        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var passwordReset = this.repository.findByEmail(user.getEmail()).get();
        assertNotNull(passwordReset.getToken());
        assertEquals(WEB_DEVICE_ID, passwordReset.getDeviceId());
        assertTrue(passwordReset.getExpiredAt().isAfter(LocalDateTime.now()));

    }

    @Test
    void shouldPublishForgotPasswordEmailForMobileUserSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        //input
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("fabiane@decagonhq.com");
        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var passwordReset = this.repository.findByEmail(user.getEmail()).get();
        assertNotNull(passwordReset.getToken());
        assertEquals(MOBILE_DEVICE_ID, passwordReset.getDeviceId());
        assertTrue(passwordReset.getExpiredAt().isAfter(LocalDateTime.now()));

    }

    @Test
    void shouldThrowInvalidRequestWhenTryingToPublishForgotPasswordWithNoEmailPresent() throws Exception {

        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        //input
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("");
        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldThrowUserNotFoundWhenTryingToPublishForgotPasswordAndUserDoesNotExist() throws Exception {

        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("fabiane@decagonhq.com");
        //act
        headers = this.addWebIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTryingToPublishForgotPasswordAndDeviceIdDoesNotExist() throws Exception {

        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("fabiane@decagonhq.com");
        //act
        headers.add(DEVICE_KEY_HEADER, "");
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePasswordResetTokenWhenPublishingForgotPasswordEmailAndTokenAlreadyExist() throws Exception {
        headers = this.addWebIdToHeaders();
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        //input
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto("fabiane@decagonhq.com");
        //act
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var passwordReset = this.repository.findByEmail(user.getEmail()).get();
        assertNotNull(passwordReset.getToken());
        assertEquals(WEB_DEVICE_ID, passwordReset.getDeviceId());
        assertTrue(passwordReset.getExpiredAt().isAfter(LocalDateTime.now()));


        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var updatedPasswordReset = this.repository.findByEmail(user.getEmail()).get();
        assertNotNull(updatedPasswordReset.getToken());
        assertEquals(WEB_DEVICE_ID, updatedPasswordReset.getDeviceId());
        assertTrue(updatedPasswordReset.getExpiredAt().isAfter(LocalDateTime.now()));



    }


}
