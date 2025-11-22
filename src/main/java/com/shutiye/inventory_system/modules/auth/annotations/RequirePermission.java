package com.shutiye.inventory_system.modules.auth.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for method-level permission checking.
 * Similar to FastAPI's @permission_checker decorator.
 * 
 * Usage:
 * @RequirePermission("USER_CREATE")
 * public UserDTO createUser(UserCreateRequest request) { ... }
 * 
 * This annotation works with Spring Security's @PreAuthorize.
 * The permission check is performed automatically by Spring Security.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(#permission)")
public @interface RequirePermission {
    
    /**
     * Permission code required to access the method.
     * 
     * @return Permission code (e.g., "USER_CREATE", "PRODUCT_VIEW")
     */
    String permission();
}

