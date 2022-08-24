package com.decagon.decapay.integration.budget;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetPeriod;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.EXPENSE_CREATED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static com.decagon.decapay.utils.CustomDateUtil.formatLocalDateToString;
import static com.decagon.decapay.utils.CustomDateUtil.formatStringToLocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class ExpensesTest {
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
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
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
        return budgetRepository.save(budget);
    }

    @Test
    void GivenBudgetLineItemExists_WhenUserLogExpenseWithInvalidExpenseDataRequest_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(0));
        dto.setDescription("");
        dto.setTransactionDate("");
        setAuthHeader(user);

        this.validateExpectation(budget, category, dto, status().isBadRequest());

    }

    @Test
    void GivenNoOrSomeBudgetLineItemExists_WhenUserLogExpenseWithNoExistingBudget_SystemShouldFailWith404() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setDescription("Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT));
        setAuthHeader(user);

        this.validateExpectation(null, category, dto, status().isNotFound());
    }

    @Test
    void GivenNoOrSomeBudgetLineItemExists_WhenUserLogExpenseWithNoExistingBudgetCategory_SystemShouldFailWith404() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setDescription("Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT));
        setAuthHeader(user);

        this.validateExpectation(budget, null, dto, status().isNotFound());
    }

    @Test
    void GivenABudgetLineItemExists_WhenUserLogExpenseAndBudgetLineItemDoesNotBelongToUser_SystemShouldFailWith404() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);
        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setDescription("Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT));

        setAuthHeader(user);
        this.validateExpectation(budget, category, dto, status().isNotFound());
    }

    @Test
    void GivenABudgetLineItemExists_WhenUserLogExpenseAndTransactionDateIsOutsideLineItemDateRange_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(4000));

        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        var lineItem = budget.getBudgetLineItem(category);
        lineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(1000));

        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setDescription("Food");
        dto.setTransactionDate(LocalDate.now().plusMonths(2).toString());
        setAuthHeader(user);

        this.validateExpectation(budget, category, dto, status().isBadRequest());
    }

    @Test
    void GivenABudgetLineItemExists_WhenUserLogExpenseAndSumOfAllExpensesIsGreaterThanLineItemTotalAmountSpentSoFar_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));

        var lineItem = budget.getBudgetLineItem(category);
        lineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(1000));
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(2000));
        dto.setDescription("Food");
        dto.setTransactionDate(LocalDate.now().toString());
        setAuthHeader(user);

        this.validateExpectation(budget, category, dto, status().isBadRequest());
    }

    @Test
    void GivenABudgetLineItemExists_WhenUserLogExpenseAndSumOfExpectedLineItemTotalAmountSpentSoFarIsGreaterThanBudgetTotalAmountSpentSoFar_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(4000));

        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000));

        var lineItem = budget.getBudgetLineItem(category);
        lineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(3000));

        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(2000));
        dto.setDescription("Food");
        dto.setTransactionDate(LocalDate.now().plusDays(5).toString());
        setAuthHeader(user);

        this.validateExpectation(budget, category, dto, status().isBadRequest());
    }

    @Test
    void GivenABudgetLineItemExists_WhenUserLogExpense_SystemShouldLogExpenseSuccessfully() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(4000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000));

        var lineItem = budget.getBudgetLineItem(category);
        lineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2000));

        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setDescription("Food");
        dto.setTransactionDate(LocalDate.now().toString());
        setAuthHeader(user);

         this.validateExpectation(budget, category, dto, status().isOk())
                .andExpect(jsonPath("$.message").value(EXPENSE_CREATED_SUCCESSFULLY));
         var expense = this.expenseRepository.findAll().get(0);
         assertThat(expense.getAmount().doubleValue()).isEqualTo(dto.getAmount().doubleValue());
         assertThat(expense.getDescription()).isEqualTo(dto.getDescription());
         assertThat(expense.getTransactionDate()).isEqualTo(formatStringToLocalDate(dto.getTransactionDate()));
    }

    private ResultActions validateExpectation(Budget budget, BudgetCategory category, ExpenseDto dto, ResultMatcher expectedResult) throws Exception {
        Long budgetId = budget == null ? 1L : budget.getId();
        Long categoryId = category == null ? 1L : category.getId();
        return this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems/{categoryId}/expenses", budgetId, categoryId)
                .content(TestUtils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(expectedResult)
        ;
    }
}
