package com.decagon.decapay.integration.budget;


import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.CustomDateUtil;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

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
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private HttpHeaders headers;

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
    void shouldReturn404WhenViewBudgetAndBudgetNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", 2).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenAUserViewBudgetSheDidNotCreate() throws Exception {

        User user = TestModels.user("ola", "dip", "o@b.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        Budget budget = new Budget();
        LocalDateTime today = LocalDateTime.now();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));

        user.addBudget(budget);
        userRepository.save(user);

        User user1 = TestModels.user("ola1", "dip1", "a@g.com",
                passwordEncoder.encode("password"), "08067644805");
        user1.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user1);

        setUpAuthUser(user1);

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyCollectionWhenBudgetLineItemIsDoesNotExist() throws Exception {

        LocalDateTime today = LocalDateTime.now();

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));

        user.addBudget(budget);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lineItems.size()").value(0));

    }


    @Test
    void shouldViewBudgetDetailsSuccessfully() throws Exception {
        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
        Currency currency = AppConstants.DEFAULT_CURRENCY;

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        LocalDateTime today = LocalDateTime.now();

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budget.setProjectedAmount(BigDecimal.valueOf(5000));

        user.addBudget(budget);
        userRepository.save(user);

        setUpAuthUser(user);

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectedAmount").value(5000.00))
                .andExpect(jsonPath("$.data.displayProjectedAmount").value(currency.getSymbol(locale) + "5,000.00"))
                .andExpect(jsonPath("$.data.notificationThreshold").value("Notification Trashold"))
                .andExpect(jsonPath("$.data.title").value("Transportation Budget"))
                .andExpect(jsonPath("$.data.startDate").value(CustomDateUtil.formatLocalDateTimeToString(budget.getBudgetStartDate(), DateDisplayConstants.DATE_DB_FORMAT)))
                .andExpect(jsonPath("$.data.displayStartDate").value(CustomDateUtil.formatLocalDateTimeToString(budget.getBudgetStartDate(), DateDisplayConstants.DATE_DISPLAY_FORMAT)))
                .andExpect(jsonPath("$.data.endDate").value(CustomDateUtil.formatLocalDateTimeToString(budget.getBudgetEndDate(), DateDisplayConstants.DATE_DB_FORMAT)))
                .andExpect(jsonPath("$.data.displayEndDate").value(CustomDateUtil.formatLocalDateTimeToString(budget.getBudgetEndDate(), DateDisplayConstants.DATE_DISPLAY_FORMAT)))
                .andExpect(jsonPath("$.data.totalAmountSpentSoFar").value( 2500.00))
                .andExpect(jsonPath("$.data.displayTotalAmountSpentSoFar").value(currency.getSymbol(locale) + "2,500.00"))
                .andExpect(jsonPath("$.data.percentageSpentSoFar").value(50.0))
                .andExpect(jsonPath("$.data.displayPercentageSpentSoFar").value("50.0%"))
                .andExpect(jsonPath("$.data.budgetPeriod").value(BudgetPeriod.MONTHLY.name()));
    }
}
