package com.decagon.decapay.service.period;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.service.budget.periodHandler.DailyPeriodHandler;
import com.decagon.decapay.utils.CustomDateUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class DailyPeriodTest {

    @InjectMocks
    DailyPeriodHandler dailyPeriod;

    static CreateBudgetRequestDTO req(String startDate, String endDate){

        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setPeriod(BudgetPeriod.DAILY.name());
        budgetRequest.setBudgetStartDate(startDate);
        budgetRequest.setBudgetEndDate(endDate);
        budgetRequest.setDescription("des");
        budgetRequest.setYear((short)0);
        budgetRequest.setMonth((short)0);
        return budgetRequest;
    }

    /*
     test invalid request
     */
    @ParameterizedTest
    @MethodSource("invalidRequestDtoProvider")
    void testThrow400WhenCreateBudgetWithInvalidDateRequest(CreateBudgetRequestDTO dto) throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            dailyPeriod.validateRequest(dto);
        });

    }

    /*
    for daily period, start and end date are both required
         both must be equal
         TODO:must be of valid format
 */
    static Stream<Arguments> invalidRequestDtoProvider() {
        String pattern = DateDisplayConstants.DATE_INPUT_FORMAT;
        return Stream.of(
                //invalid different dates
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern), CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 3), pattern))),
                //start date cannot be null
                arguments(req(null, pattern), CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern)),
                //end date can be null
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern), null)),
                //both start and end date cannot be null
                arguments(req(null, null))
        );
    }


    /*
     test calculated date range for daily budget
     */
    @ParameterizedTest
    @MethodSource("requestDtoProvider")
    void testShouldGetPeriodDateRangeSuccessfully(CreateBudgetRequestDTO dto, LocalDate[] expectedRange) throws Exception {
        LocalDate[] dateRange = dailyPeriod.calculateBudgetDateRange(dto);
        assertEquals(expectedRange[0], dateRange[0]);
        assertEquals(expectedRange[1], dateRange[1]);
    }
    static Stream<Arguments> requestDtoProvider() {
        LocalDate startDate=LocalDate.of(2002,01,01);
        LocalDate endDate=LocalDate.of(2002,01,01);

        String requestStartDate= CustomDateUtil.formatLocalDateToString(startDate, DateDisplayConstants.DATE_INPUT_FORMAT);
        String requestEndDate=CustomDateUtil.formatLocalDateToString(endDate, DateDisplayConstants.DATE_INPUT_FORMAT);
        return Stream.of(
                //custom period return request start date and end date
                arguments(req(requestStartDate,requestEndDate),new LocalDate[]{startDate,endDate})
        );
    }

}
