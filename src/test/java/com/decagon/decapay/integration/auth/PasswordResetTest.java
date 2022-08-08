package com.decagon.decapay.integration.auth;


import com.decagon.decapay.model.auth.ResetCodeStatus;
import com.decagon.decapay.model.auth.PasswordReset;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.dto.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.dto.auth.VerifyPasswordResetCodeRequest;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("emailuser", "emailpass"));

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

        var passwordReset = this.repository.findByEmailAndDeviceId(user.getEmail(), WEB_DEVICE_ID).get();
        assertNotNull(passwordReset.getToken());
        assertEquals(WEB_DEVICE_ID, passwordReset.getDeviceId());
        assertTrue(passwordReset.getExpiredAt().isAfter(LocalDateTime.now()));

        //assert email sent
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        MimeMessage receivedMessage = receivedMessages[0];
        assertTrue(StringUtils.isNotEmpty(GreenMailUtil.getBody(receivedMessage)));
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals("fabiane@decagonhq.com", receivedMessage.getAllRecipients()[0].toString());
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

        var passwordReset = this.repository.findByEmailAndDeviceId(user.getEmail(), MOBILE_DEVICE_ID).get();
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

        var passwordReset = this.repository.findByEmailAndDeviceId(user.getEmail(), WEB_DEVICE_ID).get();
        assertNotNull(passwordReset.getToken());
        assertEquals(WEB_DEVICE_ID, passwordReset.getDeviceId());
        assertTrue(passwordReset.getExpiredAt().isAfter(LocalDateTime.now()));


        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/forgot-password").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var updatedPasswordReset = this.repository.findByEmailAndDeviceId(user.getEmail(),WEB_DEVICE_ID).get();
        assertNotNull(updatedPasswordReset.getToken());
        assertEquals(WEB_DEVICE_ID, updatedPasswordReset.getDeviceId());
        assertTrue(updatedPasswordReset.getExpiredAt().isAfter(LocalDateTime.now()));



    }

    @Test
    void shouldThrowInvalidRequestWhenVerifyingPasswordResetCodeAndPasswordResetCodeIsNotPresentInTheRequest() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);

        //input
        VerifyPasswordResetCodeRequest dto = new VerifyPasswordResetCodeRequest("fabiane@decagonhq.com","");
        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/verify-code").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldThrowResourceNotFoundWhenVerifyingPasswordResetCodeAndPasswordResetCodeDoesNotExist() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        this.userRepository.save(user);


        //input
        VerifyPasswordResetCodeRequest dto = new VerifyPasswordResetCodeRequest("fabiane@decagonhq.com","1234");
        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/verify-code").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowInvalidRequestWhenVerifyingPasswordResetCodeAndPasswordResetCodeHasExpired() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setExpiredAt(LocalDateTime.now().minusDays(1));
        passwordReset.setDeviceId(MOBILE_DEVICE_ID);
        this.repository.save(passwordReset);

        //input
        VerifyPasswordResetCodeRequest dto = new VerifyPasswordResetCodeRequest(user.getEmail(), passwordReset.getToken());
        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/verify-code").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldVerifyingPasswordResetCodeSuccessfully() throws Exception {
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        PasswordReset passwordReset = TestModels.passwordReset(user.getEmail(), "3215");
        passwordReset.setDeviceId(MOBILE_DEVICE_ID);
        passwordReset = this.repository.save(passwordReset);

        //input
        VerifyPasswordResetCodeRequest dto = new VerifyPasswordResetCodeRequest("fabiane@decagonhq.com","3215");
        //act
        headers = this.addMobileIdToHeaders();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/verify-code").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(PASSWORD_RESET_CODE_VERIFIED_SUCCESSFULLY));

        passwordReset = this.repository.findByEmailAndDeviceId(user.getEmail(),MOBILE_DEVICE_ID).get();
        assertEquals(ResetCodeStatus.VERIFIED, passwordReset.getStatus());
    }


}
