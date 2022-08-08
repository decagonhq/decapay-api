package com.decagon.decapay.integration.budget;

<<<<<<< HEAD

import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import org.junit.jupiter.api.BeforeEach;
import com.decagon.decapay.model.budget.BudgetState;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import org.hamcrest.Matchers;
=======
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.request.budget.UpdateBudgetRequestDto;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.TestModels;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.UserDetails;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
>>>>>>> e263b2f (feat: Edit Budget)
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
<<<<<<< HEAD
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;



@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
=======
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static com.decagon.decapay.constants.ResponseMessageConstants.BUDGET_UPDATED_SUCCESSFULLY;
import static com.decagon.decapay.enumTypes.BudgetPeriod.ANNUAL;
import static com.decagon.decapay.enumTypes.BudgetPeriod.MONTHLY;
import static com.decagon.decapay.utils.CustomDateUtil.getDateFromMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

>>>>>>> e263b2f (feat: Edit Budget)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
<<<<<<< HEAD
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

    Locale locale = new Locale(AppConstants.DEFAULT_LANGUAGE, AppConstants.DEFAULT_COUNTRY);

    Currency currency = AppConstants.DEFAULT_CURRENCY;

    @BeforeEach
    public void runBeforeAllTestMethods() throws Exception {
=======
class BudgetTest {

    @Value("${api.basepath-user}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository repository;

    @Autowired
    private UserDetails userDetails;

    private HttpHeaders headers;

    @BeforeEach
    public void setUpBeforeEach() {
>>>>>>> e263b2f (feat: Edit Budget)
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

<<<<<<< HEAD
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
        budget.setBudgetPeriod(BudgetPeriod.ANNUAL);
        return budget;
    }


    private void setAuthHeader(User user){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
=======
    @BeforeAll
    public void setup() {
    }

    private void buildHeader(String email) {
        String token = userDetails.getLoggedInToken(email);
>>>>>>> e263b2f (feat: Edit Budget)
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
<<<<<<< HEAD
    void shouldReturn404WhenViewBudgetAndBudgetNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        setAuthHeader(user);;

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", 2).headers(headers))
=======
    void shouldThrow403ForbiddenWhenTryingToUpdateBudgetAndUserIsNotSignedIn() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Budget budget = TestModels.budget(user, MONTHLY, LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .build();
        //act
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldThrow404NotFoundWhenTryingToUpdateBudgetAndBudgetDoesNotExist() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), 0L).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
>>>>>>> e263b2f (feat: Edit Budget)
                .andExpect(status().isNotFound());
    }

    @Test
<<<<<<< HEAD
    void shouldReturn400InvalidRequestWhenAUserViewBudgetUserDidNotCreate() throws Exception {

        User user = TestModels.user("ola", "dip", "o@b.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        Budget budget = new Budget();
        LocalDate today = LocalDate.now();
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

        setAuthHeader(user1);;

        this.mockMvc.perform(get(path + "/budgets/{budgetId}", budget.getId()).headers(headers))
                .andExpect(status().isBadRequest());
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
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
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
    void testShouldReturnForbidden() throws Exception {

        User user = new User();
        user.setEmail("o3g@gmail.com");
        user.setPassword(passwordEncoder.encode("1234567"));
        user.setFirstName("Goodluck");
        user.setLastName("Nwoko");
        user.setPhoneNumber("07056355667");
        userRepository.save(user);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        this.mockMvc
                .perform(post(path + "/budgets")
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
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
        budget.setBudgetPeriod(BudgetPeriod.ANNUAL);
        budgetRepository.save(budget);

        Budget budget1 = new Budget();
        budget1.setTitle("Education");
        budget1.setTotalAmountSpentSoFar(BigDecimal.valueOf(200000));
        budget1.setProjectedAmount(BigDecimal.valueOf(400000));
        budget1.setBudgetStartDate(LocalDate.now());
        budget1.setBudgetEndDate(LocalDate.MAX);
        budget1.setUser(user);
        budget1.setBudgetPeriod(BudgetPeriod.ANNUAL);
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
                .andExpect(jsonPath("$.data.content[0].period").value(BudgetPeriod.ANNUAL.name()))
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
        budget.setBudgetPeriod(BudgetPeriod.ANNUAL);
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
        budget2.setBudgetPeriod(BudgetPeriod.ANNUAL);
        budgetRepository.save(budget2);

        Budget budget3 = new Budget();
        budget3.setTitle("Utiliy");
        budget3.setTotalAmountSpentSoFar(BigDecimal.valueOf(300000));
        budget3.setProjectedAmount(BigDecimal.valueOf(400000));
        budget3.setBudgetStartDate(LocalDate.now());
        budget3.setBudgetEndDate(LocalDate.MAX);
        budget3.setUser(user2);
        budget3.setBudgetPeriod(BudgetPeriod.ANNUAL);
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
        budget.setBudgetPeriod(BudgetPeriod.ANNUAL);
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
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMMING.name().toLowerCase())
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
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMMING.name().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(0));

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
            budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
            budget.setBudgetStartDate(today);
            budget.setBudgetEndDate(today.plusWeeks(3));
            budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
            budget.setProjectedAmount(BigDecimal.valueOf(5000));

            user.addBudget(budget);
            userRepository.save(user);

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
                    .andExpect(jsonPath("$.data.budgetPeriod").value(BudgetPeriod.MONTHLY.name()));
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
                .perform(get(path + "/budgets").param("state", BudgetState.UPCOMMING.name().toLowerCase())
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
=======
    void shouldThrowInvalidRequestWhenTryingToUpdateBudgetAndBudgetAmountDoesNotEqualLineItemsTotalAmount() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), LocalDateTime.now());

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, MONTHLY, LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(MONTHLY))
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowInvalidRequestWhenTryingToUpdateBudgetAndBudgetExpenseTransactionDateIsNotWithinNewPeriod() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), LocalDateTime.now().minusMonths(4));

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, ANNUAL, LocalDateTime.now().minusYears(1), LocalDateTime.now());
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(MONTHLY))
                .month((short) LocalDateTime.now().plusMonths(1).getMonthValue())
                .year((short) LocalDateTime.now().getYear())
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateMonthlyBudgetToAnnualBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        short currentYear = (short) LocalDateTime.now().getYear();
        short currentMonth = (short) LocalDateTime.now().getMonthValue();

        var startDate = getDateFromMonth(currentYear, currentMonth, firstDayOfMonth());
        var endDate = getDateFromMonth(currentYear, currentMonth, lastDayOfMonth());

        System.out.println("@@@@@@@@@ test month startDate: " + startDate);
        System.out.println("@@@@@@@@@ test month endDate: " + endDate);


        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), startDate.plusDays(5));
