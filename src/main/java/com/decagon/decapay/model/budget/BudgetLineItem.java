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
import java.util.Collection;

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

    private String title;

    private BigDecimal projectedAmount;

    private BigDecimal totalAmountSpentSoFar;

    @Column(length = 100, columnDefinition = "jsonb")
    private String notificationThreshold;

    @ManyToOne
    @JoinColumn(name = "budget_category_id")
    private BudgetCategory budgetCategory;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(mappedBy = "budgetLineItem")
    private Collection<Expenses> expenses;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}
