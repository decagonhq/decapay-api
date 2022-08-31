package com.decagon.decapay.integration.budget;


import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
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
import com.decagon.decapay.utils.TestUtils;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

import static com.decagon.decapay.constants.ResponseMessageConstants.BUDGET_UPDATED_SUCCESSFULLY;
import static com.decagon.decapay.constants.ResponseMessageConstants.RESOURCE_RETRIEVED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);

    Currency currency = AppConstants.DEFAULT_CURRENCY;

    @BeforeEach
    public void runBeforeAllTestMethods() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public void setUpAuthUser(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
    }


    private Budget budget(LocalDate startDate, LocalDate endDate){
        Budget budget = new Budget();
        budget.setTitle("Transport");
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget.setProjectedAmount(BigDecimal.valueOf(400000));
        budget.setBudgetStartDate(startDate);
        budget.setBudgetEndDate(endDate);
        budget.setBudgetPeriod(ANNUAL);
        return budget;
    }


    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    private void buildHeader(String email){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
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

        setAuthHeader(user);;

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", 2).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenAUserViewBudgetUserDidNotCreate() throws Exception {

        User user = TestModels.user("ola", "dip", "o@b.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        Budget budget = new Budget();
        LocalDate today = LocalDate.now();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));

        user.addBudget(budget);
        userRepository.save(user);

        User user1 = TestModels.user("ola1", "dip1", "a@g.com",
                passwordEncoder.encode("password"), "08067644805");
        user1.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user1);

        setAuthHeader(user1);;

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldViewBudgetDetailsSuccessfully() throws Exception {
        Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);
        Currency currency = AppConstants.DEFAULT_CURRENCY;

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        LocalDate today = LocalDate.now();

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budget.setProjectedAmount(BigDecimal.valueOf(5000));

        user.addBudget(budget);
        userRepository.save(user);

        //budget line items
        BudgetCategory category1 = TestModels.budgetCategory("Food");
        category1.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category1, category2));

        budget.addBudgetLineItem(category1, BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2500));
        this.budgetRepository.save(budget);

        setAuthHeader(user);;

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectedAmount").value(5000.00))
                .andExpect(jsonPath("$.data.displayProjectedAmount").value(currency.getSymbol(locale) + "5,000.00"))
                .andExpect(jsonPath("$.data.notificationThreshold").value("Notification Trashold"))
                .andExpect(jsonPath("$.data.title").value("Transportation Budget"))
                .andExpect(jsonPath("$.data.startDate").value(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateDisplayConstants.DATE_DB_FORMAT)))
                .andExpect(jsonPath("$.data.displayStartDate").value(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateDisplayConstants.DATE_DISPLAY_FORMAT)))
                .andExpect(jsonPath("$.data.endDate").value(CustomDateUtil.formatLocalDateToString(budget.getBudgetEndDate(), DateDisplayConstants.DATE_DB_FORMAT)))
                .andExpect(jsonPath("$.data.displayEndDate").value(CustomDateUtil.formatLocalDateToString(budget.getBudgetEndDate(), DateDisplayConstants.DATE_DISPLAY_FORMAT)))
                .andExpect(jsonPath("$.data.totalAmountSpentSoFar").value(2500.00))
                .andExpect(jsonPath("$.data.displayTotalAmountSpentSoFar").value(currency.getSymbol(locale) + "2,500.00"))
                .andExpect(jsonPath("$.data.percentageSpentSoFar").value(50.0))
                .andExpect(jsonPath("$.data.displayPercentageSpentSoFar").value("50.0%"))
                .andExpect(jsonPath("$.data.budgetPeriod").value(MONTHLY.name()))
                //line items
                .andExpect(jsonPath("$.data.lineItems.size()").value(2))
                .andExpect(jsonPath("$.data.lineItems[*].categoryId", Matchers.containsInAnyOrder(category1.getId().intValue(),category2.getId().intValue())))
                .andExpect(jsonPath("$.data.lineItems[*].category", Matchers.containsInAnyOrder("Food","Water")))
                .andExpect(jsonPath("$.data.lineItems[*].budgetId", Matchers.containsInAnyOrder(budget.getId().intValue(),budget.getId().intValue())))
                .andExpect(jsonPath("$.data.lineItems[*].projectedAmount", Matchers.containsInAnyOrder(2000.00,2500.00)))
                .andExpect(jsonPath("$.data.lineItems[*].displayProjectedAmount", Matchers.containsInAnyOrder(currency.getSymbol(locale) + "2,000.00",currency.getSymbol(locale) + "2,500.00")))
                .andExpect(jsonPath("$.data.lineItems[*].totalAmountSpentSoFar", Matchers.containsInAnyOrder(0.00,0.00)))
                .andExpect(jsonPath("$.data.lineItems[*].displayTotalAmountSpentSoFar", Matchers.containsInAnyOrder(currency.getSymbol(locale) + "0.00",currency.getSymbol(locale) + "0.00")))
                .andExpect(jsonPath("$.data.lineItems[*].percentageSpentSoFar", Matchers.containsInAnyOrder(0.0,0.0)))
                .andExpect(jsonPath("$.data.lineItems[*].displayPercentageSpentSoFar", Matchers.containsInAnyOrder("0.0%","0.0%")));
    }


    @Test
    void shouldReturnEmptyCollectionWhenBudgetLineItemDoesNotExist() throws Exception {

        LocalDate today = LocalDate.now();

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        Budget budget = new Budget();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));

        user.addBudget(budget);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lineItems.size()").value(0));
    }


    @Test
    void testShouldReturnListOfBudgetsSuccesfullyWhenAUserHasBudget() throws Exception {

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056355667");
        userRepository.save(user);


        Budget budget = new Budget();
        budget.setTitle("Transport");
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget.setProjectedAmount(BigDecimal.valueOf(400000));
        budget.setBudgetStartDate(LocalDate.now().minusDays(1));
        budget.setBudgetEndDate(LocalDate.MAX);
        budget.setUser(user);
        budget.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget);

        Budget budget1 = new Budget();
        budget1.setTitle("Education");
        budget1.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget1.setProjectedAmount(BigDecimal.valueOf(400000));
        budget1.setBudgetStartDate(LocalDate.now());
        budget1.setBudgetEndDate(LocalDate.MAX);
        budget1.setUser(user);
        budget1.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget1);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("o2g@gmail.com");

        String token = jwtUtil.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);


        this.mockMvc
                .perform(get(path + "/budgets")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInRelativeOrder(budget.getId().intValue(), budget1.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].id").value(budget.getId()))
                .andExpect(jsonPath("$.data.content[0].title").value("Transport"))
                .andExpect(jsonPath("$.data.content[0].totalAmountSpentSoFar").value(200000.00))
                .andExpect(jsonPath("$.data.content[0].displayTotalAmountSpentSoFar").value(currency.getSymbol(locale) + "200,000.00"))
                .andExpect(jsonPath("$.data.content[0].projectedAmount").value(400000.00))
                .andExpect(jsonPath("$.data.content[0].displayProjectedAmount").value(currency.getSymbol(locale) + "400,000.00"))
                .andExpect(jsonPath("$.data.content[0].period").value(ANNUAL.name()))
                .andExpect(jsonPath("$.data.content[0].percentageSpentSoFar").value(50.00))
                .andExpect(jsonPath("$.data.content[0].displayPercentageSpentSoFar").value("50.0%"));
    }


    @Test
    void testShouldReturnEmptyListOfBudgetsSuccesfullyWhenAUserHasNoBudget() throws Exception {

        User user = new User();
        user.setEmail("o4g@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056357667");
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("o4g@gmail.com");

        String token = jwtUtil.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);


        this.mockMvc
                .perform(get(path + "/budgets")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }


    @Test
    void testShouldReturnBudgetThatBelongsToALogginUserSuccessfully() throws Exception {

        User user = new User();
        user.setEmail("o5g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        userRepository.save(user);

        Budget budget = new Budget();
        budget.setTitle("Transport");
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget.setProjectedAmount(BigDecimal.valueOf(400000));
        budget.setBudgetStartDate(LocalDate.now());
        budget.setBudgetEndDate(LocalDate.MAX);
        budget.setUser(user);
        budget.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget);


        User user2 = new User();
        user2.setEmail("o7g@gmail.com");
        user2.setPassword(passwordEncoder.encode("123456789"));
        user2.setFirstName("Goodluck");
        user2.setLastName("Nwoko");
        user2.setPhoneNumber("07050359667");
        userRepository.save(user2);

        Budget budget2 = new Budget();
        budget2.setTitle("Utiliy");
        budget2.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget2.setProjectedAmount(BigDecimal.valueOf(400000));
        budget2.setBudgetStartDate(LocalDate.now());
        budget2.setBudgetEndDate(LocalDate.now().plusDays(30));
        budget2.setUser(user2);
        budget2.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget2);

        Budget budget3 = new Budget();
        budget3.setTitle("Utiliy");
        budget3.setTotalAmountSpentSoFar(BigDecimal.valueOf(300000));
        budget3.setProjectedAmount(BigDecimal.valueOf(400000));
        budget3.setBudgetStartDate(LocalDate.now());
        budget3.setBudgetEndDate(LocalDate.MAX);
        budget3.setUser(user2);
        budget3.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget3);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("o5g@gmail.com");
        String token = jwtUtil.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);


        this.mockMvc
                .perform(get(path + "/budgets")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(1))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInAnyOrder(budget.getId().intValue())));

        UserDetails userDetails2 = customUserDetailsService.loadUserByUsername("o7g@gmail.com");
        String token2 = jwtUtil.generateToken(userDetails2);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token2);


        this.mockMvc
                .perform(get(path + "/budgets")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInAnyOrder(budget2.getId().intValue(), budget3.getId().intValue())));
    }

    @Test
    void testShouldNotListPastBudgetWhenListingActiveBudget() throws Exception {

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        userRepository.save(user);

        Budget budget = new Budget();
        budget.setTitle("Transport");
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget.setProjectedAmount(BigDecimal.valueOf(400000));
        budget.setBudgetStartDate(LocalDate.now().minusDays(30));
        budget.setBudgetEndDate(LocalDate.now().minusDays(2));
        budget.setUser(user);
        budget.setBudgetPeriod(ANNUAL);
        budgetRepository.save(budget);


        UserDetails userDetails = customUserDetailsService.loadUserByUsername("o2g@gmail.com");
        String token = jwtUtil.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);


        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.CURRENT.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }

    @Test
    void testShouldNotListUpcommingWhenListingActiveBudget() throws Exception {

        Budget budget = budget(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().plusDays(4), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.CURRENT.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }


    @Test
    void testShouldListCurrentBudgetSuccessfully() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(2), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().minusDays(3), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.CURRENT.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }


    @Test
    void testShouldNotListCurrentWhenListingPastBudget() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().minusDays(2), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.PAST.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }

    @Test
    void testShouldNotListUpcommingWhenListingPastBudget() throws Exception {

        Budget budget = budget(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.PAST.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }


    @Test
    void testShouldListPastBudgetSuccessfully() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
        Budget budget2 = budget(LocalDate.now().minusDays(15), LocalDate.now().minusDays(5));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.PAST.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }


    @Test
    void testShouldNotListCurrentWhenListingUpcommingBudget() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().minusDays(2), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMING.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));
    }

    @Test
    void testShouldNotListPastWhenListingUpcommingBudget() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
        Budget budget2 = budget(LocalDate.now().minusDays(15), LocalDate.now().minusDays(5));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMING.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));

    }


    @Test
    void testShouldListUpcommingBudgetSuccessfully() throws Exception {

        Budget budget = budget(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Budget budget2 = budget(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20));

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        userRepository.save(user);

        setAuthHeader(user);

        this.mockMvc
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMING.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }

    @Test
    void testShouldReturnPaginatedListBudget() throws Exception {

        Budget budget = budget(LocalDate.now().minusDays(30), LocalDate.now().plusDays(1));
        budget.setTitle("Education");
        Budget budget2 = budget(LocalDate.now().minusDays(25), LocalDate.now().plusDays(1));
        budget2.setTitle("Food");
        Budget budget3 = budget(LocalDate.now().minusDays(22), LocalDate.now().plusDays(1));
        budget3.setTitle("Cloth");
        Budget budget4 = budget(LocalDate.now().minusDays(19), LocalDate.now().plusDays(1));
        budget4.setTitle("Land");

        User user = new User();
        user.setEmail("o2g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056389667");
        user.addBudget(budget);
        user.addBudget(budget2);
        user.addBudget(budget3);
        user.addBudget(budget4);
        userRepository.save(user);

        setAuthHeader(user);


        this.mockMvc
                .perform(get(path + "/budgets").param("page", "1").param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInRelativeOrder(budget.getId().intValue(), budget2.getId().intValue())));

        this.mockMvc
                .perform(get(path + "/budgets").param("page", "2").param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2))
                .andExpect(jsonPath("$.data.content[*].id", Matchers.containsInRelativeOrder(budget3.getId().intValue(), budget4.getId().intValue())));
    }

    @Test
    void shouldThrowInvalidRequestWhenTryingToUpdateBudgetAndBudgetAmountLessLineItemsTotalAmount() throws Exception {

        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1));
        budget.setUser(user);
        budget.setTitle("title");
        budget = this.budgetRepository.save(budget);
        budget.setProjectedAmount(BigDecimal.valueOf(750));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200));

        BudgetCategory category1=TestModels.budgetCategory("Transport");
        category1.setUser(user);
        BudgetCategory category2=TestModels.budgetCategory("Food");
        category2.setUser(user);
        BudgetCategory category3=TestModels.budgetCategory("Health");
        category3.setUser(user);

        budgetCategoryRepository.saveAll(List.of(category1,category2,category3));

        budget.addBudgetLineItem(category1,BigDecimal.valueOf(250));
        budget.addBudgetLineItem(category2,BigDecimal.valueOf(250));
        budget.addBudgetLineItem(category3,BigDecimal.valueOf(250));
        this.budgetRepository.save(budget);

        //input
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        CreateBudgetRequestDTO dto = new CreateBudgetRequestDTO();
                dto.setTitle("New Title");
                dto.setDescription("New Description");
                dto.setAmount(BigDecimal.valueOf(300));
                dto.setPeriod(ANNUAL.name());
        dto.setMonth((short) ym.getMonthValue());
        dto.setYear((short) ym.getYear());
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(put(path + "/budgets/{budgetId}", budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowInvalidRequestWhenUpdateBudgetAndExpenseExistOutsideNewPeriod() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget( MONTHLY, LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 31));
        budget.setUser(user);
        budget.setTitle("title");
        budget.setProjectedAmount(BigDecimal.valueOf(500));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(250));
        budget = this.budgetRepository.save(budget);
        //add budget line item
        BudgetCategory category=TestModels.budgetCategory("Health");
        category.setUser(user);
        budgetCategoryRepository.save(category);
        budget.addBudgetLineItem(category,BigDecimal.valueOf(250));
        budgetRepository.save(budget);
        //add expense
        var lineItem = budget.getBudgetLineItem(category);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(250), LocalDate.of(2022, 1, 2));
        expense1.setBudgetLineItem(lineItem);
        expenseRepository.save(expense1);
        //input
        CreateBudgetRequestDTO dto = new CreateBudgetRequestDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setAmount(BigDecimal.valueOf(300));
        dto.setPeriod(WEEKLY.name());
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 2, 1), DateDisplayConstants.DATE_INPUT_FORMAT));
        dto.setDuration(1);
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(put(path + "/budgets/{budgetId}", budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateMonthlyBudgetToAnnualBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget(MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1));
        budget.setUser(user);
        budget.setTitle("title");
        budget.setProjectedAmount(BigDecimal.valueOf(500));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(250));
        budget = this.budgetRepository.save(budget);

        BudgetCategory category=TestModels.budgetCategory("Health");
        category.setUser(user);
        budgetCategoryRepository.save(category);
        budget.addBudgetLineItem(category,BigDecimal.valueOf(250));
        budgetRepository.save(budget);
        //add expense
        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(250), LocalDate.now().plusDays(3));
        expense1.setBudgetLineItem(budget.getBudgetLineItem(category));
        expenseRepository.save(expense1);
        //input
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        CreateBudgetRequestDTO dto = new CreateBudgetRequestDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setAmount(BigDecimal.valueOf(250));
        dto.setPeriod(ANNUAL.name());
        dto.setYear((short) ym.getYear());
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(put(path + "/budgets/{budgetId}", budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));
    }

    @Test
    void shouldUpdateWeeklyBudgetToMonthlyBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget(WEEKLY, LocalDate.now(), LocalDate.now().plusWeeks(1));
        budget.setUser(user);
        budget.setTitle("title");
        budget.setProjectedAmount(BigDecimal.valueOf(250));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(250));
        budget = this.budgetRepository.save(budget);

        BudgetCategory category=TestModels.budgetCategory("Health");
        category.setUser(user);
        budgetCategoryRepository.save(category);
        budget.addBudgetLineItem(category,BigDecimal.valueOf(250));
        budgetRepository.save(budget);
        //add expense
        //input
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        CreateBudgetRequestDTO dto = new CreateBudgetRequestDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setAmount(BigDecimal.valueOf(250));
        dto.setPeriod(MONTHLY.name());
        dto.setMonth((short) ym.getMonthValue());
        dto.setYear((short) ym.getYear());
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(put(path + "/budgets/{budgetId}", budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        Budget updatedBudget = this.budgetRepository.findById(budget.getId()).get();
        assertEquals(MONTHLY, updatedBudget.getBudgetPeriod());
    }

    @Test
    void shouldUpdateToCustomBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget(WEEKLY, LocalDate.of(2022, 3, 1), LocalDate.of(2022, 3, 7));
        budget.setTitle("title");
        budget.setUser(user);
        budget.setProjectedAmount(BigDecimal.valueOf(500));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(250));
        budget = this.budgetRepository.save(budget);

        BudgetCategory category=TestModels.budgetCategory("Health");
        category.setUser(user);
        budgetCategoryRepository.save(category);
        budget.addBudgetLineItem(category,BigDecimal.valueOf(250));
        budgetRepository.save(budget);
        //add expense
        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(250), LocalDate.of(2022, 3, 5));
        expense1.setBudgetLineItem(budget.getBudgetLineItem(category));
        expenseRepository.save(expense1);
        //input
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        CreateBudgetRequestDTO dto = new CreateBudgetRequestDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setAmount(BigDecimal.valueOf(250));
        dto.setPeriod(CUSTOM.name());
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), DateDisplayConstants.DATE_INPUT_FORMAT));
        dto.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 4, 30), DateDisplayConstants.DATE_INPUT_FORMAT));
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(put(path + "/budgets/{budgetId}", budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        Budget updatedBudget = this.budgetRepository.findById(budget.getId()).get();
        assertEquals(CUSTOM, updatedBudget.getBudgetPeriod());
        assertEquals(LocalDate.of(2022, 1, 1), updatedBudget.getBudgetStartDate());
        assertEquals(LocalDate.of(2022, 4, 30), updatedBudget.getBudgetEndDate());
        assertEquals(dto.getTitle(), updatedBudget.getTitle());
        assertEquals(dto.getDescription(), updatedBudget.getDescription());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTryingToFetchBudgetAndDoesNotExist() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(get(path + "/budgets/{budgetId}/edit", 0L)
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldThrowNotFoundWhenTryingToFetchBudgetAndBudgetDoesNotBelongToUser() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        User user2 = TestModels.user("John", "Doe", "user2@gmail.com", "password","08137640746");
        user2 = this.userRepository.save(user2);

        Budget budget = TestModels.budget( CUSTOM, LocalDate.now(), LocalDate.now().plusMonths(1));
        budget.setUser(user);
        budget.setTitle("title");
        budget.setProjectedAmount(BigDecimal.valueOf(500));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200));
        budget = this.budgetRepository.save(budget);

        //act
        buildHeader(user2.getEmail());
        this.mockMvc
                .perform(get(path + "/budgets/{budgetId}/fetch", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFetchBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget( DAILY, LocalDate.now(), LocalDate.now());
        budget.setUser(user);
        budget.setTitle("title");
        budget.setDescription("Description");
        budget.setProjectedAmount(BigDecimal.valueOf(500));
        budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(200));
        budget = this.budgetRepository.save(budget);
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(get(path + "/budgets/edit/{budgetId}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(RESOURCE_RETRIEVED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.title").value(budget.getTitle()))
                .andExpect(jsonPath("$.data.period").value(budget.getBudgetPeriod().name()))
                .andExpect(jsonPath("$.data.description").value(budget.getDescription()));
    }


    @Test
    void shouldFetchCustomBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = this.fetchTestBudget( CUSTOM, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT),
                "endDate", CustomDateUtil.formatLocalDateToString(LocalDate.now().plusMonths(1), DateDisplayConstants.DATE_INPUT_FORMAT)));

        budget = this.fetchTestBudget( CUSTOM, LocalDate.of(1985,3,7), LocalDate.of(1985,9,20),user);

        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.of(1985,3,7), DateDisplayConstants.DATE_INPUT_FORMAT),
                "endDate", CustomDateUtil.formatLocalDateToString(LocalDate.of(1985,9,20), DateDisplayConstants.DATE_INPUT_FORMAT)));
    }

    @Test
    void shouldFetchDailyBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = this.fetchTestBudget( DAILY, LocalDate.now(), LocalDate.now(),user);
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT),
                "endDate", CustomDateUtil.formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT)));

        budget = this.fetchTestBudget( DAILY, LocalDate.of(2001,4,23), LocalDate.of(2001,4,23),user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.of(2001,4,23), DateDisplayConstants.DATE_INPUT_FORMAT),
                "endDate", CustomDateUtil.formatLocalDateToString(LocalDate.of(2001,4,23), DateDisplayConstants.DATE_INPUT_FORMAT)));
    }

    @Test
    void shouldFetchWeeklyBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = this.fetchTestBudget( WEEKLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.now(), DateDisplayConstants.DATE_INPUT_FORMAT),
                "duration", 4 ));
        budget = this.fetchTestBudget( WEEKLY, LocalDate.of(2004,3,1), LocalDate.of(2004,3,15),user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "startDate", CustomDateUtil.formatLocalDateToString(LocalDate.of(2004,3,1), DateDisplayConstants.DATE_INPUT_FORMAT),
                "duration", 2 ));
    }

    @Test
    void shouldFetchMonthlyBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "month", LocalDate.now().getMonthValue(),
                "year", LocalDate.now().getYear()));

        budget = this.fetchTestBudget( MONTHLY, LocalDate.of(1999,12,1), LocalDate.of(1999,12, 31),user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of(
                "month", LocalDate.of(1999,12,1).getMonthValue(),
                "year", LocalDate.of(1999,12,1).getYear()));

    }

    @Test
    void shouldFetchAnnualBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "user1@gmail.com", "password","08137640746");
        user = this.userRepository.save(user);
        // Budget 1
        Budget budget = this.fetchTestBudget(ANNUAL, LocalDate.now(), LocalDate.now().plusMonths(1), user);
        //act
        this.assertFetchObjectsSuccessfully(budget, user, Map.of("year", LocalDate.now().getYear()));
        // Budget 2
        budget = this.fetchTestBudget( ANNUAL, LocalDate.of(2000,1, 1), LocalDate.of(2000,12, 31), user);
        this.assertFetchObjectsSuccessfully(budget, user, Map.of("year", 2000));
    }

    private void assertFetchObjectsSuccessfully(Budget budget, User user, Map<String, Object> expectedResult) throws Exception {
        buildHeader(user.getEmail());

        var requestBuilder = this.mockMvc
                .perform(get(path + "/budgets/edit/{budgetId}", budget.getId())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        switch (budget.getBudgetPeriod()) {
            case CUSTOM, DAILY ->
                    requestBuilder
                            .andExpect(jsonPath("$.data.budgetStartDate").value(expectedResult.get("startDate")))
                            .andExpect(jsonPath("$.data.budgetEndDate").value(expectedResult.get("endDate")));
            case WEEKLY -> requestBuilder
                    .andExpect(jsonPath("$.data.budgetStartDate").value(expectedResult.get("startDate")))
                    .andExpect(jsonPath("$.data.duration").value(expectedResult.get("duration")));
            case MONTHLY -> requestBuilder
                    .andExpect(jsonPath("$.data.month").value(expectedResult.get("month")))
                    .andExpect(jsonPath("$.data.year").value(expectedResult.get("year")));
            case ANNUAL -> requestBuilder
                    .andExpect(jsonPath("$.data.year").value(expectedResult.get("year")));
        }
    }

    private Budget fetchTestBudget(BudgetPeriod period, LocalDate startDate, LocalDate endDate, User user){
        Budget budget = TestModels.budget( period, startDate, endDate);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }



}
