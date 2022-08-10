package com.decagon.decapay.service.period;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.service.budget.periodHandler.MonthPeriodHandler;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.utils.TestUtils;
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
public class MonthPeriodTest {

    @InjectMocks
    MonthPeriodHandler monthPeriod;

    static CreateBudgetRequestDTO req(short month, short year){
        CreateBudgetRequestDTO budgetRequest = new CreateBudgetRequestDTO();
        budgetRequest.setTitle("Title");
        budgetRequest.setAmount(BigDecimal.TEN);
        budgetRequest.setDescription("des");
        budgetRequest.setPeriod(BudgetPeriod.ANNUAL.name());
        budgetRequest.setBudgetStartDate(null);
        budgetRequest.setBudgetEndDate(null);
        budgetRequest.setYear(year);
        budgetRequest.setMonth(month);
        budgetRequest.setDuration(0);
        return budgetRequest;
    }
    /*
     test invalid request
     */
    @ParameterizedTest
    @MethodSource("invalidRequestDtoProvider")
    void testThrow400WhenCreateBudgetWithInvalidDateRequest(CreateBudgetRequestDTO dto) throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            monthPeriod.validateRequest(dto);
        });

    }
    /*
      for monthly period, year and month are both required
                         year must be a number and 4xters in len and
                         month must be number and btw 1 and 12
     */
    static Stream<Arguments> invalidRequestDtoProvider() {
        return Stream.of(
                //invalid month field
                arguments(req((short) 0,(short)2022)),
                arguments(req((short) 13,(short)2022)),
                //invalid year
                arguments(req((short) 1,(short)999)),
                arguments(req((short) 2,(short)10000))
        );
    }

    /*
    test calculated date range for monthly budget
     */
    @Test
    void testThrow400WhenGetDateRandeAndMonthInvalid() throws Exception {
        assertThrows(InvalidRequestException.class, () -> {
            monthPeriod.calculateBudgetDateRange(req((short) 0,(short)2022));
        });
        assertThrows(InvalidRequestException.class, () -> {
            monthPeriod.calculateBudgetDateRange(req((short) 13,(short)2022));
        });

    }
    @ParameterizedTest
    @MethodSource("requestDtoProvider")
    void testShouldGetPeriodDateRangeSuccessfully(CreateBudgetRequestDTO dto, LocalDate[] expectedRange) throws Exception {
        LocalDate[] dateRange = monthPeriod.calculateBudgetDateRange(dto);
        assertEquals(expectedRange[0], dateRange[0]);
        assertEquals(expectedRange[1], dateRange[1]);
    }
    static Stream<Arguments> requestDtoProvider() {
        YearMonth ym = YearMonth.from(Instant.now().atZone(ZoneId.of("UTC")));

        short currMnth=(short) ym.getMonthValue();
        short currYr=(short) ym.getYear();

        short notCurrMnth1= TestUtils.getNonCurrentMonth(currMnth+1);
        short notCurrMnth2=TestUtils.getNonCurrentMonth(currMnth-1);

        int notActiveYr1=currYr+1;
        int notActiveYr2=currYr-1;

        return Stream.of(
                //if month and year are current, then startdate is current date and enddate last date of month
                arguments(req(currMnth,currYr),new LocalDate[]{CustomDateUtil.today(),CustomDateUtil.lastDateOfMonth(currYr,currMnth)}),

                //if month current and year not current , then start date is first date and enddate lastDateOfMonth
                arguments(req(currMnth,(short)notActiveYr1),new LocalDate[]{CustomDateUtil.firstDateOfMonth((short)notActiveYr1,currMnth),CustomDateUtil.lastDateOfMonth((short)notActiveYr1,currMnth)}),
                arguments(req(currMnth,(short)notActiveYr2),new LocalDate[]{CustomDateUtil.firstDateOfMonth((short)notActiveYr2,currMnth),CustomDateUtil.lastDateOfMonth((short)notActiveYr2,currMnth)}),

                //if month not current and year current , start date is first date
                arguments(req(notCurrMnth1,currYr),new LocalDate[]{CustomDateUtil.firstDateOfMonth(currYr,notCurrMnth1),CustomDateUtil.lastDateOfMonth(currYr,notCurrMnth1)}),
                arguments(req(notCurrMnth2,currYr),new LocalDate[]{CustomDateUtil.firstDateOfMonth(currYr,notCurrMnth2),CustomDateUtil.lastDateOfMonth(currYr,notCurrMnth2)}),

                //if month and year not current, start date is first date
                arguments(req(notCurrMnth1,(short)notActiveYr1),new LocalDate[]{CustomDateUtil.firstDateOfMonth((short)notActiveYr1,notCurrMnth1),CustomDateUtil.lastDateOfMonth((short)notActiveYr1,notCurrMnth1)}),
                arguments(req(notCurrMnth2,(short)notActiveYr2),new LocalDate[]{CustomDateUtil.firstDateOfMonth((short)notActiveYr2,notCurrMnth2),CustomDateUtil.lastDateOfMonth((short)notActiveYr2,notCurrMnth2)})
        );
    }

}
