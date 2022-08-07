package com.decagon.decapay.service.period;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.service.budget.period.AnnualPeriodHandler;
import com.decagon.decapay.utils.CustomDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class AnnualPeriodTest {

    @InjectMocks
    AnnualPeriodHandler annualPeriod;

    static  CreateBudgetRequestDTO req(short year){

        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setDescription("des");
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        budgetRequest.setBudgetStartDate(null);
        budgetRequest.setBudgetEndDate(null);
        budgetRequest.setYear(year);
        budgetRequest.setMonth((short)0);
        budgetRequest.setDuration(0);
        return budgetRequest;
    }
    @ParameterizedTest
    @MethodSource("invalidRequestDtoProvider")
    void testThrow400WhenCreateBudgetWithInvalidDateRequest(CreateBudgetRequestDTO dto) throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            annualPeriod.validateRequest(dto);
        });
    }
    /*
      for annual period, year required must be a number and 4xters in len and
     */
    static Stream<Arguments> invalidRequestDtoProvider() {
        return Stream.of(
                //invalid year
                arguments(req((short)999)),
                arguments(req((short)10000))
        );
    }


    /*
    test calculated date range for annual budget
     */
    @Test
    void testThrow400WhenYearInvalid() throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            annualPeriod.calculateBudgetDateRange(req((short)999));
        });
        assertThrows(InvalidRequestException.class, () -> {
            annualPeriod.calculateBudgetDateRange(req((short)10000));
        });
    }
    @ParameterizedTest
    @MethodSource("requestDtoProvider")
    void testShouldGetPeriodDateRangeSuccessfully(CreateBudgetRequestDTO dto, LocalDate[] expectedRange) throws Exception {
        LocalDate[] dateRange = annualPeriod.calculateBudgetDateRange(dto);
        assertEquals(expectedRange[0], dateRange[0]);
        assertEquals(expectedRange[1], dateRange[1]);
    }
    static Stream<Arguments> requestDtoProvider() {
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));
        short currYr=(short) ym.getYear();
        int notActiveYr1=currYr+1;
        int notActiveYr2=currYr-1;
        return Stream.of(
                //if year current, then startdate is current date and enddate last date of year
                arguments(req(currYr),new LocalDate[]{CustomDateUtil.today(),CustomDateUtil.lastDateOfYear(currYr)}),
                //if year not current, then startdate is first date of year and enddate last date of year
                arguments(req((short)notActiveYr1),new LocalDate[]{CustomDateUtil.firstDateOfYear((short)notActiveYr1),CustomDateUtil.lastDateOfYear((short)notActiveYr1)}),
                arguments(req((short)notActiveYr2),new LocalDate[]{CustomDateUtil.firstDateOfYear((short)notActiveYr2),CustomDateUtil.lastDateOfYear((short)notActiveYr2)})
        );
    }




}
