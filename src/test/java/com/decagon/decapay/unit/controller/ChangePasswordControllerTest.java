package com.decagon.decapay.unit.controller;

import com.decagon.decapay.controller.user.UserController;
import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.annotation.UnsecuredWebMvcTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnsecuredWebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
class ChangePasswordControllerTest {
    @Value("${api.basepath-api}")
    private String path = "";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService passwordResetService;

    ChangePasswordRequestDto requestDto(){
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto();
        requestDto.setPassword("password");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmNewPassword("newPassword");
        return requestDto;
    }

    void assertFailWithInvalidRequest(ChangePasswordRequestDto requestDto, String... invalidInputs) throws Exception {
        mockMvc.perform(post(path + "/profile/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(requestDto)))
                .andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.subErrors[*].field", Matchers.hasItems(invalidInputs)));

    }

    @Test
    void changePasswordFailsWithInvalidRequest() throws Exception {
        ChangePasswordRequestDto requestDto = requestDto();

        //invalid empty fields request
        requestDto.setPassword("");
        requestDto.setNewPassword("");
        requestDto.setConfirmNewPassword("");
        assertFailWithInvalidRequest(requestDto, "password", "newPassword", "confirmNewPassword");

        //invalid null fields request
        requestDto.setPassword(null);
        requestDto.setNewPassword(null);
        requestDto.setConfirmNewPassword(null);
        assertFailWithInvalidRequest(requestDto, "password", "newPassword", "confirmNewPassword");
    }

    @Test
    void changePasswordFailsWithInvalidRequestWithPasswordMismatch() throws Exception {
        ChangePasswordRequestDto requestDto = requestDto();

        //invalid password mismatch request
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmNewPassword("newPassword1");

        mockMvc.perform(post(path + "/profile/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
