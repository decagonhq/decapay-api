package com.decagon.decapay.model.budget;

import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET_CATEGORY;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private User user;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}
