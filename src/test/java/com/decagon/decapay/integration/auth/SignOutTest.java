package com.decagon.decapay.integration.auth;


import com.decagon.decapay.dto.auth.SignOutRequestDto;
import com.decagon.decapay.model.auth.TokenBlacklist;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.auth.TokenBlacklistRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SignOutTest {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;



    @Value("${api.basepath-api}")
    private String path = "";

    private HttpServletRequest request;


    @Test
    void testShouldSignOutSuccessfully() throws Exception {

        User user = new User();
        user.setEmail("go@gmail.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056155664");
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("go@gmail.com");

        String token = jwtUtil.generateToken(userDetails);

        SignOutRequestDto signOutRequestDto = new SignOutRequestDto();
        signOutRequestDto.setToken(token);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        this.mockMvc
                .perform(post(path + "/signout").content(TestUtils.asJsonString(signOutRequestDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Collection<TokenBlacklist> tokenBlacklistCollection = this.tokenBlacklistRepository.findAll();
        assertEquals(tokenBlacklistCollection.size(), 1);
        assertEquals(token, tokenBlacklistCollection.iterator().next().getToken());

        //assert token cannot be used again for any api request
       this.mockMvc
                .perform(get(path + "/any").content(TestUtils.asJsonString(signOutRequestDto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
               .andDo(print())
                .andExpect(status().isForbidden());
    }
}
