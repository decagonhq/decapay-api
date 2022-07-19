package com.decagon.decapay.model.budget;

import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET_CATEGORY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_BUDGET_CATEGORY)
public class BudgetCategory implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "budgetCategory")
    private Collection<BudgetLineItem> budgetLineItem;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}
