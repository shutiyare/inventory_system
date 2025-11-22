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
 * Menu entity representing navigation menu items in the system.
 * Menus can have a parent-child relationship to create hierarchical menu structures.
 * Menus are assigned to roles, determining which navigation items users can see.
 * Extends BaseEntity for common auditing fields.
 */
@Entity
@Table(name = "menus")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"parent", "children", "roles"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Menu extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String path;

    @Column(length = 100)
    private String icon;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    /**
     * Self-referencing relationship for parent-child menu structure.
     * A menu can have a parent menu, creating a tree structure.
     * Using LAZY fetch to avoid loading entire menu tree when fetching a menu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    /**
     * One-to-Many relationship for child menus.
     * Using LAZY fetch to avoid loading all children when fetching a menu.
     * This creates the hierarchical menu structure.
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Menu> children = new HashSet<>();

    /**
     * Many-to-Many relationship with Role entity (inverse side).
     * Using LAZY fetch to avoid loading all roles when menu is fetched.
     * This is the inverse side of the role_menus join table.
     */
    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
}

