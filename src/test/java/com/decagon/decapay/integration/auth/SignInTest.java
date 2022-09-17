package com.decagon.decapay.integration.auth;


import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;

import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
public class SignInTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    private HttpHeaders headers;

    private final UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");


    @Value("${api.basepath-api}")
    private String path = "";

    @BeforeEach
    public void runBeforeAllTestMethods() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    @Test
    void testShouldReturn401WhenSigninWithInvalidCredentials() throws Exception {
        User user = new User();
        user.setEmail("og@gmail.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056155664");
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("gt@gmail.com");
        loginDto.setPassword("1234");

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.post( path + "/signin").content(TestUtils.asJsonString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.token").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.message").value("Invalid Credentials"))
                .andReturn();
    }

    @Test
    void testShouldReturnAuthenticateSuccessfully() throws Exception {
        User user = new User();
        user.setEmail("ogg@gmail.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056355664");
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("ogg@gmail.com");
        loginDto.setPassword("123456");

        MvcResult result =  this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/signin").content(TestUtils.asJsonString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.country").value("NG"))
                .andExpect(jsonPath("$.data.language").value("en"))
                .andExpect(jsonPath("$.data.currency").value("NGN"))
                .andReturn();
    }

    @Test
    void testReturnDefaultLocaleSettingWhenAuthenticateAndUserSettingNotSet() throws Exception {
        User user = new User();
        user.setEmail("ogg@gmail.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056355664");
        userRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("ogg@gmail.com");
        loginDto.setPassword("123456");

        MvcResult result =  this.mockMvc
                .perform(MockMvcRequestBuilders.post(path + "/signin").content(TestUtils.asJsonString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.country").value(AppConstants.DEFAULT_COUNTRY))
                .andExpect(jsonPath("$.data.language").value(AppConstants.DEFAULT_LANGUAGE))
                .andExpect(jsonPath("$.data.currency").value(AppConstants.DEFAULT_CURRENCY_CODE))
                .andReturn();
    }

}
