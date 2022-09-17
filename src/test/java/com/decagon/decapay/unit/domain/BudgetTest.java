package com.decagon.decapay.unit.domain;


import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.utils.TestModels;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Stream;

import static com.decagon.decapay.model.budget.BudgetPeriod.MONTHLY;
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

    static Stream<Arguments> budgetProvider() {

        return Stream.of(
                arguments(budget(BigDecimal.valueOf(500.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(100.0)),
                arguments(budget(BigDecimal.valueOf(250.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(50.0)),
                arguments(budget(BigDecimal.valueOf(0.00), BigDecimal.valueOf(500.00)), BigDecimal.valueOf(0.0)),
                arguments(budget(null, BigDecimal.valueOf(500.00)), BigDecimal.valueOf(0.0)),
                arguments(budget(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN), BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN)), BigDecimal.valueOf(0.0)),
                arguments(budget(BigDecimal.valueOf(0.0).setScale(1, RoundingMode.HALF_DOWN), BigDecimal.valueOf(0.0).setScale(1, RoundingMode.HALF_DOWN)), BigDecimal.valueOf(0.0)),
                arguments(budget(BigDecimal.valueOf(500.00), BigDecimal.valueOf(0.00)), BigDecimal.valueOf(0.0)),
                arguments(budget(BigDecimal.valueOf(500.00), BigDecimal.valueOf(0.0)), BigDecimal.valueOf(0.0)),
                arguments(budget(BigDecimal.valueOf(500.00), null), BigDecimal.valueOf(0.0)),
                arguments(budget(null, null), BigDecimal.valueOf(0.0)));
    }

    private static Budget budget(BigDecimal amountSpentSoFar, BigDecimal projectedAmount) {
        Budget budget = new Budget();
        budget.setProjectedAmount(projectedAmount);
        budget.setTotalAmountSpentSoFar(amountSpentSoFar);
        return budget;
    }

    @ParameterizedTest
    @MethodSource("budgetLineItemProvider")
    void testShouldReturnNullIfBudgetLineItemIsNotPresent(Budget budget, BudgetCategory budgetCategory, BudgetLineItem expectedBudgetLineItem) throws Exception {
        assertEquals(expectedBudgetLineItem, budget.getBudgetLineItem(budgetCategory));
    }

    static Stream<Arguments> budgetLineItemProvider() {
        Budget budgetWithNoLineItem = TestModels.budget(MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1));
        budgetWithNoLineItem.setId(2L);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setId(1L);

        return Stream.of(
                arguments(budgetWithNoLineItem, category, null)
        );
    }
}
