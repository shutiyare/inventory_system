package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 * Provides data access methods for role operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    
    /**
     * Find a role by name.
     * Used for role lookup and validation.
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if a role name already exists.
     * Used for validation during role creation.
     */
    boolean existsByName(String name);
    
    /**
     * Find all roles by permission ID.
     * Useful for finding all roles that have a specific permission.
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    java.util.List<Role> findAllByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * Find all roles by menu ID.
     * Useful for finding all roles that have access to a specific menu.
     */
    @Query("SELECT r FROM Role r JOIN r.menus m WHERE m.id = :menuId")
    java.util.List<Role> findAllByMenuId(@Param("menuId") Long menuId);
}

