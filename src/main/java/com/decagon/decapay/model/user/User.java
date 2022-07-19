package com.decagon.decapay.model.user;


import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

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

    @Email(message = "please enter a valid email value")
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 64)
    private String password;

    @Column(length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user")
    private Collection<BudgetCategory> budgetCategory;

    @OneToMany(mappedBy = "user")
    private Collection<Budget> budget;

    @Embedded
    private AuditSection auditSection = new AuditSection();

    @Override
    public String toString() {
        return "User{id=%d, firstName='%s', lastName='%s', email='%s', password='%s', phoneNumber='%s', userStatus=%s, lastLogin=%s, auditSection=%s}"
                .formatted(id, firstName, lastName, email, password, phoneNumber, userStatus, lastLogin, auditSection);
    }

}
