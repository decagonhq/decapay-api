package com.decagon.decapay.model.budget;


import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET_LINE_ITEM;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditListener.class)
@Entity
@Table(name = TABLE_BUDGET_LINE_ITEM)
//@EqualsAndHashCode
public class BudgetLineItem implements Auditable, Serializable {

    @EmbeddedId
    private BudgetLineItemId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("budgetId")
    Budget budget;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("budgetCategoryId")
    private BudgetCategory budgetCategory;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal projectedAmount;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal totalAmountSpentSoFar;

    @Column(length = 100)
    private String notificationThreshold;

    @OneToMany(mappedBy = "budgetLineItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expenses> expenses = new HashSet<>();

    @Embedded
    private AuditSection auditSection = new AuditSection();

    public BudgetLineItem(Budget budget, BudgetCategory budgetCategory, BigDecimal projectedAmount){
        this.budget = budget;
        this.budgetCategory = budgetCategory;
        this.projectedAmount = projectedAmount;
    }


    public void addExpense(Expenses expense) {
        expense.setBudgetLineItem(this);
        this.expenses.add(expense);
    }

    public void removeExpense(Expenses expense) {
        expense.setId(null);
        this.expenses.remove(expense);
    }

    public BigDecimal calculatePercentageAmountSpent() {
        if (this.getTotalAmountSpentSoFar() == null){
            return BigDecimal.ZERO.setScale(1, RoundingMode.CEILING);
        }
        BigDecimal spentSoFar = this.getTotalAmountSpentSoFar().divide(this.getProjectedAmount(), BigDecimal.ROUND_HALF_DOWN);
        return spentSoFar.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.CEILING);
    }

    public BigDecimal calculateExpensesTotalAmount(){
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Expenses expense : this.getExpenses()){
            totalAmount = totalAmount.add(expense.getAmount());
        }
        return totalAmount;
    }

    public void addExpense(BigDecimal expenseAmount) {
        if(this.getExpenses().isEmpty()) {
            this.setTotalAmountSpentSoFar(expenseAmount);
        }
        BigDecimal totalExpensesAmount = this.calculateExpensesTotalAmount();
        totalExpensesAmount = totalExpensesAmount.add(expenseAmount);

        this.setTotalAmountSpentSoFar(totalExpensesAmount);
    }
}
