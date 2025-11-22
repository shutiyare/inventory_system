package com.shutiye.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity class with common auditing fields.
 * All entities should extend this class to inherit:
 * - id (auto-generated)
 * - createdDate (automatically set on creation)
 * - modifiedDate (automatically updated on modification)
 * - createdById (ID of user who created the record)
 * - modifiedById (ID of user who last modified the record)
 * - owner (username of creator)
 * - modifier (username of last modifier)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(name = "created_by_id", nullable = true)
    private Long createdById;

    @Column(name = "modified_by_id", nullable = true)
    private Long modifiedById;

    @Column(name = "owner", length = 100, nullable = true)
    private String owner;

    @Column(name = "modifier", length = 100, nullable = true)
    private String modifier;

    /**
     * Set auditing fields before persisting.
     * This is called automatically by JPA lifecycle callbacks.
     */
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (modifiedDate == null) {
            modifiedDate = LocalDateTime.now();
        }
    }

    /**
     * Update the modifiedDate field before updating.
     * This is called automatically by JPA lifecycle callbacks.
     */
    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}

