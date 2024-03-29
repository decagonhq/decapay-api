package com.decagon.decapay.integration.budget;


import com.decagon.decapay.config.userSetting.UserBudgetLineItemTemplate;
import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.budget.CreateBudgetLineItemDto;
import com.decagon.decapay.dto.budget.EditBudgetLineItemDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetPeriod;
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
import com.decagon.decapay.utils.TestUtils;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.LINE_ITEM_CREATED_SUCCESSFULLY;
import static com.decagon.decapay.constants.ResponseMessageConstants.LINE_ITEM_UPDATED_SUCCESSFULLY;
import static com.decagon.decapay.constants.ResponseMessageConstants.LINE_ITEM_REMOVED_SUCCESSFULLY;
import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class BudgetLineItemTest {
    @Value("${api.basepath-api}")
    private String path;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    ObjectMapper objectMapper;

    private UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");

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

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetDoesNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");

        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", 1L)
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetCategoryDoesNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);

        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(2L);
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn405ConflictWhenTryingToCreateLineItemAndLineItemAlreadyExists() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));

        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(200));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenTryingToCreateLineItemAndBudgetDoesNotBelongToUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);
        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToCreateLineItemAndBudgetCategoryDoesNotBelongToUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToCreateLineItemAndTotalProjectAmountForBudgetIsLessThanTheExistingLineItemsTotalAmountPlusTheNewLineItemTotalAmount() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        BudgetCategory category3 = TestModels.budgetCategory("Rent");
        category3.setUser(user);

        BudgetCategory category4 = TestModels.budgetCategory("Fare");
        category4.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category, category2, category3, category4));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category3, BigDecimal.valueOf(500));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category4.getId());
        dto.setAmount(BigDecimal.valueOf(600));


        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateLineItemSuccessfully() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        user.setUserSetting(objectMapper.writeValueAsString(userSettings));

        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        BudgetCategory category3 = TestModels.budgetCategory("Rent");
        category3.setUser(user);

        BudgetCategory category4 = TestModels.budgetCategory("Fare");
        category4.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category, category2, category3, category4));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category3, BigDecimal.valueOf(500.00));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category4.getId());
        dto.setAmount(BigDecimal.valueOf(500.00));

        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_CREATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(4, budget.getBudgetLineItems().size());

        var lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(budgetLineItem -> budgetLineItem.getBudgetCategory().equals(category4))
                .findFirst()
                .get();

        assertEquals(category4.getId(), lineItem.getBudgetCategory().getId());
        assertEquals(budget.getId(), lineItem.getBudget().getId());
        assertEquals(dto.getAmount().setScale(2), lineItem.getProjectedAmount());
    }


    @Test
    void shouldCreateBudgetLineItemTemplate_WhenCreateLineItem_AndSetLineItemAsTemplateSelectedByUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        user.setUserSetting(objectMapper.writeValueAsString(userSettings));

        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        this.budgetRepository.save(budget);


        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(500.00));
        dto.setSetLineItemAsTemplate(true);

        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_CREATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(1, budget.getBudgetLineItems().size());

        //assert line item template set
        user=this.userRepository.findByEmail(user.getEmail()).get();
        UserSettings settings=objectMapper.readValue(user.getUserSetting(),UserSettings.class);
        UserBudgetLineItemTemplate budgetLineItemTemplate=settings.getUserBudgetLineItemTemplate().stream().filter(period->period.getPeriod().equals(MONTHLY)).findFirst().get();
        assertTrue(budgetLineItemTemplate.getBudgetCategories().contains(category.getId()));
    }



    @Test
    void shouldNotCreateBudgetLineItemTemplate_WhenCreateLineItem_AndSetItemAsTemplateNotSelectedByUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        UserBudgetLineItemTemplate budgetLineItemTemplate=new UserBudgetLineItemTemplate();
        budgetLineItemTemplate.setPeriod(MONTHLY);
        budgetLineItemTemplate.setBudgetCategories(new ArrayList<>());
        userSettings.addBudgetLineItemTemplateSetting(budgetLineItemTemplate);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));

        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        this.budgetRepository.save(budget);

        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(500.00));
        dto.setSetLineItemAsTemplate(false);

        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_CREATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(1, budget.getBudgetLineItems().size());

        //assert line item template not set
        user=this.userRepository.findByEmail(user.getEmail()).get();
        UserSettings settings=objectMapper.readValue(user.getUserSetting(),UserSettings.class);
        budgetLineItemTemplate=settings.getUserBudgetLineItemTemplate().stream().filter(item ->item.getPeriod().equals(MONTHLY)).findFirst().get();
        assertNotNull(budgetLineItemTemplate);
        assertFalse(budgetLineItemTemplate.getBudgetCategories().contains(category.getId()));
    }

    @Test
    void givenUserDoesNoHaveExistingSettings_WhenCreateLineItemAndSetItemAsTemplateSelected_ShouldCreateLineItemTemplate() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(null);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        this.budgetRepository.save(budget);

        CreateBudgetLineItemDto dto = new CreateBudgetLineItemDto();
        dto.setBudgetCategoryId(category.getId());
        dto.setAmount(BigDecimal.valueOf(500.00));
        dto.setSetLineItemAsTemplate(true);
        setAuthHeader(user);;

        this.mockMvc.perform(post(path + "/budgets/{budgetId}/lineItems", budget.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_CREATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.id").value(budget.getId()));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(1, budget.getBudgetLineItems().size());
        //assert line item template set
        user=this.userRepository.findByEmail(user.getEmail()).get();
        UserSettings settings=objectMapper.readValue(user.getUserSetting(),UserSettings.class);
        UserBudgetLineItemTemplate budgetLineItemTemplate=settings.getUserBudgetLineItemTemplate().stream().filter(period->period.getPeriod().equals(MONTHLY)).findFirst().get();
        assertTrue(budgetLineItemTemplate.getBudgetCategories().contains(category.getId()));
    }


    private Budget fetchTestBudget(BudgetPeriod period, LocalDate startDate, LocalDate endDate, User user){
        Budget budget = TestModels.budget( period, startDate, endDate);
        budget.setUser(user);
        return this.budgetRepository.save(budget);
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToEditLineItemAndBudgetLineItemDoesNotBelongToUser() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));


        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user2);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);


        EditBudgetLineItemDto dto = new EditBudgetLineItemDto();
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(put(path + "/budgets/{budgetId}/lineItems/{categoryId}", budget.getId(), category.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldReturn404WhenTryingToEditLineItemAndBudgetLineItemDoesNotExist() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        EditBudgetLineItemDto dto = new EditBudgetLineItemDto();
        dto.setAmount(BigDecimal.valueOf(100));


        setAuthHeader(user);;

        this.mockMvc.perform(put(path + "/budgets/{budgetId}/lineItems/{categoryId}", 1L, category.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400InvalidRequestWhenTryingToEditLineItemAndTotalProjectAmountForBudgetIsLessThanTheExistingLineItemsTotalAmountPlusTheNewLineItemTotalAmount() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        this.budgetCategoryRepository.save(category);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(2000));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000));
        this.budgetRepository.save(budget);


        EditBudgetLineItemDto dto = new EditBudgetLineItemDto();
        dto.setAmount(BigDecimal.valueOf(2600));


        setAuthHeader(user);;

        this.mockMvc.perform(put(path + "/budgets/{budgetId}/lineItems/{categoryId}", budget.getId(), category.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldEditLineItemSuccessfully() throws Exception {

        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        user.setUserSetting(objectMapper.writeValueAsString(userSettings));

        userRepository.save(user);


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        BudgetCategory category2 = TestModels.budgetCategory("Water");
        category2.setUser(user);

        this.budgetCategoryRepository.saveAll(List.of(category, category2));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(1000.00));
        this.budgetRepository.save(budget);

        var lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(item -> item.getBudgetCategory().getId().equals(category.getId())).findAny().get();


        EditBudgetLineItemDto dto = new EditBudgetLineItemDto();
        dto.setAmount(BigDecimal.valueOf(3500.00));


        setAuthHeader(user);;

        this.mockMvc.perform(put(path + "/budgets/{budgetId}/lineItems/{categoryId}", budget.getId(), category.getId())
                        .content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_UPDATED_SUCCESSFULLY));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(2, budget.getBudgetLineItems().size());

        lineItem = budget.getBudgetLineItems()
                .stream()
                .filter(item -> item.getBudgetCategory().getId().equals(category.getId())).findAny().get();

        assertEquals(category.getId(), lineItem.getBudgetCategory().getId());
        assertEquals(budget.getId(), lineItem.getBudget().getId());
        assertEquals(dto.getAmount().setScale(2), lineItem.getProjectedAmount());

        UserSettings settings = new UserSettings();
        settings.setLanguage("en");
        settings.setCountryCode("NG");
        settings.setCurrencyCode("NGN");

        user.setUserSetting(objectMapper.writeValueAsString(userSettings));
    }

    @Test
    void shouldReturn404WhenTryingToRemoveBudgetLineItemAndLineItemBudgetDoesNotBelongToUser() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);

        userRepository.saveAll(List.of(user, user2));

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Food");
        category2.setUser(user2);
        this.budgetCategoryRepository.saveAll(List.of(category, category2));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user2);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000.00));
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.assertRemoveLineItem(budget, category, status().isNotFound());
    }
    @Test
    void shouldReturn404WhenTryingToRemoveBudgetLineItemAndLineItemBudgetCategoryDoesNotBelongToUser() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        User user2 = TestModels.user("ola2", "dip2", "ola2@gmail.com",
                passwordEncoder.encode("password"), "08067644802");
        user2.setUserStatus(UserStatus.ACTIVE);

        userRepository.saveAll(List.of(user, user2));


        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);

        BudgetCategory category2 = TestModels.budgetCategory("Food");
        category2.setUser(user2);
        this.budgetCategoryRepository.saveAll(List.of(category, category2));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.assertRemoveLineItem(budget, category2, status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTryingToRemoveBudgetLineItemAndBudgetDoesNotExist() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        this.budgetCategoryRepository.save(category);

        setAuthHeader(user);
        this.assertRemoveLineItem(null, category, status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTryingToRemoveBudgetLineItemAndBudgetCategoryDoesNotExist() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        this.budgetRepository.save(budget);

        setAuthHeader(user);
        this.assertRemoveLineItem(budget, null, status().isNotFound());
    }

    @Test
    void shouldReturn400WhenTryingToRemoveBudgetLineItemAndLineItemHasExpenses() throws Exception {
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

        Expense expenses = TestModels.expenses(BigDecimal.valueOf(500.00), LocalDate.now());
        expenses.setBudgetLineItem(lineItem);
        expenseRepository.save(expenses);

        setAuthHeader(user);
        this.assertRemoveLineItem(budget, category, status().isBadRequest());
    }

    private void assertRemoveLineItem(Budget budget, BudgetCategory category, ResultMatcher expectedResult) throws Exception {
        Long budgetId = budget == null ? 1L : budget.getId();
        Long categoryId = category == null ? 1L : category.getId();
        this.mockMvc.perform(delete(path + "/budgets/{budgetId}/lineItems/{categoryId}", budgetId, categoryId).headers(headers))
                .andExpect(expectedResult);
    }

    @Test
    void shouldRemoveBudgetLineItemSuccessfully() throws Exception {
        User user = TestModels.user("ola", "dip", "ola@gmail.com",
                passwordEncoder.encode("password"), "08067644805");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setUser(user);
        BudgetCategory category2 = TestModels.budgetCategory("Food");
        category2.setUser(user);
        this.budgetCategoryRepository.saveAll(List.of(category, category2));

        Budget budget = this.fetchTestBudget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1),user);
        budget.setProjectedAmount(BigDecimal.valueOf(5000.00));
        budget.addBudgetLineItem(category, BigDecimal.valueOf(2000.00));
        budget.addBudgetLineItem(category2, BigDecimal.valueOf(2000.00));
        this.budgetRepository.save(budget);

        setAuthHeader(user);;

        assertEquals(2, budget.getBudgetLineItems().size());

        this.mockMvc.perform(delete(path + "/budgets/{budgetId}/lineItems/{categoryId}", budget.getId(), category.getId()).headers(headers))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(LINE_ITEM_REMOVED_SUCCESSFULLY));

        budget = this.budgetRepository.findBudgetWithLineItems(budget.getId(), user.getId()).get();
        assertEquals(1, budget.getBudgetLineItems().size());
    }



}
