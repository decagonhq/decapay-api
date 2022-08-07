package com.decagon.decapay.unit.controller;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.controller.BudgetController;
import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.service.BudgetService;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.utils.TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.Filter;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BudgetController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        WebSecurityConfigurer.class, Filter.class}), excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class CreateBudgetControllerTest {

    @Value("${api.basepath-api}")
    private String path = "";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BudgetService budgetService;

    CreateBudgetRequestDTO budgetRequest() {
        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setPeriod(BudgetPeriod.DAILY.name());
        budgetRequest.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), DateDisplayConstants.DATE_INPUT_FORMAT));
        budgetRequest.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 2), DateDisplayConstants.DATE_INPUT_FORMAT));
        budgetRequest.setDescription("des");
        budgetRequest.setYear((short)2022);
        budgetRequest.setMonth((short)8);
        budgetRequest.setDuration(1);
        return budgetRequest;
    }


    @Test
    void createBudgetFailsWithInvalidRequest() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();

        //invalid empty fields request
        budgetRequest.setPeriod("");
        budgetRequest.setTitle("");
        assertFailWithInvalidRequest(budgetRequest, "title", "period");

        //invalid null fields request
        budgetRequest.setPeriod(null);
        budgetRequest.setTitle(null);
        budgetRequest.setAmount(null);
        assertFailWithInvalidRequest(budgetRequest, "title", "period","amount");

        //invalid size fields
        budgetRequest = budgetRequest();
        budgetRequest.setTitle(RandomStringUtils.randomAlphabetic(SchemaConstants.BUDGET_TITLE_SIZE+1));
        budgetRequest.setDescription(RandomStringUtils.randomAlphabetic(SchemaConstants.BUDGET_DESC_SIZE+1));
        assertFailWithInvalidRequest(budgetRequest, "title", "description");

        //invalid enum format fields
        budgetRequest = budgetRequest();
        budgetRequest.setPeriod("INVALIDENUM");
        assertFailWithInvalidRequest(budgetRequest, "period");

        //invalid amount field
        budgetRequest = budgetRequest();
        budgetRequest.setAmount(BigDecimal.valueOf(0));
        assertFailWithInvalidRequest(budgetRequest, "amount");
    }

    void assertFailWithInvalidRequest(CreateBudgetRequestDTO budgetRequest, String... invalidIputs) throws Exception {
        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest))).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.subErrors[*].field", Matchers.hasItems(invalidIputs)));

    }

    @Test
    void createBudgetFailsWithInvalidRequestForCustomPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.CUSTOM.name());
        budgetRequest.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 01, 01), DateDisplayConstants.DATE_INPUT_FORMAT));
        budgetRequest.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 01, 01), DateDisplayConstants.DATE_INPUT_FORMAT));
        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest)))
                .andExpect(status().is(400))
                .andDo(print());
    }

    @Test
    void createBudgetFailsWithInvalidRequestForDailyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.DAILY.name());
        //invalid different dates
        budgetRequest.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 01, 01), DateDisplayConstants.DATE_INPUT_FORMAT));
        budgetRequest.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 01, 03), DateDisplayConstants.DATE_INPUT_FORMAT));

        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest)))
                .andExpect(status().is(400))
                .andDo(print());
    }

    @Test
    void createBudgetFailsWithInvalidRequestForAnnualPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        //invalid year given
        budgetRequest.setYear((short) 0);
        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest)))
                .andExpect(status().is(400))
                .andDo(print());
    }

    @Test
    void createBudgetFailsWithInvalidRequestForMonthlyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.MONTHLY.name());
        //invalid month and year
        budgetRequest.setMonth((short) 0);
        budgetRequest.setYear((short) 0);

        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest)))
                .andExpect(status().is(400))
                .andDo(print());
    }

    @Test
    void createBudgetFailsWithInvalidRequestForWeeklyPeriod() throws Exception {
        CreateBudgetRequestDTO budgetRequest = budgetRequest();
        budgetRequest.setPeriod(BudgetPeriod.WEEKLY.name());
        //invalid start date
        budgetRequest.setBudgetStartDate(null);
        budgetRequest.setDuration(1);

        mockMvc.perform(
                        post(path + "/budgets").contentType(MediaType.APPLICATION_JSON).content(
                                TestUtils.asJsonString(budgetRequest)))
                .andExpect(status().is(400))
                .andDo(print());
    }

}
