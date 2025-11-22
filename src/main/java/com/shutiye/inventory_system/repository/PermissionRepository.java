package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Permission entity.
 * Provides data access methods for permission operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    
    /**
     * Find a permission by code.
     * Permission codes are unique identifiers like "USER_VIEW", "PRODUCT_CREATE", etc.
     * Used for permission lookup and validation.
     */
    Optional<Permission> findByCode(String code);
    
    /**
     * Find a permission by name.
     * Used for permission lookup.
     */
    Optional<Permission> findByName(String name);
    
    /**
     * Check if a permission code already exists.
     * Used for validation during permission creation.
     */
    boolean existsByCode(String code);
    
    /**
     * Check if a permission name already exists.
     * Used for validation during permission creation.
     */
    boolean existsByName(String name);
    
    /**
     * Find all permissions by role ID.
     * Useful for getting all permissions assigned to a specific role.
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    java.util.List<Permission> findAllByRoleId(@Param("roleId") Long roleId);
}

