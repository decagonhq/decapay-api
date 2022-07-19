package com.decagon.decapay.model.budget;


import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET_LINE_ITEM;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_BUDGET_LINE_ITEM)
public class BudgetLineItem implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal projectedAmount;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal totalAmountSpentSoFar;

    @Column(length = 100)
    private String notificationThreshold;

    @ManyToOne
    @JoinColumn(name = "budget_category_id")
    private BudgetCategory budgetCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(mappedBy = "budgetLineItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expenses> expenses = new HashSet<>();

    @Embedded
    private AuditSection auditSection = new AuditSection();

    public void addExpense(Expenses expense) {
        expense.setBudgetLineItem(this);
        this.expenses.add(expense);
    }

    public void removeExpense(Expenses expense) {
        expense.setBudgetLineItem(null);
        this.expenses.remove(expense);
    }
}
