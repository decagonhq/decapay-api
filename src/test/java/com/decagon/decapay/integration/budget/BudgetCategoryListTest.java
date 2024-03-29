package com.decagon.decapay.integration.budget;


import com.decagon.decapay.dto.budget.CreateBudgetCategoryDto;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
public class BudgetCategoryListTest {

    @Value("${api.basepath-api}")
    private String path;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private HttpHeaders headers;



    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }



    @Test
    void testShouldReturnEmptyListOfBudgetCategoriesSuccesfullyWhenAUserHasNoBudgetCategory() throws Exception {

        User user = new User();
        user.setEmail("o4g@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056357667");
        userRepository.save(user);

        setAuthHeader(user);


        this.mockMvc
                .perform(get(path + "/budget_categories")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }


    @Test
    void testShouldReturnListOfBudgetCategoriesSuccesfullyWhenAUserHasBudgetCategory() throws Exception {

        User user = new User();
        user.setEmail("o4g@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056357667");
        userRepository.save(user);

        setAuthHeader(user);

        BudgetCategory budgetCategory1 = new BudgetCategory();
        budgetCategory1.setTitle("Transport");
        budgetCategory1.setUser(user);
        budgetCategoryRepository.save(budgetCategory1);

        BudgetCategory budgetCategory2 = new BudgetCategory();
        budgetCategory2.setTitle("Entertainment");
        budgetCategory2.setUser(user);
        budgetCategoryRepository.save(budgetCategory2);

        BudgetCategory budgetCategory3 = new BudgetCategory();
        budgetCategory3.setTitle("food");
        budgetCategory3.setUser(user);
        budgetCategoryRepository.save(budgetCategory3);

        this.mockMvc
                .perform(get(path + "/budget_categories")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(3))
                .andExpect(jsonPath("$.data[*].id", Matchers.containsInRelativeOrder(budgetCategory1.getId().intValue(), budgetCategory2.getId().intValue(), budgetCategory3.getId().intValue())))
                .andExpect(jsonPath("$.data[0].id").value(budgetCategory1.getId().intValue()))
                .andExpect(jsonPath("$.data[0].title").value("Transport"));
    }


    @Test
    void testShouldReturnListOfBudgetCategoriesThatBelongsToALogginUserSuccessfully() throws Exception {

        User user = new User();
        user.setEmail("o5g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        userRepository.save(user);

        BudgetCategory budgetCategory1 = new BudgetCategory();
        budgetCategory1.setTitle("Transport");
        budgetCategory1.setUser(user);
        budgetCategoryRepository.save(budgetCategory1);


        BudgetCategory budgetCategory2 = new BudgetCategory();
        budgetCategory2.setTitle("Entertainment");
        budgetCategory2.setUser(user);
        budgetCategoryRepository.save(budgetCategory2);



        User user2 = new User();
        user2.setEmail("o7g@gmail.com");
        user2.setPassword(passwordEncoder.encode("123456789"));
        user2.setFirstName("Goodluck");
        user2.setLastName("Nwoko");
        user2.setPhoneNumber("07050359667");
        userRepository.save(user2);

        BudgetCategory budgetCategory3 = new BudgetCategory();
        budgetCategory3.setTitle("food");
        budgetCategory3.setUser(user2);
        budgetCategoryRepository.save(budgetCategory3);



        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budget_categories")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(budgetCategory1.getId().intValue()))
                .andExpect(jsonPath("$.data[0].title").value("Transport"))
                .andExpect(jsonPath("$.data[1].id").value(budgetCategory2.getId().intValue()))
                .andExpect(jsonPath("$.data[1].title").value("Entertainment"));



        setAuthHeader(user2);

        this.mockMvc
                .perform(get(path + "/budget_categories")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(budgetCategory3.getId().intValue()))
                .andExpect(jsonPath("$.data[0].title").value("food"));
    }

    @Test
    void shouldCreateBudgetCategorySuccessfully() throws Exception {

        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0123456789");
        user = userRepository.save(user);
        setAuthHeader(user);

        CreateBudgetCategoryDto dto = new CreateBudgetCategoryDto();
        dto.setTitle("Transportation");

        mockMvc.perform(post(path + "/budget_categories").headers(headers).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto))).andExpect(status().isCreated());

        BudgetCategory category = budgetCategoryRepository.findAll().iterator().next();
        Assertions.assertEquals("Transportation", category.getTitle());
        Assertions.assertEquals(user.getId(), category.getUser().getId());
    }

    @Test
    void createBudgetCategoryFailsWhenUserNotAuthenticated() throws Exception {

        CreateBudgetCategoryDto dto = new CreateBudgetCategoryDto();
        dto.setTitle("Transportation");

        mockMvc.perform(post(path + "/budget_categories").contentType(MediaType.APPLICATION_JSON).content(
                TestUtils.asJsonString(dto))).andExpect(status().isUnauthorized());
    }


    @Test
    void shouldUpdateBudgetCategorySuccessfully() throws Exception {

        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0123456789");
        user = userRepository.save(user);
        setAuthHeader(user);

        BudgetCategory category = new BudgetCategory();
        category.setTitle("Transportation");
        category.setUser(user);
        budgetCategoryRepository.save(category);

        CreateBudgetCategoryDto dto = new CreateBudgetCategoryDto();
        dto.setTitle("Food");

        mockMvc.perform(put(path + "/budget_categories/{categoryId}", category.getId()).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto))).andExpect(status().isOk());

        BudgetCategory updatedBudgetCategory = budgetCategoryRepository.findById(category.getId()).get();
        Assertions.assertEquals("Food", updatedBudgetCategory.getTitle());
    }

    @Test
    void updateBudgetCategoryFailsWhenUserNotAuthenticated() throws Exception {

        User user = new User();
        user.setEmail("o4g@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056357667");
        userRepository.save(user);

        BudgetCategory category = new BudgetCategory();
        category.setTitle("Transportation");
        category.setUser(user);
        budgetCategoryRepository.save(category);

        CreateBudgetCategoryDto dto = new CreateBudgetCategoryDto();
        dto.setTitle("Food");

        mockMvc.perform(put(path + "/budget_categories/{categoryId}", category.getId()).contentType(MediaType.APPLICATION_JSON).content(
                TestUtils.asJsonString(dto))).andExpect(status().isUnauthorized());
    }

    @Test
    void updateBudgetCategoryFailsWhenUserUpdateBudgetCategoryUserDidNotCreate() throws Exception {

        User user = new User();
        user.setFirstName("name");
        user.setLastName("name");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0122345555");
        user = userRepository.save(user);

        BudgetCategory category = new BudgetCategory();
        category.setUser(user);
        category.setTitle("Transportation");
        budgetCategoryRepository.save(category);

        User user1 = new User();
        user1.setEmail("o5g@gmail.com");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setFirstName("dip");
        user1.setLastName("ola");
        user1.setPhoneNumber("0911111");
        userRepository.save(user1);

        setAuthHeader(user1);

        CreateBudgetCategoryDto dto = new CreateBudgetCategoryDto();
        dto.setTitle("Food");

        mockMvc.perform(put(path + "/budget_categories/{categoryId}", category.getId()).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto))).andExpect(status().isBadRequest());

    }



}
