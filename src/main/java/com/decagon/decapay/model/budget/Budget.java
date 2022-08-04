package com.decagon.decapay.model.budget;

import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_BUDGET)
public class Budget implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal totalAmountSpentSoFar;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal projectedAmount;

    private LocalDate budgetStartDate;

    private LocalDate budgetEndDate;

    @Column(length = 100)
    private String notificationThreshold;

    private String description;

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
