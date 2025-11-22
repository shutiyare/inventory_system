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
 * Role entity representing user roles in the system.
 * Roles are assigned to users and contain permissions and menu access.
 * This is a core component of the RBAC (Role-Based Access Control) system.
 * Extends BaseEntity for common auditing fields.
 */
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"permissions", "menus", "users"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Many-to-Many relationship with Permission entity.
     * Using LAZY fetch to avoid loading all permissions when role is fetched.
     * Permissions define what actions a role can perform.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Many-to-Many relationship with Menu entity.
     * Using LAZY fetch to avoid loading all menus when role is fetched.
     * Menus define what navigation items a role can access.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_menus",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id")
    )
    private Set<Menu> menus = new HashSet<>();

    /**
     * Many-to-Many relationship with User entity (inverse side).
     * Using LAZY fetch as this is the inverse side of the relationship.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
}

