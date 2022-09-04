package com.decagon.decapay.model.reference.language;

import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_LANGUAGE;

@Getter
@Setter
@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = TABLE_LANGUAGE, indexes = { @Index(name="CODE_IDX2", columnList = "CODE")})
@Cacheable
public class Language  implements Auditable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private AuditSection auditSection = new AuditSection();

  @Column(name = "CODE", nullable = false)
  private String code;

  @Column(name = "TITLE", nullable = false)
  private String title;

  @JsonIgnore
  @Column(name = "SORT_ORDER")
  private Integer sortOrder;

  public Language() {}

  public Language(String code) {
    this.setCode(code);
  }

  public Language(String code, String title) {
    this(code);
    this.setTitle(title);
  }
}
