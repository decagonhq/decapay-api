package com.decagon.decapay.model.audit;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class AuditSection implements Serializable {
    @CreatedDate
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(name = "date_modified")
    private LocalDateTime dateModified;

    @Column(name="deleted", columnDefinition = "varchar(1) default (0)")
    private String delF="0";

    @Override
    public String toString() {
        return "AuditSection{dateCreated=%s, dateModified=%s, delF='%s'}"
                .formatted(dateCreated, dateModified, delF);
    }
}
