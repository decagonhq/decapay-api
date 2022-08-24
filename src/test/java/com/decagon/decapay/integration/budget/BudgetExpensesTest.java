package com.decagon.decapay.integration.budget;


import com.decagon.decapay.dto.budget.CreateBudgetLineItemDto;
import com.decagon.decapay.dto.budget.EditBudgetLineItemDto;
import com.decagon.decapay.model.budget.*;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.budget.ExpenseRepository;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class BudgetExpensesTest {
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
    private ExpenseRepository expenseRepository;
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

    private Budget fetchTestBudget(BudgetPeriod period, LocalDate startDate, LocalDate endDate, User user){
        Budget budget = TestModels.budget( period, startDate, endDate);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }

    @Test
    void shouldReturnBudgetLineItemExpensesSuccessfully() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));

        BudgetLineItem lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(budgetLineItem -> budgetLineItem.getBudgetCategory().equals(category))
                .findFirst().get();
        Expenses expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setDescription("descriptin 1");
        Expenses expenses2 = TestModels.expenses(BigDecimal.valueOf(1000.00), LocalDate.now());
        expenses2.setDescription("description 2");

        lineItem.addExpense(expenses);
        lineItem.addExpense(expenses2);
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2));

        Collection<Expenses> expenses1 = expenseRepository.findAll();
        assertEquals(2, expenses1.size());
        expenses1.stream().map(Expenses::getDescription).toList().containsAll(List.of("Description 1", "Description 2"));
        expenses1.stream().map(Expenses::getAmount).toList().containsAll(List.of("500", "1000"));
    }

    @Test
    void shouldNotListExpensesWhenBudgetDoesNotExists() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", 1, category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }
    @Test
    void shouldNotListExpensesWhenBudgetCategoryDoesNotExists() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));


        Expenses expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setDescription("descriptin 1");
        Expenses expenses2 = TestModels.expenses(BigDecimal.valueOf(1000.00), LocalDate.now());
        expenses2.setDescription("description 2");

        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), 1).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }

    @Test
    void shouldNotListExpensesWhenBudgetLineItemExpensesDoesNotExists() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));

        BudgetLineItem lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(budgetLineItem -> budgetLineItem.getBudgetCategory().equals(category))
                .findFirst().get();
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
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

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));

        BudgetLineItem lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(budgetLineItem -> budgetLineItem.getBudgetCategory().equals(category))
                .findFirst().get();
        Expenses expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setDescription("descriptin 1");
        Expenses expenses2 = TestModels.expenses(BigDecimal.valueOf(1000.00), LocalDate.now());
        expenses2.setDescription("description 2");

        lineItem.addExpense(expenses);
        lineItem.addExpense(expenses2);
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }

}
