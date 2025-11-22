package com.shutiye.inventory_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission entity representing system permissions.
 * Permissions define specific actions that can be performed in the system.
 * Examples: USER_VIEW, USER_CREATE, PRODUCT_VIEW, etc.
 * Permissions are assigned to roles, and users inherit permissions through their roles.
 * Extends BaseEntity for common auditing fields.
 */
@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(length = 500)
    private String description;

    /**
     * Many-to-Many relationship with Role entity (inverse side).
     * Using LAZY fetch to avoid loading all roles when permission is fetched.
     * This is the inverse side of the role_permissions join table.
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
}

