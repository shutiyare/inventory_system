package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Menu entity.
 * Provides data access methods for menu operations.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {
    
    /**
     * Find all menus that have no parent (root menus).
     * These are the top-level menu items in the menu tree.
     * Used for building the menu hierarchy.
     */
    List<Menu> findAllByParentIsNull();
    
    /**
     * Find all menus by parent ID.
     * Used for getting child menus of a specific parent.
     */
    List<Menu> findAllByParentId(Long parentId);
    
    /**
     * Find a menu by path.
     * Used for menu lookup by route path.
     */
    Optional<Menu> findByPath(String path);
    
    /**
     * Find all menus by role ID.
     * Useful for getting all menus accessible by a specific role.
     */
    @Query("SELECT m FROM Menu m JOIN m.roles r WHERE r.id = :roleId ORDER BY m.orderIndex")
    List<Menu> findAllByRoleId(@Param("roleId") Long roleId);
    
    /**
     * Find all root menus (menus with no parent) accessible by a role.
     * Used for building the menu tree for a specific role.
     */
    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN m.roles r WHERE m.parent IS NULL AND (r.id = :roleId OR r.id IS NULL) ORDER BY m.orderIndex")
    List<Menu> findRootMenusByRoleId(@Param("roleId") Long roleId);
}

