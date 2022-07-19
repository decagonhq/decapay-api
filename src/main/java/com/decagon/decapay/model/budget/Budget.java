package com.decagon.decapay.model.budget;

import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_BUDGET)
public class Budget implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    private BigDecimal totalAmountSpentSoFar = BigDecimal.ZERO;

    private BigDecimal projectedAmount = BigDecimal.ZERO;

    private LocalDateTime budgetStartDate;

    private LocalDateTime budgetEndDate;

    @Column(length = 100)
    private String notificationThreshold;

    @ManyToOne
    @JoinColumn(name = "parent_budget_id")
    private Budget parentBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BudgetPeriod budgetPeriod;

    @OneToMany(mappedBy = "budget", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BudgetLineItem> budgetLineItems = new HashSet<>();


    @Embedded
    private AuditSection auditSection = new AuditSection();

    public void addBudgetLineItem(BudgetLineItem budgetLineItem) {
        budgetLineItem.setBudget(this);
        this.budgetLineItems.add(budgetLineItem);
    }

    public void removeBudgetLineItem(BudgetLineItem budgetLineItem) {
        budgetLineItem.setBudget(null);
        this.budgetLineItems.remove(budgetLineItem);
    }
}
