package com.decagon.decapay.controller;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.JwtUtil;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class BudgetControllerTest {

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

	@Value("${api.basepath-api}")
	private String path = "";

	ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


	@Test
	void createBudgetSucceedsWithCustomPeriod() throws Exception {
		assertCreateBudgetWithPeriod("CUSTOM");
	}

	@Test
	void createBudgetSucceedsWithDailyPeriod() throws Exception {
		assertCreateBudgetWithPeriod("DAILY");
	}

	@Test
	void createBudgetSucceedsWithWeeklyPeriod() throws Exception {
		assertCreateBudgetWithPeriod("WEEKLY");
	}

	@Test
	void createBudgetSucceedsWithMonthlyPeriod() throws Exception {
		assertCreateBudgetWithPeriod("MONTHLY");
	}

	@Test
	void createBudgetSucceedsWithAnnualPeriod() throws Exception {
		assertCreateBudgetWithPeriod("ANNUAL");
	}

	@Test
	void createBudgetFailsWhenUserNotAuthenticated() throws Exception {
		CreateBudgetRequestDTO budgetRequest =
			new CreateBudgetRequestDTO("Title", BigDecimal.TEN, "CUSTOM", LocalDate.of(2022, 01, 01),
				LocalDate.of(2022, 01, 02), "des");

		mockMvc.perform(
			post(path + "/user/budget").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(budgetRequest))).andExpect(status().isForbidden());
	}

	@Test
	void createBudgetFailsWhenInvalidDatesForCustomPeriod() throws Exception {
		addAuthorizationHeader();

		CreateBudgetRequestDTO budgetRequest =
			new CreateBudgetRequestDTO("Title", BigDecimal.TEN, "CUSTOM", LocalDate.of(2022, 01, 01),
				LocalDate.of(2022, 01, 01), "des");

		mockMvc.perform(
			post(path + "/user/budget").headers(headers).contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(budgetRequest))).andExpect(status().is(400));
	}

	@Test
	void createBudgetFailsWhenUserNotActive() throws Exception {
		User user = new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("a@b.com");
		user.setPassword(passwordEncoder.encode("Password1!"));
		user.setPhoneNumber("0123456789");
		user.setUserStatus(UserStatus.INACTIVE);

		user = userRepository.save(user);

		String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));

		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + token);

		CreateBudgetRequestDTO budgetRequest =
			new CreateBudgetRequestDTO("Title", BigDecimal.TEN, "CUSTOM", LocalDate.of(2022, 01, 01),
				LocalDate.of(2022, 01, 01), "des");

		mockMvc.perform(
			post(path + "/user/budget").headers(headers).contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(budgetRequest))).andExpect(status().is(400));
	}

	void assertCreateBudgetWithPeriod(String period) throws Exception {
		addAuthorizationHeader();
		CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();

		budgetRequest.setTitle("Title");
		budgetRequest.setPeriod(period);
		budgetRequest.setAmount(BigDecimal.TEN);
		if("CUSTOM".equals(period)) {
			budgetRequest.setBudgetStartDate(LocalDate.of(2022, 1, 1));
			budgetRequest.setBudgetEndDate(LocalDate.of(2022, 1, 2));
		}

		mockMvc.perform(
			post(path + "/user/budget").headers(headers).contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(budgetRequest))).andExpect(status().isCreated());

		List<Budget> budgets = budgetRepository.findAll();
		assertEquals(1, budgets.size());
		Budget budget = budgets.get(0);
		assertEquals(budgetRequest.getTitle(), budget.getTitle());
		assertEquals(budgetRequest.getDescription(), budget.getDescription());
		assertEquals(budgetRequest.getPeriod(), budget.getBudgetPeriod().toString());
		assertEquals(0, budget.getProjectedAmount().compareTo(budgetRequest.getAmount()));

		switch (budget.getBudgetPeriod()) {
			case CUSTOM -> {
				assertEquals(budgetRequest.getBudgetStartDate(), budget.getBudgetStartDate());
				assertEquals(budgetRequest.getBudgetEndDate(), budget.getBudgetEndDate());
			}
			case DAILY -> {
				assertEquals(LocalDate.now(), budget.getBudgetStartDate());
				assertEquals(LocalDate.now(), budget.getBudgetEndDate());
			}
			case WEEKLY -> {
				assertEquals(LocalDate.now(), budget.getBudgetStartDate());
				assertEquals(LocalDate.now().with(DayOfWeek.SATURDAY), budget.getBudgetEndDate());
			}
			case MONTHLY -> {
				assertEquals(LocalDate.now(), budget.getBudgetStartDate());
				assertEquals(LocalDate.now().with(lastDayOfMonth()), budget.getBudgetEndDate());
			}
			default -> {
				assertEquals(LocalDate.now(), budget.getBudgetStartDate());
				assertEquals(LocalDate.now().with(lastDayOfYear()), budget.getBudgetEndDate());
			}
		}
	}

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
}
