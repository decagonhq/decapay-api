package com.decagon.decapay.model.budget;


import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_EXPENSES;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditListener.class)
@Entity
@Table(name = TABLE_EXPENSES)
public class Expense implements Auditable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal amount;

    private LocalDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "budget_id", referencedColumnName = "budget_id"),
            @JoinColumn(name = "budget_category_id", referencedColumnName = "budget_category_id")
    })
    private BudgetLineItem budgetLineItem;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}
