package com.decagon.decapay.model.audit;

public interface Auditable {
    AuditSection getAuditSection();
    void setAuditSection(AuditSection auditSection);
}
