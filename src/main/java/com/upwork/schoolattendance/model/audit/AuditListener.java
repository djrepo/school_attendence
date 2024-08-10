package com.upwork.schoolattendance.model.audit;

import com.upwork.schoolattendance.model.AuditDetails;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;

public class AuditListener {

    @PrePersist
    public void onBeforeInsert(AuditDetails audit) {
        audit.setCreatedAt(OffsetDateTime.now());
        audit.setCreatedBy("SYSTEM");
    }

    @PreUpdate
    public void onBeforeUpdate(AuditDetails audit) {
        audit.setModifiedAt(OffsetDateTime.now());
        audit.setModifiedBy("SYSYEM");
    }
}
