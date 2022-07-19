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
import java.util.Collection;

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

    private String title;

    private BigDecimal totalAmountSpentSoFar;

    private BigDecimal projectedAmount;

    private LocalDateTime budgetStartDate;

    private LocalDateTime budgetEndDate;

    @Column(length = 100, columnDefinition = "jsonb")
    private String notificationThreshold;

    @ManyToOne
    @JoinColumn(name = "parent_budget_id")
    private Budget parentBudget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BudgetPeriod budgetPeriod;

    @OneToMany(mappedBy = "budget")
    private Collection<BudgetLineItem> budgetLineItem;


    @Embedded
    private AuditSection auditSection = new AuditSection();
}
