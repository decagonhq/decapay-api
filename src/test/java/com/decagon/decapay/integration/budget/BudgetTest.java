package com.decagon.decapay.integration.budget;


import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.budget.BudgetLineItemRepository;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.budget.ExpensesRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.JwtUtil;
import com.decagon.decapay.utils.TestModels;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
public class BudgetTest {

    @Value("${api.basepath-api}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    @Autowired
    private JwtUtil jwtUtil;
    private HttpHeaders headers;

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private BudgetLineItemRepository budgetLineItemRepository;

    @BeforeEach
    public void runBeforeAllTestMethods() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }


    public void setUpAuthUser(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
    @Transactional
    void shouldViewBudgetDetailsSuccessfully() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem =TestModels.setUpBudgetLineItems(budgetCategory);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = TestModels.setUpBudget(budgetLineItem);
        budgetRepository.save(budget);

        user.setUserStatus(UserStatus.ACTIVE);
        user.addBudget(budget);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.percentageSpentSoFar").value(50))
                .andExpect(jsonPath("$.data.projectedAmount").value(5000))
                .andExpect(jsonPath("$.data.notificationThreshold").value("Notification Trashold"))
                .andExpect(jsonPath("$.data.title").value("Transportation Budget"))
                .andExpect(jsonPath("$.data.totalAmountSpentSoFar").value(2500))
                .andExpect(jsonPath("$.data.budgetPeriod").value(BudgetPeriod.MONTHLY.name()))
                .andExpect(jsonPath("$.data.lineItems[0].projectedAmount").value(5000))
                .andExpect(jsonPath("$.data.lineItems[0].notificationThreshold").value("Notification ThreshHold"))
                .andExpect(jsonPath("$.data.lineItems[0].budgetCategory.title").value("Transportation Budget"));
    }

    @Test
    @Transactional
    void shouldReturn403ForbiddenErrorWhenUserNotSignIn() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem =TestModels.setUpBudgetLineItems(budgetCategory);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = TestModels.setUpBudget(budgetLineItem);
        budgetRepository.save(budget);

        user.setUserStatus(UserStatus.ACTIVE);
        user.addBudget(budget);
        userRepository.save(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void shouldReturn404NotFoundWhenBudgetIsNotAvailable() throws Exception {

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem =TestModels.setUpBudgetLineItems(budgetCategory);
        budgetLineItemRepository.save(budgetLineItem);

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", 2).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void shouldReturn400InvalidRequestWhenAUserViewBudgetSheDidNotCreate() throws Exception {

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem =TestModels.setUpBudgetLineItems(budgetCategory);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = TestModels.setUpBudget(budgetLineItem);
        budgetRepository.save(budget);

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isBadRequest());
    }


}
