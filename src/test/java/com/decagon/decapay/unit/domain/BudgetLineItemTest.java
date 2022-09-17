package com.decagon.decapay.unit.domain;


import com.decagon.decapay.model.budget.BudgetLineItem;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                arguments(budgetLineItem(BigDecimal.valueOf(500.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(100.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(250.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(50.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(0.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(null, BigDecimal.valueOf(500.00)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN), BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(0.0).setScale(1, RoundingMode.HALF_DOWN), BigDecimal.valueOf(0.0).setScale(1, RoundingMode.HALF_DOWN)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(500.00), BigDecimal.valueOf(0.00)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(500.00), BigDecimal.valueOf(0.0)), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(BigDecimal.valueOf(500.00), null), BigDecimal.valueOf(0.0)),
                arguments(budgetLineItem(null, null), BigDecimal.valueOf(0.0)));
    }

    private static BudgetLineItem budgetLineItem(BigDecimal amountSpentSoFar, BigDecimal projectedAmount) {
        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setProjectedAmount(projectedAmount);
        budgetLineItem.setTotalAmountSpentSoFar(amountSpentSoFar);
        return budgetLineItem;
    }

}
