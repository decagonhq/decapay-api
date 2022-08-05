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

        LocalDateTime today = LocalDateTime.now();

        Expenses transportationExpense1 = new Expenses();
        transportationExpense1.setAmount(BigDecimal.valueOf(1300));
        transportationExpense1.setDescription("Day 1 Transportation");
        expensesRepository.save(transportationExpense1);

        Expenses transportationExpense2 = new Expenses();
        transportationExpense2.setAmount(BigDecimal.valueOf(1200));
        transportationExpense2.setDescription("Day 2 Transportation");
        expensesRepository.save(transportationExpense2);

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setNotificationThreshold("Notification ThreshHold");
        budgetLineItem.setProjectedAmount(BigDecimal.valueOf(5000));
        budgetLineItem.setBudgetCategory(budgetCategory);
        budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budgetLineItem.addExpense(transportationExpense1);
        budgetLineItem.addExpense(transportationExpense2);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setProjectedAmount(budgetLineItem.getProjectedAmount());
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.addBudgetLineItem(budgetLineItem);
        budgetRepository.save(budget);

        User user = new User();
        user.setEmail("ola@gmail.com");
        user.setFirstName("ola");
        user.setLastName("dip");
        user.setPhoneNumber("08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode("password"));
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

        LocalDateTime today = LocalDateTime.now();

        Expenses TransportationExpense1 = new Expenses();
        TransportationExpense1.setDescription("Day 1 Transportation");
        TransportationExpense1.setAmount(BigDecimal.valueOf(1300));

        Expenses TransportationExpense2 = new Expenses();
        TransportationExpense2.setDescription("Day 2 Transportation");
        TransportationExpense2.setAmount(BigDecimal.valueOf(1200));
        expensesRepository.saveAll(List.of(TransportationExpense1, TransportationExpense2));

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setNotificationThreshold("Notification ThreshHold");
        budgetLineItem.setProjectedAmount(BigDecimal.valueOf(5000));
        budgetLineItem.setBudgetCategory(budgetCategory);
        budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budgetLineItem.addExpense(TransportationExpense1);
        budgetLineItem.addExpense(TransportationExpense2);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setProjectedAmount(budgetLineItem.getProjectedAmount());
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.addBudgetLineItem(budgetLineItem);
        budgetRepository.save(budget);

        User user = new User();
        user.setEmail("ola@gmail.com");
        user.setFirstName("ola");
        user.setLastName("dip");
        user.setPhoneNumber("08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode("password"));
        user.addBudget(budget);
        userRepository.save(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isForbidden());
    }



    @Test
    @Transactional
    void shouldReturn404NotFoundWhenBudgetIsNotAvailable() throws Exception {

        LocalDateTime today = LocalDateTime.now();

        Expenses TransportationExpense1 = new Expenses();
        TransportationExpense1.setDescription("Day 1 Transportation");
        TransportationExpense1.setAmount(BigDecimal.valueOf(1300));

        Expenses TransportationExpense2 = new Expenses();
        TransportationExpense2.setDescription("Day 2 Transportation");
        TransportationExpense2.setAmount(BigDecimal.valueOf(1200));
        expensesRepository.saveAll(List.of(TransportationExpense1, TransportationExpense2));

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setNotificationThreshold("Notification ThreshHold");
        budgetLineItem.setProjectedAmount(BigDecimal.valueOf(5000));
        budgetLineItem.setBudgetCategory(budgetCategory);
        budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budgetLineItem.addExpense(TransportationExpense1);
        budgetLineItem.addExpense(TransportationExpense2);
        budgetLineItemRepository.save(budgetLineItem);

        User user = new User();
        user.setEmail("ola@gmail.com");
        user.setFirstName("ola");
        user.setLastName("dip");
        user.setPhoneNumber("08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", 2).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void shouldReturn400InvalidRequestWhenAUserViewBudgetSheDidNotCreate() throws Exception {

        LocalDateTime today = LocalDateTime.now();

        Expenses TransportationExpense1 = new Expenses();
        TransportationExpense1.setDescription("Day 1 Transportation");
        TransportationExpense1.setAmount(BigDecimal.valueOf(1300));

        Expenses TransportationExpense2 = new Expenses();
        TransportationExpense2.setDescription("Day 2 Transportation");
        TransportationExpense2.setAmount(BigDecimal.valueOf(1200));
        expensesRepository.saveAll(List.of(TransportationExpense1, TransportationExpense2));

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle("Transportation Budget");
        budgetCategoryRepository.save(budgetCategory);

        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setNotificationThreshold("Notification ThreshHold");
        budgetLineItem.setProjectedAmount(BigDecimal.valueOf(5000));
        budgetLineItem.setBudgetCategory(budgetCategory);
        budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budgetLineItem.addExpense(TransportationExpense1);
        budgetLineItem.addExpense(TransportationExpense2);
        budgetLineItemRepository.save(budgetLineItem);

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setProjectedAmount(budgetLineItem.getProjectedAmount());
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.addBudgetLineItem(budgetLineItem);
        budgetRepository.save(budget);

        User user = new User();
        user.setEmail("ola@gmail.com");
        user.setFirstName("ola");
        user.setLastName("dip");
        user.setPhoneNumber("08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budget/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isBadRequest());
    }


}
