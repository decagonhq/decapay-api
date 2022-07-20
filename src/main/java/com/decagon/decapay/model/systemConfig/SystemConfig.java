package com.decagon.decapay.model.systemConfig;


import javax.persistence.*;

import com.decagon.decapay.enumTypes.SystemConfigType;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import lombok.*;

import java.io.Serializable;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_SYSTEM_CONFIG;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = TABLE_SYSTEM_CONFIG)
public class SystemConfig implements Auditable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String configName;

    private String value;

    @Column(length=50,unique = true)
    private String configKey;

    private String description;

    @Column(length=30)
    private String configGroup;

    private int sortOrder;

    @Enumerated(EnumType.STRING)
    private SystemConfigType systemConfigType=SystemConfigType.TEXT;

    @Embedded
    private AuditSection auditSection = new AuditSection();



}
