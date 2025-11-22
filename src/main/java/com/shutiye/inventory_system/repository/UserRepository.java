package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides data access methods for user operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * Find a user by username.
     * Used for authentication and user lookup.
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email.
     * Used for email-based user lookup.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a username already exists.
     * Used for validation during user creation.
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists.
     * Used for validation during user creation.
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all users by role ID.
     * Useful for finding all users with a specific role.
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    java.util.List<User> findAllByRoleId(@Param("roleId") Long roleId);
}

