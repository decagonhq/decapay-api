package com.decagon.decapay.model.budget;

import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.model.audit.AuditListener;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_BUDGET)
public class Budget implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = SchemaConstants.BUDGET_TITLE_SIZE)
    private String title;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal totalAmountSpentSoFar;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal projectedAmount;

    private LocalDateTime budgetStartDate;

    private LocalDateTime budgetEndDate;

    @Column(length = 100)
    private String notificationThreshold;

    @Column(length = SchemaConstants.BUDGET_DESC_SIZE)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_budget_id")
    private Budget parentBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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


    public BigDecimal calculatePercentageAmountSpent(){
        if (this.getTotalAmountSpentSoFar() == null){
            return BigDecimal.ZERO.setScale(1, RoundingMode.CEILING);
        }
        BigDecimal spentSoFar = this.getTotalAmountSpentSoFar().divide(this.getProjectedAmount(), BigDecimal.ROUND_HALF_DOWN);
        return spentSoFar.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.CEILING);
    }
}
