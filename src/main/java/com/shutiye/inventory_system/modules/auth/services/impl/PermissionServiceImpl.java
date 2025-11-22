package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.Permission;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.PermissionService;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.modules.auth.utils.PaginationHelper;
import com.shutiye.inventory_system.modules.auth.utils.SpecificationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import com.shutiye.inventory_system.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PermissionService interface.
 * Handles permission creation and retrieval.
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    private final PermissionRepository permissionRepository;
    private final AuditingHelper auditingHelper;
    
    public PermissionServiceImpl(PermissionRepository permissionRepository,
                                AuditingHelper auditingHelper) {
        this.permissionRepository = permissionRepository;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    @CacheEvict(value = {"permissions", "permissionsPage"}, allEntries = true)
    public PermissionDTO createPermission(PermissionCreateRequest request) {
        logger.info("Creating new permission with code: {}", request.getCode());
        
        // Validate permission name uniqueness
        if (permissionRepository.existsByName(request.getName())) {
            logger.warn("Permission name already exists: {}", request.getName());
            throw new ResourceAlreadyExistsException("Permission", "name", request.getName());
        }
        
        // Validate permission code uniqueness
        if (permissionRepository.existsByCode(request.getCode())) {
            logger.warn("Permission code already exists: {}", request.getCode());
            throw new ResourceAlreadyExistsException("Permission", "code", request.getCode());
        }
        
        Permission permission = Permission.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();
        
        auditingHelper.setAuditFields(permission);
        
        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Permission created successfully with ID: {}", savedPermission.getId());
        
        return PermissionDTO.fromEntity(savedPermission);
    }
    
    @Override
    @CacheEvict(value = {"permissions", "permissionsPage", "permission"}, key = "#id")
    public PermissionDTO updatePermission(Long id, PermissionUpdateRequest request) {
        logger.info("Updating permission with ID: {}", id);
        
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Permission not found with ID: {}", id);
                    return new ResourceNotFoundException("Permission", "id", id);
                });
        
        // Update fields if provided
        if (request.getName() != null) {
            // Check name uniqueness if changed
            if (!permission.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
                throw new ResourceAlreadyExistsException("Permission", "name", request.getName());
            }
            permission.setName(request.getName());
        }
        
        if (request.getCode() != null) {
            // Check code uniqueness if changed
            if (!permission.getCode().equals(request.getCode()) && permissionRepository.existsByCode(request.getCode())) {
                throw new ResourceAlreadyExistsException("Permission", "code", request.getCode());
            }
            permission.setCode(request.getCode());
        }
        
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
        
        auditingHelper.setModifiedAudit(permission);
        
        Permission updatedPermission = permissionRepository.save(permission);
        logger.info("Permission updated successfully with ID: {}", id);
        
        return PermissionDTO.fromEntity(updatedPermission);
    }
    
    @Override
    @CacheEvict(value = {"permissions", "permissionsPage", "permission"}, key = "#id", allEntries = true)
    public void deletePermission(Long id) {
        logger.info("Deleting permission with ID: {}", id);
        
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Permission not found with ID: {}", id);
                    return new ResourceNotFoundException("Permission", "id", id);
                });
        
        permissionRepository.delete(permission);
        logger.info("Permission deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", unless = "#result == null || #result.isEmpty()")
    public List<PermissionDTO> getAllPermissions() {
        logger.debug("Fetching all permissions (cache miss - fetching from database)");
        return permissionRepository.findAll().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PermissionDTO> getAllPermissions(PageRequestDTO pageRequest) {
        logger.debug("Fetching permissions with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        
        Specification<Permission> searchSpec = SpecificationHelper.buildSearchSpecification(
                pageRequest.getSearch(),
                "name", "code", "description"
        );
        
        Specification<Permission> filterSpec = SpecificationHelper.buildFilterSpecification(
                pageRequest.getFilters()
        );
        
        Specification<Permission> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
        org.springframework.data.domain.Pageable pageable = PaginationHelper.createPageable(pageRequest);
        
        long totalRecords = permissionRepository.count();
        Page<Permission> page = combinedSpec != null 
                ? permissionRepository.findAll(combinedSpec, pageable)
                : permissionRepository.findAll(pageable);
        
        long filteredRecords = combinedSpec != null 
                ? permissionRepository.count(combinedSpec)
                : totalRecords;
        
        List<PermissionDTO> permissionDTOs = page.getContent().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
        
        return PageResponseDTO.of(
                permissionDTOs,
                totalRecords,
                filteredRecords,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permission", key = "#id", unless = "#result == null")
    public PermissionDTO getPermissionById(Long id) {
        logger.debug("Fetching permission with ID: {} (cache miss - fetching from database)", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Permission not found with ID: {}", id);
                    return new ResourceNotFoundException("Permission", "id", id);
                });
        return PermissionDTO.fromEntity(permission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByCode(String code) {
        logger.debug("Fetching permission with code: {}", code);
        Permission permission = permissionRepository.findByCode(code)
                .orElseThrow(() -> {
                    logger.warn("Permission not found with code: {}", code);
                    return new ResourceNotFoundException("Permission", "code", code);
                });
        return PermissionDTO.fromEntity(permission);
    }
}

