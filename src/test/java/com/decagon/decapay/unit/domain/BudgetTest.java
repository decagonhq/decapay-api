package com.decagon.decapay.unit.domain;


import com.decagon.decapay.model.budget.Budget;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class BudgetTest {
    @InjectMocks
    Budget budget;

    @ParameterizedTest
    @MethodSource("budgetProvider")
    void testShouldCalculatePercentageAmountSuccessfully(Budget budget, BigDecimal expectedCalculatedAmount) throws Exception {
        BigDecimal calculatedPercentage = budget.calculatePercentageAmountSpent();
        assertEquals(expectedCalculatedAmount, calculatedPercentage);
    }

   static   Stream<Arguments> budgetProvider() {

        return Stream.of(
                arguments(budget(500.00, 500.00), BigDecimal.valueOf(100.0)),
                arguments(budget(250.00, 500.00), BigDecimal.valueOf(50.0)),
                arguments(budget(0.00, 500.00), BigDecimal.valueOf(0.0)),
                arguments(budget(null, 500.00), BigDecimal.valueOf(0.0)));
    }

    private static Budget budget(Double amountSpentSoFar, Double projectedAmount){
        Budget budget = new Budget();
        if (projectedAmount!= null){
            budget.setProjectedAmount(BigDecimal.valueOf(projectedAmount));
        }
        if (amountSpentSoFar != null){
            budget.setTotalAmountSpentSoFar(BigDecimal.valueOf(amountSpentSoFar));
        }
        return budget;
    }

}
