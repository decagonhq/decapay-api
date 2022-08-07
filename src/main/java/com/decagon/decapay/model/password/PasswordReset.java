package com.decagon.decapay.model.password;

import com.decagon.decapay.enumTypes.ResetCodeStatus;
import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.utils.CommonUtil;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.decagon.decapay.constants.AppConstants.PASSWORD_RESET_TOKEN_VALIDITY_PERIOD;
import static com.decagon.decapay.constants.SchemaConstants.TABLE_PASSWORD_RESET;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditListener.class)
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

    private String email;

    @Column(name = "device_id",length = 30)
    private String deviceId;

    private String token;

    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private ResetCodeStatus status;

    @Embedded
    private AuditSection auditSection = new AuditSection();

    public boolean tokenExpired() {
        return this.expiredAt != null && this.expiredAt.isBefore(LocalDateTime.now());
    }

    public void calculateTokenExpiryDate(String validityPeriod){
        if (!"0" .equals(validityPeriod)) { //0 means no validity term used
            if (!CommonUtil.isInteger(validityPeriod)) {
                this.setDefaultExpirationDate();
            } else {
                if (Integer.parseInt(validityPeriod) < 0) {
                    this.setDefaultExpirationDate();
                } else {
                    setExpiredAt(LocalDateTime.now().plusHours(Integer.parseInt(validityPeriod)));
                }
            }
        }
    }

    private void setDefaultExpirationDate() {
        setExpiredAt(LocalDateTime.now().plusHours(PASSWORD_RESET_TOKEN_VALIDITY_PERIOD));
    }
}
