package com.decagon.decapay.integration.budget;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.TxnManager;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class CreateBudgetTest {

    @Value("${api.basepath-api}")
    private String path = "";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    BudgetRepository budgetRepository;

    private HttpHeaders headers;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    TxnManager txnManager;


    void addAuthorizationHeader() {
        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0123456789");
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    void addAuthorizationHeader(User user) {
        String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }


    CreateBudgetRequestDTO budgetRequest() {
        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setDescription("des");
        return budgetRequest;
    }

    @Test
    void createBudgetFailsWhenUserNotAuthenticated() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        mockMvc.perform(
                post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                        TestUtils.asJsonString(budgetRequest))).andExpect(status().isUnauthorized());
    }

    @Test
    void createBudgetFailsWhenUserNotActive() throws Exception {
        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0123456789");
        user.setUserStatus(UserStatus.ACTIVE);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        mockMvc.perform(
                post(path + "/budgets").headers(headers).contentType(MediaType.APPLICATION_JSON).content(
                        TestUtils.asJsonString(budgetRequest))).andExpect(status().is(400));
    }


    @Test
    void createBudgetSucceedsWithCustomPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.CUSTOM.name());
        budgetRequest.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), DateDisplayConstants.DATE_INPUT_FORMAT));
        budgetRequest.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 2), DateDisplayConstants.DATE_INPUT_FORMAT));
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    @Test
    void createBudgetSucceedsWithDailyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.DAILY.name());
        String startDate = CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), DateDisplayConstants.DATE_INPUT_FORMAT);
        budgetRequest.setBudgetStartDate(startDate);
        budgetRequest.setBudgetEndDate(startDate);//same date
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    @Test
    void createBudgetSucceedsWithCurrentMonthYearForMonthlyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.MONTHLY.name());
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        budgetRequest.setMonth((short) ym.getMonthValue());//curr month
        budgetRequest.setYear((short) ym.getYear());//curr yr
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    @Test
    void createBudgetSucceedsWithNonCurrentMonthForMonthlyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.MONTHLY.name());
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        budgetRequest.setMonth(TestUtils.getNonCurrentMonth(((short) ym.getMonthValue()) + 1));//non curr month
        budgetRequest.setYear((short) ym.getYear());//curr yr
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    @Test
    void createBudgetSucceedsWithCurrentYearForAnnualPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        budgetRequest.setYear((short) ym.getYear());//curr yr
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    @Test
    void createBudgetSucceedsWithNonCurrentYearForAnnualPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        budgetRequest.setYear((short) (ym.getYear() + 1));//non curr yr
        assertCreateBudgetWithPeriod(budgetRequest);
    }


    @Test
    void createBudgetSucceedsWithWeeklyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = this.budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.WEEKLY.name());
        String startDate = CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), DateDisplayConstants.DATE_INPUT_FORMAT);
        budgetRequest.setBudgetStartDate(startDate);
        budgetRequest.setDuration(1);//same date
        assertCreateBudgetWithPeriod(budgetRequest);
    }

    void assertCreateBudgetWithPeriod(CreateBudgetRequestDTO budgetRequest) throws Exception {

        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("a@b.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setPhoneNumber("0123456789");
        user = userRepository.save(user);
        addAuthorizationHeader(user);

        mockMvc.perform(
                post(path + "/budgets").headers(headers).contentType(MediaType.APPLICATION_JSON).content(
                        TestUtils.asJsonString(budgetRequest))).andExpect(status().isCreated());

        //TODO: implement lamda for this to wrap code that needs to run in transaction
        txnManager.startTxn();
        List<Budget> budgets = budgetRepository.findAll();
        assertEquals(1, budgets.size());
        Budget budget = budgets.get(0);
        assertEquals(budgetRequest.getTitle(), budget.getTitle());
        assertEquals(budgetRequest.getDescription(), budget.getDescription());
        assertEquals(budgetRequest.getPeriod(), budget.getBudgetPeriod().toString());
        assertEquals(0, budget.getProjectedAmount().compareTo(budgetRequest.getAmount()));
        assertEquals(user.getId(), budget.getUser().getId());
        txnManager.endTxn();

        BudgetPeriod period = BudgetPeriod.valueOf(budgetRequest.getPeriod());
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));

        switch (period) {
            case CUSTOM, DAILY -> {
                assertEquals(CustomDateUtil.formatStringToLocalDate(budgetRequest.getBudgetStartDate(), DateDisplayConstants.DATE_INPUT_FORMAT), budget.getBudgetStartDate());
                assertEquals(CustomDateUtil.formatStringToLocalDate(budgetRequest.getBudgetEndDate(), DateDisplayConstants.DATE_INPUT_FORMAT), budget.getBudgetEndDate());
            }
            case MONTHLY -> {
                //if current month and year
                if (budgetRequest.getMonth() == (short) ym.getMonthValue() &&
                        budgetRequest.getYear() == (short) ym.getYear()) {
                    //assertEquals(CustomDateUtil.today(), budget.getBudgetStartDate());
                    assertEquals(CustomDateUtil.firstDateOfMonth(budgetRequest.getYear(), budgetRequest.getMonth()), budget.getBudgetStartDate());//todo:remove redundant conditional statement when confirm requirement changes permanently
                    assertEquals(CustomDateUtil.lastDateOfMonth(budgetRequest.getYear(), budgetRequest.getMonth()), budget.getBudgetEndDate());
                } else {
                    assertEquals(CustomDateUtil.firstDateOfMonth(budgetRequest.getYear(), budgetRequest.getMonth()), budget.getBudgetStartDate());
                    assertEquals(CustomDateUtil.lastDateOfMonth(budgetRequest.getYear(), budgetRequest.getMonth()), budget.getBudgetEndDate());
                }
            }
            case ANNUAL -> {
                if (budgetRequest.getYear() == (short) ym.getYear()) { //todo: remove redundant conditional statement
                    //assertEquals(CustomDateUtil.today(), budget.getBudgetStartDate());
                    assertEquals(CustomDateUtil.firstDateOfYear(budgetRequest.getYear()), budget.getBudgetStartDate());
                    assertEquals(CustomDateUtil.lastDateOfYear(budgetRequest.getYear()), budget.getBudgetEndDate());
                } else {
                    assertEquals(CustomDateUtil.firstDateOfYear(budgetRequest.getYear()), budget.getBudgetStartDate());
                    assertEquals(CustomDateUtil.lastDateOfYear(budgetRequest.getYear()), budget.getBudgetEndDate());
                }
            }
            case WEEKLY -> {
                assertEquals(CustomDateUtil.formatStringToLocalDate(budgetRequest.getBudgetStartDate(), DateDisplayConstants.DATE_INPUT_FORMAT), budget.getBudgetStartDate());
                assertEquals(CustomDateUtil.formatStringToLocalDate(budgetRequest.getBudgetStartDate(), DateDisplayConstants.DATE_INPUT_FORMAT).plusDays(AppConstants.NUM_DAYS_IN_WEEK * budgetRequest.getDuration()), budget.getBudgetEndDate());
            }
        }
    }


}
