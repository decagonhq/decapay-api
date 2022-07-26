package com.decagon.decapay.model.user;


import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_USER;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_USER)
@Builder
public class User implements Auditable, Serializable, UserDetails {
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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BudgetCategory> budgetCategories = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Budget> budgets = new HashSet<>();

    @Embedded
    private AuditSection auditSection = new AuditSection();

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;



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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
