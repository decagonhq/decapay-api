package com.decagon.decapay.unit.controller;

import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.controller.user.SignUpController;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.dto.user.SignUpRequestDTO;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.annotation.UnsecuredWebMvcTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnsecuredWebMvcTest(controllers = SignUpController.class)
@ActiveProfiles("test")
class SignUpControllerTest {
    @Value("${api.basepath-api}")
    private String path = "";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService userService;

    SignUpRequestDTO requestDto(){
      SignUpRequestDTO  signUpRequestDTO = new SignUpRequestDTO();
      signUpRequestDTO.setFirstName("firstName");
      signUpRequestDTO.setLastName("lastName");
      signUpRequestDTO.setEmail("a@b.com");
      signUpRequestDTO.setPassword("password");
      signUpRequestDTO.setPhoneNumber("0123456789");
      return signUpRequestDTO;
    }


    @Test
    void signUpFailsWithEmptyRequiredInputFields() throws Exception {
        SignUpRequestDTO requestDto = requestDto();
        //invalid empty fields request
        requestDto.setFirstName("");
        requestDto.setLastName("");
        requestDto.setEmail("");
        requestDto.setPhoneNumber("");
        requestDto.setCountryCode("");
        requestDto.setCurrencyCode("");
        requestDto.setLanguageCode("");

        assertFailWithInvalidRequest(requestDto, "firstName", "lastName", "email","phoneNumber","" +
                "countryCode","currencyCode","languageCode");

        //invalid null fields request
        requestDto.setFirstName(null);
        requestDto.setLastName(null);
        requestDto.setEmail(null);
        requestDto.setPhoneNumber(null);
        requestDto.setCountryCode(null);
        requestDto.setCurrencyCode(null);
        requestDto.setLanguageCode(null);
        
        assertFailWithInvalidRequest(requestDto, "firstName", "lastName", "email","phoneNumber","" +
                "countryCode","currencyCode","languageCode");
    }


    @Test
    void signUpFailsWithInvalidInputSize() throws Exception {
        SignUpRequestDTO requestDto = requestDto();
        //invalid empty fields request
        requestDto.setFirstName(RandomStringUtils.randomAlphabetic(SchemaConstants.FIRST_NAME_MAX_SIZE+1));
        requestDto.setLastName(RandomStringUtils.randomAlphabetic(SchemaConstants.LAST_NAME_MAX_SIZE+1));
        requestDto.setEmail(RandomStringUtils.randomAlphabetic(64) + "@" + RandomStringUtils.randomAlphabetic((SchemaConstants.EMAIL_MAX_SIZE-64)+1) + ".com");//<=100
        requestDto.setPhoneNumber(RandomStringUtils.randomNumeric(SchemaConstants.PHONE_NUMBER_MAX_SIZE+1));
        requestDto.setCountryCode(RandomStringUtils.randomAlphabetic(SchemaConstants.COUNTRY_CODE_MAX_SIZE+1));
        requestDto.setCurrencyCode(RandomStringUtils.randomAlphabetic(SchemaConstants.CURRENCY_CODE_MAX_SIZE+1));
        requestDto.setLanguageCode(RandomStringUtils.randomAlphabetic(SchemaConstants.LANGUAGE_CODE_MAX_SIZE+1));

        assertFailWithInvalidRequest(requestDto, "firstName","email", "lastName", "phoneNumber","" +
                "countryCode","currencyCode","languageCode");
    }


    void assertFailWithInvalidRequest(SignUpRequestDTO requestDto, String... invalidInputs) throws Exception {
        mockMvc.perform(post(path + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(requestDto)))
                .andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.subErrors[*].field", Matchers.hasItems(invalidInputs)))
                .andDo(print());

    }


    @Test
    void shouldSignUpSuccessfullu() throws Exception {

        IdResponseDto idResponseDto=new IdResponseDto(1L);

        SignUpRequestDTO  signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setFirstName("firstName");
        signUpRequestDTO.setLastName("lastName");
        signUpRequestDTO.setEmail("a@b.com");
        signUpRequestDTO.setPassword("password");
        signUpRequestDTO.setPhoneNumber("0123456789");
        signUpRequestDTO.setLanguageCode("en");
        signUpRequestDTO.setCountryCode("NG");
        signUpRequestDTO.setCurrencyCode("NGN");

        when(this.userService.registerUser(any(SignUpRequestDTO.class))).thenReturn(idResponseDto);
        mockMvc.perform(post(path + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(signUpRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)))
                .andDo(print());
    }

}
