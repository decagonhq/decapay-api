package com.decagon.decapay.integration.budget;

import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.*;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.budget.ExpenseRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import org.hamcrest.Matchers;
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
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class ListExpensesTest {
    @Value("${api.basepath-api}")
    private String path;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
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
    void givenAnyBudgetLineItemExist_WhenUserListsExpensesForBudgetLineItemWithABudgetThatDoesNotExist_ShouldReturnEmptyList() throws Exception {
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


        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", 0, category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }

    @Test
    void givenAnyBudgetLineItemExist_WhenUserListsExpensesForBudgetLineItemWithBudgetCategoryThatDoesExist_ShouldReturnEmptyList() throws Exception {
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

        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), 0).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }

    @Test
    void givenBudgetLineItemExistWithNoExpenses_WhenUserListsExpensesForBudgetLineItem_ShouldReturnEmptyList() throws Exception {
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

        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }


    @Test
    void givenABudgetLineItemCreatedByOtherUserExistWithAtleastTwoExpenses_WhenUserListExpensesForBudgetLineItemCreatedByOtherUser_ShouldReturnEmptyList() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User otherUser = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        otherUser.setUserStatus(UserStatus.ACTIVE);
        userRepository.saveAll(List.of(user, otherUser));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(otherUser);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),otherUser);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budgetRepository.save(budget);

        BudgetLineItem lineItem = budget.getBudgetLineItem(category);

        Expenses expense = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expense.setDescription("descriptin 1");
        expense.setBudgetLineItem(lineItem);

        Expenses expense2 = TestModels.expenses(BigDecimal.valueOf(1000.00), LocalDate.now());
        expense2.setDescription("description 2");
        expense2.setBudgetLineItem(lineItem);

        expenseRepository.saveAll(List.of(expense, expense2));

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }


    @Test
    void givenABudgetLineItemCreatedByUserExistsWithAtLeastTwoExpenses_WhenUserListExpensesForTheBudgetLineItem_ShouldReturnListSuccessfully() throws Exception {
        Currency currency = AppConstants.DEFAULT_CURRENCY;
        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);


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
        this.budgetRepository.save(budget);

        BudgetLineItem lineItem = budget.getBudgetLineItem(category);

        Expenses expense = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now().plusDays(3));
        expense.setDescription("descriptin 1");
        expense.setBudgetLineItem(lineItem);

        Expenses expense2 = TestModels.expenses(BigDecimal.valueOf(1000.00), LocalDate.now().plusDays(1));
        expense2.setDescription("description 2");
        expense2.setBudgetLineItem(lineItem);

        expenseRepository.saveAll(List.of(expense, expense2));

        setAuthHeader(user);
        this.mockMvc.perform(get(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budget.getId(), category.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInRelativeOrder(expense.getId().intValue(), expense2.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].id").value(expense.getId()))
                .andExpect(jsonPath("$.data.content[0].amount").value(500))
                .andExpect(jsonPath("$.data.content[0].displayAmount").value(currency.getSymbol(locale) + "500.00"))
                .andExpect(jsonPath("$.data.content[0].description").value("descriptin 1"))
                .andExpect(jsonPath("$.data.content[0].transactionDate").value(CustomDateUtil.formatLocalDateToString(expense.getTransactionDate(), DateDisplayConstants.DATE_DB_FORMAT)))
                .andExpect(jsonPath("$.data.content[0].displayTransactionDate").value(CustomDateUtil.formatLocalDateToString(expense.getTransactionDate(), DateDisplayConstants.DATE_DISPLAY_FORMAT)));
    }






}
