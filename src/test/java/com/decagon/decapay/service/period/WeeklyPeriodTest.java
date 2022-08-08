package com.decagon.decapay.service.period;

import com.decagon.decapay.DTO.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.service.budget.periodHandler.WeeklyPeriodHandler;
import com.decagon.decapay.utils.CustomDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class WeeklyPeriodTest {

    @InjectMocks
    WeeklyPeriodHandler weeklyPeriodHandler;

    static CreateBudgetRequestDTO req(String startDate, int duration) {

        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setDescription("des");
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        budgetRequest.setBudgetStartDate(startDate);
        budgetRequest.setBudgetEndDate(null);
        budgetRequest.setYear((short) 0);
        budgetRequest.setMonth((short) 0);
        budgetRequest.setDuration(duration);
        return budgetRequest;
    }

    @ParameterizedTest
    @MethodSource("invalidRequestProvider")
    void testThrow400WhenCreateBudgetWithInvalidPeriodInputRequest(CreateBudgetRequestDTO dto) throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            weeklyPeriodHandler.validateRequest(dto);
        });
    }

    /*
     for weekly period, start date and duration required and
     duration must be a positive number
    */
    static Stream<Arguments> invalidRequestProvider() {
        String pattern = DateDisplayConstants.DATE_INPUT_FORMAT;
        return Stream.of(
                //invalid start date field
                arguments(req(null, 1)),
                arguments(req("", 1)),
                //invalid duration:0
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern), 0)),
                //invalid start date format
                arguments(req("1200-08-09", 1)),
                arguments(req("1200/08/09", 1))
        );
    }

    /*
     test calculated date range for weekly budget
     */
    @ParameterizedTest
    @MethodSource("requestDtoProvider")
    void testShouldCalculateBudgetDateRangeSuccessfully(CreateBudgetRequestDTO dto, LocalDate[] expectedRange) throws Exception {
        LocalDate[] dateRange = weeklyPeriodHandler.calculateBudgetDateRange(dto);
        assertEquals(expectedRange[0], dateRange[0]);
        assertEquals(expectedRange[1], dateRange[1]);
    }

    static Stream<Arguments> requestDtoProvider() {
        String pattern = DateDisplayConstants.DATE_INPUT_FORMAT;
        String requestDate = CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern);
        return Stream.of(
                //startdate:2022-01-01, duration:1 expected: 2022-01-08
                arguments(req(requestDate, 1),new LocalDate[]{LocalDate.of(2022, 1, 1),LocalDate.of(2022, 1, 8)} ),
                //startdate:2022-01-01, duration:2 expected: 2022-01-15
                arguments(req(requestDate, 1),new LocalDate[]{LocalDate.of(2022, 1, 1),LocalDate.of(2022, 1, 8)} )
        );
    }


    @Test
    void testing() throws Exception {

        String s=CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 10, 12),DateDisplayConstants.DATE_INPUT_FORMAT);
        weeklyPeriodHandler.validateRequest(req(s,1));
    }


}
