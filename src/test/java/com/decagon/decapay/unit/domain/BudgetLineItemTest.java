package com.decagon.decapay.unit.domain;


import com.decagon.decapay.model.budget.BudgetLineItem;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class BudgetLineItemTest {

    @ParameterizedTest
    @MethodSource("budgetLineItemProvider")
    void testShouldCalculatePercentageAmountSuccessfully(BudgetLineItem budgetLineItem, BigDecimal expectedCalculatedAmount) throws Exception {
        BigDecimal calculatedPercentage = budgetLineItem.calculatePercentageAmountSpent();
        assertEquals(expectedCalculatedAmount, calculatedPercentage);
    }

    static Stream<Arguments> budgetLineItemProvider() {

        return Stream.of(
                arguments(budgetLineItem(500.00, 500.00), BigDecimal.valueOf(100.0)),
                arguments(budgetLineItem(250.00, 500.00), BigDecimal.valueOf(50.0)),
                arguments(budgetLineItem(0.00, 500.00), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(null, 500.00), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(0.00, 0.00), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(0.00, 0.0), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(500.00, 0.00), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(500.00, null), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(null, null), BigDecimal.valueOf(0.0)));
    }

    private static BudgetLineItem budgetLineItem(Double amountSpentSoFar, Double projectedAmount) {
        BudgetLineItem budgetLineItem = new BudgetLineItem();
        if (projectedAmount != null) {
            budgetLineItem.setProjectedAmount(BigDecimal.valueOf(projectedAmount));
        }
        if (amountSpentSoFar != null) {
            budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(amountSpentSoFar));
        }
        return budgetLineItem;
    }

}
