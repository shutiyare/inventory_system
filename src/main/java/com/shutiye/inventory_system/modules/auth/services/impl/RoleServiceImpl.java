package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.Menu;
import com.shutiye.inventory_system.entity.Permission;
import com.shutiye.inventory_system.entity.Role;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.RoleDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.RoleService;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.modules.auth.utils.PaginationHelper;
import com.shutiye.inventory_system.modules.auth.utils.SpecificationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import com.shutiye.inventory_system.repository.MenuRepository;
import com.shutiye.inventory_system.repository.PermissionRepository;
import com.shutiye.inventory_system.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of RoleService interface.
 * Handles role creation, permission assignment, and menu assignment.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final AuditingHelper auditingHelper;
    
    public RoleServiceImpl(RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          MenuRepository menuRepository,
                          AuditingHelper auditingHelper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    @CacheEvict(value = {"roles", "rolesPage"}, allEntries = true)
    public RoleDTO createRole(RoleCreateRequest request) {
        logger.info("Creating new role with name: {}", request.getName());
        
        // Validate role name uniqueness
        if (roleRepository.existsByName(request.getName())) {
            logger.warn("Role name already exists: {}", request.getName());
            throw new ResourceAlreadyExistsException("Role", "name", request.getName());
        }
        
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        auditingHelper.setAuditFields(role);
        
        Role savedRole = roleRepository.save(role);
        
        // Assign permissions and menus if provided
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(savedRole.getId(), request.getPermissionIds());
        }
        if (request.getMenuIds() != null && !request.getMenuIds().isEmpty()) {
            assignMenus(savedRole.getId(), request.getMenuIds());
        }
        
        logger.info("Role created successfully with ID: {}", savedRole.getId());
        
        return RoleDTO.fromEntity(roleRepository.findById(savedRole.getId()).orElse(savedRole));
    }
    
    @Override
    @CacheEvict(value = {"roles", "rolesPage", "role"}, key = "#id")
    public RoleDTO updateRole(Long id, RoleUpdateRequest request) {
        logger.info("Updating role with ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Role not found with ID: {}", id);
                    return new ResourceNotFoundException("Role", "id", id);
                });
        
        // Update fields if provided
        if (request.getName() != null) {
            // Check name uniqueness if changed
            if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
                throw new ResourceAlreadyExistsException("Role", "name", request.getName());
            }
            role.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        
        // Update permissions and menus if provided
        if (request.getPermissionIds() != null) {
            assignPermissions(id, request.getPermissionIds());
        }
        if (request.getMenuIds() != null) {
            assignMenus(id, request.getMenuIds());
        }
        
        auditingHelper.setModifiedAudit(role);
        
        Role updatedRole = roleRepository.save(role);
        logger.info("Role updated successfully with ID: {}", id);
        
        return RoleDTO.fromEntity(updatedRole);
    }
    
    @Override
    @CacheEvict(value = {"roles", "rolesPage", "role"}, key = "#id", allEntries = true)
    public void deleteRole(Long id) {
        logger.info("Deleting role with ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Role not found with ID: {}", id);
                    return new ResourceNotFoundException("Role", "id", id);
                });
        
        roleRepository.delete(role);
        logger.info("Role deleted successfully with ID: {}", id);
    }
    
    @Override
    @CacheEvict(value = {"roles", "rolesPage", "role"}, key = "#roleId")
    public RoleDTO assignPermissions(Long roleId, Set<Long> permissionIds) {
        logger.info("Assigning permissions to role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    logger.warn("Role not found with ID: {}", roleId);
                    return new ResourceNotFoundException("Role", "id", roleId);
                });
        
        // Fetch all permissions
        Set<Permission> permissions = permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> {
                            logger.warn("Permission not found with ID: {}", permissionId);
                            return new ResourceNotFoundException("Permission", "id", permissionId);
                        }))
                .collect(Collectors.toSet());
        
        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);
        logger.info("Permissions assigned successfully to role with ID: {}", roleId);
        
        return RoleDTO.fromEntity(updatedRole);
    }
    
    @Override
    @CacheEvict(value = {"roles", "rolesPage", "role"}, key = "#roleId")
    public RoleDTO assignMenus(Long roleId, Set<Long> menuIds) {
        logger.info("Assigning menus to role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    logger.warn("Role not found with ID: {}", roleId);
                    return new ResourceNotFoundException("Role", "id", roleId);
                });
        
        // Fetch all menus
        Set<Menu> menus = menuIds.stream()
                .map(menuId -> menuRepository.findById(menuId)
                        .orElseThrow(() -> {
                            logger.warn("Menu not found with ID: {}", menuId);
                            return new ResourceNotFoundException("Menu", "id", menuId);
                        }))
                .collect(Collectors.toSet());
        
        role.setMenus(menus);
        Role updatedRole = roleRepository.save(role);
        logger.info("Menus assigned successfully to role with ID: {}", roleId);
        
        return RoleDTO.fromEntity(updatedRole);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", unless = "#result == null || #result.isEmpty()")
    public List<RoleDTO> getAllRoles() {
        logger.debug("Fetching all roles (cache miss - fetching from database)");
        return roleRepository.findAll().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<RoleDTO> getAllRoles(PageRequestDTO pageRequest) {
        logger.debug("Fetching roles with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        
        Specification<Role> searchSpec = SpecificationHelper.buildSearchSpecification(
                pageRequest.getSearch(),
                "name", "description"
        );
        
        Specification<Role> filterSpec = SpecificationHelper.buildFilterSpecification(
                pageRequest.getFilters()
        );
        
        Specification<Role> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
        org.springframework.data.domain.Pageable pageable = PaginationHelper.createPageable(pageRequest);
        
        long totalRecords = roleRepository.count();
        Page<Role> page = combinedSpec != null 
                ? roleRepository.findAll(combinedSpec, pageable)
                : roleRepository.findAll(pageable);
        
        long filteredRecords = combinedSpec != null 
                ? roleRepository.count(combinedSpec)
                : totalRecords;
        
        List<RoleDTO> roleDTOs = page.getContent().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
        
        return PageResponseDTO.of(
                roleDTOs,
                totalRecords,
                filteredRecords,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "role", key = "#id", unless = "#result == null")
    public RoleDTO getRoleById(Long id) {
        logger.debug("Fetching role with ID: {} (cache miss - fetching from database)", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Role not found with ID: {}", id);
                    return new ResourceNotFoundException("Role", "id", id);
                });
        return RoleDTO.fromEntity(role);
    }
}

