package com.decagon.decapay.model.audit;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static java.time.LocalDateTime.now;


public class AuditListener {

  @PrePersist
  public void onSave(Object o) {
    if (o instanceof Auditable audit) {
      AuditSection auditSection = audit.getAuditSection();

      auditSection.setDateModified(now());
      if (auditSection.getDateCreated() == null) {
        auditSection.setDateCreated(now());
      }
      audit.setAuditSection(auditSection);
    }
  }

  @PreUpdate
  public void onUpdate(Object o) {
    if (o instanceof Auditable audit) {
      AuditSection auditSection = audit.getAuditSection();

      auditSection.setDateModified(now());
      if (auditSection.getDateCreated() == null) {
        auditSection.setDateCreated(now());
      }
      audit.setAuditSection(auditSection);
    }
  }
}
