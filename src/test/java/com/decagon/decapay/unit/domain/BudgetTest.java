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

   static   Stream<Arguments> budgetProvider() {

       return Stream.of(
               arguments(budget(500.00, 500.00), BigDecimal.valueOf(100.0)),
               arguments(budget(250.00, 500.00), BigDecimal.valueOf(50.0)),
               arguments(budget(0.00, 500.00), BigDecimal.valueOf(0.0)),
               arguments(budget(null, 500.00), BigDecimal.valueOf(0.0)),
               arguments(budget(0.00, 0.00), BigDecimal.valueOf(0.0)),
               arguments(budget(0.00, 0.0), BigDecimal.valueOf(0.0)),
               arguments(budget(500.00, 0.00), BigDecimal.valueOf(0.0)),
               arguments(budget(500.00, null), BigDecimal.valueOf(0.0)),
               arguments(budget(null, null), BigDecimal.valueOf(0.0)));
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

    @ParameterizedTest
    @MethodSource("budgetLineItemProvider")
    void testShouldReturnNullIfBudgetLineItemIsNotPresent(Budget budget, BudgetCategory budgetCategory, BudgetLineItem expectedBudgetLineItem) throws Exception {
        assertEquals(expectedBudgetLineItem, budget.getBudgetLineItem(budgetCategory));
    }

    static  Stream<Arguments> budgetLineItemProvider() {
        Budget budgetWithNoLineItem = TestModels.budget( MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1));
        budgetWithNoLineItem.setId(2L);

        BudgetCategory category = TestModels.budgetCategory("Food");
        category.setId(1L);

        return Stream.of(
                arguments(budgetWithNoLineItem, category, null)
        );
    }
}
