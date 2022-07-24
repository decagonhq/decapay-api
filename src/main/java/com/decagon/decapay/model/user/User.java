package com.decagon.decapay.model.user;


import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.AppConstants.PASSWORD_SETTING_VLDTY_TRM;
import static com.decagon.decapay.constants.SchemaConstants.TABLE_USER;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_USER)
public class User implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Email
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 64)
    private String password;

    @Column(length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;
    private LocalDateTime lastLogin;

    @Column(length = 100)
    private String passwordResetToken;

    private LocalDateTime passwordResetVldtyTerm;

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

    public boolean tokenExpired() {
        return this.passwordResetVldtyTerm != null && this.passwordResetVldtyTerm.isBefore(LocalDateTime.now());
    }

    public void calculateTokenExpiryDate(String valdtyTrm){
        if (!"0" .equals(valdtyTrm)) { //0 means no validity term used
            if (!CommonUtil.isInteger(valdtyTrm)) {
                this.setDefaultPasswordValidityTerm();
            } else {
                if (Integer.parseInt(valdtyTrm) < 0) {
                    this.setDefaultPasswordValidityTerm();
                } else {
                    setPasswordResetVldtyTerm(LocalDateTime.now().plusHours(Integer.parseInt(valdtyTrm)));
                }
            }
        }
    }

    private void setDefaultPasswordValidityTerm() {
        setPasswordResetVldtyTerm(LocalDateTime.now().plusHours(PASSWORD_SETTING_VLDTY_TRM));
    }

}
