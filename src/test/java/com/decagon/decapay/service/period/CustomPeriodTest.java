package com.decagon.decapay.service.period;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.service.budget.periodHandler.CustomPeriodHandler;
import com.decagon.decapay.utils.CustomDateUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class CustomPeriodTest {

    @InjectMocks
    CustomPeriodHandler customPeriod;

    static CreateBudgetRequestDTO req(String startDate, String endDate){

        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setPeriod(BudgetPeriod.CUSTOM.name());
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
            customPeriod.validateRequest(dto);
        });
    }
    /*
    for custom period, start and end date are both required
         both must not be equal
         start date must before end date
 */
    static Stream<Arguments> invalidRequestDtoProvider() {
        String pattern = DateDisplayConstants.DATE_INPUT_FORMAT;
        return Stream.of(
                //invalid req same date
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern), CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern))),
                //invalid end date before startdate
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 3), pattern), CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern))),
                //start date cannot be null
                arguments(req(null, pattern), CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern)),
                //end date cannot be null
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern), null)),
                //both start and end date cannot be null
                arguments(req(null, null)),
                //invalid start date format
                arguments(req("1200-08-09", CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern))),
                //invalid end date format
                arguments(req(CustomDateUtil.formatLocalDateToString(LocalDate.of(2022, 1, 1), pattern),"1200-08-09"))
        );
    }


    /*
     test calculated date range for custom date budget
     */
    @ParameterizedTest
    @MethodSource("requestDtoProvider")
    void testShouldGetPeriodDateRangeSuccessfully(CreateBudgetRequestDTO dto, LocalDate[] expectedRange) throws Exception {
        LocalDate[] dateRange = customPeriod.calculateBudgetDateRange(dto);
        assertEquals(expectedRange[0], dateRange[0]);
        assertEquals(expectedRange[1], dateRange[1]);
    }
    
    static Stream<Arguments> requestDtoProvider() {
        LocalDate startDate=LocalDate.of(2002,01,01);
        LocalDate endDate=LocalDate.of(2002,01,04);

        String requestStartDate=CustomDateUtil.formatLocalDateToString(startDate, DateDisplayConstants.DATE_INPUT_FORMAT);
        String requestEndDate=CustomDateUtil.formatLocalDateToString(endDate, DateDisplayConstants.DATE_INPUT_FORMAT);
        return Stream.of(
                //custom period return request start date and end date
                arguments(req(requestStartDate,requestEndDate),new LocalDate[]{startDate,endDate})
        );
    }


}
