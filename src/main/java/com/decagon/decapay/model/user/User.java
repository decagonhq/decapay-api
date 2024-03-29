package com.decagon.decapay.model.user;


import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_USER;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = TABLE_USER)
@EntityListeners(AuditListener.class)
public class User implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = SchemaConstants.FIRST_NAME_MAX_SIZE)
    private String firstName;

    @Column(length = SchemaConstants.LAST_NAME_MAX_SIZE)
    private String lastName;

    @Email
    @Column(unique = true, length = SchemaConstants.EMAIL_MAX_SIZE)
    private String email;

    @Column(length = 64)
    private String password;

    @Column(length = SchemaConstants.PHONE_NUMBER_MAX_SIZE)
    private String phoneNumber;

    @Column(length = 1000)
    private String userSetting;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BudgetCategory> budgetCategories = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Budget> budgets = new HashSet<>();

    @Embedded
    private AuditSection auditSection = new AuditSection();

    @Override
    public String toString() {
        return "User{id=%d, firstName='%s', lastName='%s', email='%s', password='%s', phoneNumber='%s', userStatus=%s, lastLogin=%s, auditSection=%s}"
                .formatted(id, firstName, lastName, email, password, phoneNumber, userStatus, lastLogin, auditSection);
    }

    public void addBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategory.setUser(this);
        this.budgetCategories.add(budgetCategory);
    }

    public void removeBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategory.setUser(null);
        this.budgetCategories.remove(budgetCategory);
    }
    public void addBudget(Budget budget) {
        budget.setUser(this);
        this.budgets.add(budget);
    }
    public void removeBudget(Budget budget) {
        budget.setUser(null);
        this.budgets.remove(budget);
    }

}
