package com.shutiye.inventory_system.modules.auth.utils;

import com.shutiye.inventory_system.entity.BaseEntity;
import org.springframework.stereotype.Component;

/**
 * Helper class for setting auditing fields on entities.
 * Automatically sets createdById, modifiedById, owner, and modifier
 * based on the current authenticated user.
 */
@Component
public class AuditingHelper {
    
    private final SecurityUtils securityUtils;
    
    public AuditingHelper(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }
    
    /**
     * Set auditing fields before creating an entity.
     * Sets createdById, owner, and initializes createdDate/modifiedDate.
     * 
     * @param entity Entity to audit
     */
    public void setCreatedAudit(BaseEntity entity) {
        securityUtils.getCurrentUser().ifPresent(user -> {
            entity.setCreatedById(user.getId());
            entity.setOwner(user.getUsername());
        });
    }
    
    /**
     * Set auditing fields before updating an entity.
     * Sets modifiedById and modifier.
     * 
     * @param entity Entity to audit
     */
    public void setModifiedAudit(BaseEntity entity) {
        securityUtils.getCurrentUser().ifPresent(user -> {
            entity.setModifiedById(user.getId());
            entity.setModifier(user.getUsername());
        });
    }
    
    /**
     * Set both created and modified audit fields.
     * Useful when creating a new entity.
     * 
     * @param entity Entity to audit
     */
    public void setAuditFields(BaseEntity entity) {
        setCreatedAudit(entity);
        setModifiedAudit(entity);
    }
}

