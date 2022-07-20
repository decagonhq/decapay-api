package com.decagon.decapay.model.password;

import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_PASSWORD_RESET;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_PASSWORD_RESET, uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email","device_id"
        })
})
public class PasswordReset implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Column(name = "device_id",length = 30, unique = true)
    private String deviceId;

    private String token;

    private LocalDateTime expiredAt;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}