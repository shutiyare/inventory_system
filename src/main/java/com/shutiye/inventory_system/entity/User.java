package com.shutiye.inventory_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing system users with role-based access control.
 * Users can have multiple roles, and roles determine their permissions and menu access.
 * Extends BaseEntity for common auditing fields.
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Many-to-Many relationship with Role entity.
     * Using EAGER fetch type as specified in requirements for user.roles.
     * This allows roles to be loaded immediately when user is fetched.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();
}

