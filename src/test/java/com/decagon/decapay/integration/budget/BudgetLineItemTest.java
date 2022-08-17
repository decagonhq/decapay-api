package com.decagon.decapay.integration.budget;


import com.decagon.decapay.dto.budget.BudgetLineItemDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.budget.BudgetLineItemRepository;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.LINE_ITEM_CREATED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class BudgetLineItemTest {
    @Value("${api.basepath-api}")
    private String path;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
    @Autowired
    private BudgetLineItemRepository budgetLineItemRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    private HttpHeaders headers;
    @BeforeEach
    public void runBeforeAllTestMethods() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetDoesNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");

        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", 1L)
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetCategoryDoesNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);

        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(2L);
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn405ConflictWhenTryingToCreateLineItemAndLineItemAlreadyExists() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);


        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(200));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetDoesNotBelongToUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);

        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        this.budgetRepository.save(budget);


        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToCreateLineItemAndBudgetCategoryDoesNotBelongToUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);

        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        this.budgetRepository.save(budget);


        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToCreateLineItemAndTotalProjectAmountForBudgetIsLessThanTheExistingLineItemsTotalAmountPlusTheNewLineItemTotalAmount() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        BudgetCategory category3 = TestModels.budgetCategory("Rent");
        category3.setUser(user);

        BudgetCategory category4 = TestModels.budgetCategory("Fare");
        category4.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category, category2, category3, category4));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category3, BigDecimal.valueOf(500));
        this.budgetRepository.save(budget);


        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category4.getId());
        dto.setAmount(BigDecimal.valueOf(600));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateLineItemSuccessfully() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        BudgetCategory category3 = TestModels.budgetCategory("Rent");
        category3.setUser(user);

        BudgetCategory category4 = TestModels.budgetCategory("Fare");
        category4.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category, category2, category3, category4));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category3, BigDecimal.valueOf(500.00));
        this.budgetRepository.save(budget);



        BudgetLineItemDto dto = new BudgetLineItemDto();
        dto.setBudgetCategoryId(category4.getId());
        dto.setAmount(BigDecimal.valueOf(500.00));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_CREATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        BudgetLineItem lineItem = this.budgetLineItemRepository.findByBudgetIdAndBudgetCategoryId(budget.getId(), category4.getId()).get();
        assertEquals(category4.getId(), lineItem.getBudgetCategory().getId());
        assertEquals(budget.getId(), lineItem.getBudget().getId());
        assertEquals(dto.getAmount().setScale(2), lineItem.getProjectedAmount());
    }

    private Budget fetchTestBudget(BudgetPeriod period, LocalDate startDate, LocalDate endDate, User user){
        Budget budget = TestModels.budget( period, startDate, endDate);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }

}
