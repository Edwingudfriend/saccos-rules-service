package com.reimagineafrica.rules.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sacco_rules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "rule_code"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SaccoRule {
    @Id
    @Column(name = "id", length = 36)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Column(name = "tenant_id", nullable = false, length = 50)
    @Builder.Default
    private String tenantId = "default";

    @Column(name = "rule_code", nullable = false, length = 100)
    private String ruleCode;

    @Column(name = "rule_value", nullable = false, length = 255)
    private String ruleValue;

    @Column(name = "data_type", length = 20)
    private String dataType;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
