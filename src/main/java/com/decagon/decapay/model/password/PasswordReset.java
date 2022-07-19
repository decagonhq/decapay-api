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
@Table(name = TABLE_PASSWORD_RESET)
public class PasswordReset implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "please enter a valid email value")
    private String email;

    private String deviceId;

    private String token;

    private LocalDateTime expiredAt;

    @Embedded
    private AuditSection auditSection = new AuditSection();
}