//        Expenses expense2 = TestModels.expenses(BigDecimal.valueOf(200), endDate.minusDays(3));
        System.out.println("@@@@@@@@@ test expense transanction date: " + expense1.getTransactionDate());

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, MONTHLY, startDate, endDate);
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(ANNUAL))
                .month(currentMonth)
                .year(currentYear)
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));
    }

    @Test
    void shouldUpdateAnnualBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), LocalDateTime.now().plusDays(1));

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, MONTHLY, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(MONTHLY))
                .month((short) LocalDateTime.now().getMonthValue())
                .year((short) LocalDateTime.now().getYear())
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));
    }

    @Test
    void shouldUpdateCustomBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), LocalDateTime.now().plusDays(1));

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, MONTHLY, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(MONTHLY))
                .month((short) LocalDateTime.now().getMonthValue())
                .year((short) LocalDateTime.now().getYear())
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));
    }

    @Test
    void shouldUpdateWeeklyBudgetSuccessfully() throws Exception {
        //arrange
        User user = TestModels.user("John", "Doe", "fabiane@decagonhq.com", "password","08137640746");
        user = this.userRepository.save(user);

        Expenses expense1 = TestModels.expenses(BigDecimal.valueOf(200), LocalDateTime.now().plusDays(1));

        BudgetLineItem lineItem = TestModels.budgetLineItem(Set.of(expense1), BigDecimal.valueOf(250), BigDecimal.valueOf(200));


        Budget budget = TestModels.budget(user, MONTHLY, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        budget.setTitle("title");
        budget.setBudgetPeriod(MONTHLY);
        budget.setBudgetLineItems(Set.of(lineItem));
        budget = this.repository.save(budget);

        //input
        UpdateBudgetRequestDto dto = UpdateBudgetRequestDto
                .builder()
                .title("New Title")
                .description("New Description")
                .budgetPeriod(String.valueOf(MONTHLY))
                .month((short) LocalDateTime.now().getMonthValue())
                .year((short) LocalDateTime.now().getYear())
                .projectedAmount(BigDecimal.valueOf(250))
                .totalAmountSpentSoFar(BigDecimal.valueOf(300))
                .notificationThreshold("50")
                .build();
        //act
        buildHeader(user.getEmail());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put(path + "/{userId}/budget/{budgetId}", user.getId(), budget.getId()).content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BUDGET_UPDATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));
    }

>>>>>>> e263b2f (feat: Edit Budget)
}
