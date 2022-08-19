package com.decagon.decapay.model.budget;

import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET_CATEGORY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_BUDGET_CATEGORY)
public class BudgetCategory implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Embedded
    private AuditSection auditSection = new AuditSection();

    @OneToMany(mappedBy = "budgetCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Set<BudgetLineItem> budgetLineItems = new HashSet<>();

    public void removeBudgetLineItem(BudgetLineItem budgetLineItem){
        //TODO: Causing Lazily Initialization Exception Without Transactional
        this.budgetLineItems.remove(budgetLineItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BudgetCategory category = (BudgetCategory) o;
        return id != null && Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
