package com.decagon.decapay.integration.budget;

import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.model.budget.Expense;
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

import static com.decagon.decapay.constants.ResponseMessageConstants.EXPENSE_UPDATED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static com.decagon.decapay.utils.CustomDateUtil.formatLocalDateToString;
import static com.decagon.decapay.utils.CustomDateUtil.formatStringToLocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class EditExpenseTest {
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
    void givenNoOrAnExpenseExists_WhenUserUpdateExpenseWithInvalidExpenseDataRequest_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(0));
        dto.setDescription("");
        dto.setTransactionDate("");
        setAuthHeader(user);

        this.validateExpectation(null, dto, status().isBadRequest());

    }

    @Test
    void givenAnExpenseExists_WhenUserUpdateAnExpenseThatDoesNotExistWithValidData_SystemShouldFailWith404() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000));
        this.budgetRepository.save(budget);

        var lineItem1 = budget.getBudgetLineItem(category);

        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(500.00));
        expense1.setTransactionDate(LocalDate.now().plusDays(2));
        expense1.setDescription("Food expense1");
        expense1.setBudgetLineItem(lineItem1);
        expense1.getAuditSection().setDelF("0");
        expenseRepository.save(expense1);

        lineItem1.addExpense(expense1);
        this.budgetRepository.save(budget);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setDescription("Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now(), DateConstants.DATE_INPUT_FORMAT));
        setAuthHeader(user);

        this.validateExpectation(null, dto, status().isNotFound());

    }

    @Test
    void givenAnExpenseExists_WhenUserUpdateAnExpenseAndTransactionDateIsFutureDate_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000));
        this.budgetRepository.save(budget);

        var lineItem1 = budget.getBudgetLineItem(category);

        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(500.00));
        expense1.setTransactionDate(LocalDate.now().plusDays(2));
        expense1.setDescription("Food expense1");
        expense1.setBudgetLineItem(lineItem1);
        expense1.getAuditSection().setDelF("0");
        expenseRepository.save(expense1);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setDescription("Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now().plusDays(1), DateConstants.DATE_INPUT_FORMAT));
        setAuthHeader(user);

        this.validateExpectation(expense1, dto, status().isBadRequest());

    }

    @Test
    void givenAnExpenseCreatedByOtherUserExists_WhenUserUpdateExpenseAndExpenseBelongToOtherUser_SystemShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);
        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);

        budgetCategoryRepository.save(category);


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000));
        this.budgetRepository.save(budget);

        var lineItem1 = budget.getBudgetLineItem(category);

        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(500.00));
        expense1.setTransactionDate(LocalDate.now().plusDays(2));
        expense1.setDescription("Food expense1");
        expense1.setBudgetLineItem(lineItem1);
        expense1.getAuditSection().setDelF("0");

        expense1 = expenseRepository.save(expense1);

        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setDescription("Food");
        dto.setTransactionDate(LocalDate.now().toString());
        setAuthHeader(user);

        this.validateExpectation(expense1, dto, status().isBadRequest());

    }

    @Test
    void givenAnExpenseCreatedByUserExists_WhenUserUpdateExpense_SystemShouldUpdateExpenseSuccessfully() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Food");
        category2.setUser(user);
        budgetCategoryRepository.saveAll(List.of(category,category2));


        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(3000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000.00));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(1500.00));
        this.budgetRepository.save(budget);

        var lineItem1 = budget.getBudgetLineItem(category);
        lineItem1.setTotalAmountSpentSoFar(BigDecimal.valueOf(500.00));

        var lineItem2 = budget.getBudgetLineItem(category2);
        lineItem2.setTotalAmountSpentSoFar(BigDecimal.valueOf(1000.00));
        this.budgetRepository.save(budget);

        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(500.00));
        expense1.setTransactionDate(LocalDate.now().plusDays(2));
        expense1.setDescription("Food expense1");
        expense1.setBudgetLineItem(lineItem1);
        expense1.getAuditSection().setDelF("0");

        Expense expense2 = new Expense();
        expense2.setAmount(BigDecimal.valueOf(1000.00));
        expense2.setTransactionDate(LocalDate.now().plusDays(3));
        expense2.setDescription("Food expense2");
        expense2.setBudgetLineItem(lineItem2);
        expense2.getAuditSection().setDelF("0");
        expenseRepository.saveAll(List.of(expense1, expense2));


        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setDescription("Updated Food");
        dto.setTransactionDate(formatLocalDateToString(LocalDate.now(), DateConstants.DATE_INPUT_FORMAT));
        setAuthHeader(user);

        var response = this.validateExpectation(expense1, dto, status().isOk())
                .andExpect(jsonPath("$.message").value(EXPENSE_UPDATED_SUCCESSFULLY)).andReturn();

        var expenseId = (Integer) TestUtils.objectFromResponseStr(response.getResponse().getContentAsString(),"$.data.id");

        var updatedExpense = expenseRepository.findExpenseById(Long.valueOf(expenseId)).get();
        lineItem1 = updatedExpense.getBudgetLineItem();
        budget = updatedExpense.getBudgetLineItem().getBudget();

        assertNotNull(updatedExpense.getId());
        assertThat(updatedExpense.getAmount().setScale(2)).isEqualTo(dto.getAmount().setScale(2));
        assertThat(updatedExpense.getDescription()).isEqualTo(dto.getDescription());
        assertThat(updatedExpense.getTransactionDate()).isEqualTo(formatStringToLocalDate(dto.getTransactionDate(), DateConstants.DATE_INPUT_FORMAT));
        assertThat(lineItem1.getTotalAmountSpentSoFar().setScale(2)).isEqualTo(BigDecimal.valueOf(1000.00).setScale(2));
        assertThat(budget.getTotalAmountSpentSoFar().setScale(2)).isEqualTo(BigDecimal.valueOf(2000.00).setScale(2));
    }

    private ResultActions validateExpectation(Expense expense, ExpenseDto dto, ResultMatcher expectedResult) throws Exception {
        Long expenseId = expense == null ? 0 : expense.getId();
        return this.mockMvc.perform(put(path + "/expenses/{expenseId}", expenseId)
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(expectedResult);
    }
}
