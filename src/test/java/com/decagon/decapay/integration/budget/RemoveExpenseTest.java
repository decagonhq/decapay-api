package com.decagon.decapay.integration.budget;


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
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
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
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.EXPENSE_REMOVED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
public class RemoveExpenseTest {

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
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetRepository budgetRepository;



    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }


    @Test
    void givenAnExpenseDoesNotExist_WhenUserRemoveExpenseThatDoesNotExist_shouldReturn404() throws Exception {
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
        this.assertEditExpense(null, status().isNotFound());
    }


    @Test
    void givenExpenseCreatedByOtherUserExist_WhenUserRemoveExpense_ShouldFailWith400() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User otherUser = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        otherUser.setUserStatus(UserStatus.ACTIVE);

        userRepository.saveAll(List.of(user, otherUser));



        //Create other user expense
        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(otherUser);
        this.budgetCategoryRepository.save(category);



        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),otherUser);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        this.budgetRepository.save(budget);

        BudgetLineItem lineItem = budget.getBudgetLineItem(category);


        Expenses expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setBudgetLineItem(lineItem);
        expenseRepository.save(expenses);


        setAuthHeader(user);
        this.mockMvc.perform(delete(path + "/expenses/{expenseId}",expenses.getId()).headers(headers))
                .andExpect(status().isBadRequest());
    }


    @Test
    void givenExpenseCreatedByUserExist_WhenUserRemoveExpense_shouldReturnSuccess() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);


        //Expense created by user
        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(1200.00));
        this.budgetRepository.save(budget);
        BudgetLineItem lineItem = budget.getBudgetLineItem(category);
        lineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(1200.00));
        this.budgetRepository.save(budget);

        Expenses expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setBudgetLineItem(lineItem);
        Expenses expenses2 = TestModels.expenses(BigDecimal.valueOf(400.00), LocalDate.now());
        expenses2.setBudgetLineItem(lineItem);
        Expenses expenses3 = TestModels.expenses(BigDecimal.valueOf(300.00), LocalDate.now());
        expenses3.setBudgetLineItem(lineItem);
        expenseRepository.saveAll(List.of(expenses, expenses2, expenses3));

        setAuthHeader(user);

        this.mockMvc.perform(delete(path + "/expenses/{expenseId}",expenses.getId()).headers(headers))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(EXPENSE_REMOVED_SUCCESSFULLY));

        Budget updatedBudget = budgetRepository.findBudgetByIdAndUserId(budget.getId(), user.getId()).get();
        BigDecimal updatedBudgetTotalAmount = updatedBudget.getTotalAmountSpentSoFar();
        BigDecimal updatedLineItemTotalAmount = updatedBudget.getBudgetLineItem(category).getTotalAmountSpentSoFar();
        assertEquals(BigDecimal.valueOf(700.00).setScale(2),updatedBudgetTotalAmount);
        assertEquals(BigDecimal.valueOf(700.00).setScale(2), updatedLineItemTotalAmount);
    }


    private Budget fetchTestBudget(BudgetPeriod period, LocalDate startDate, LocalDate endDate, User user){
        Budget budget = TestModels.budget( period, startDate, endDate);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }

    private void assertEditExpense(Expenses expenses, ResultMatcher expectedResult) throws Exception {
        Long expenseId = expenses == null ? 1L : expenses.getId();
        this.mockMvc.perform(delete(path + "/expenses/{expenseId}", expenseId).headers(headers))
                .andExpect(expectedResult);
    }
}
