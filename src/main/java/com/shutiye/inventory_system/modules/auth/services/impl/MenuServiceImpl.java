package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.Menu;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.MenuCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.MenuDTO;
import com.shutiye.inventory_system.modules.auth.dtos.MenuUpdateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.services.MenuService;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.modules.auth.utils.PaginationHelper;
import com.shutiye.inventory_system.modules.auth.utils.SpecificationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import com.shutiye.inventory_system.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MenuService interface.
 * Handles menu creation and retrieval, including tree structure building.
 */
@Service
@Transactional
public class MenuServiceImpl implements MenuService {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);
    
    private final MenuRepository menuRepository;
    private final AuditingHelper auditingHelper;
    
    public MenuServiceImpl(MenuRepository menuRepository,
                          AuditingHelper auditingHelper) {
        this.menuRepository = menuRepository;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    @CacheEvict(value = {"menus", "menusPage", "menuTree"}, allEntries = true)
    public MenuDTO createMenu(MenuCreateRequest request) {
        logger.info("Creating new menu with title: {}", request.getTitle());
        
        Menu menu = Menu.builder()
                .title(request.getTitle())
                .path(request.getPath())
                .icon(request.getIcon())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .build();
        
        // Set parent if provided
        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> {
                        logger.warn("Parent menu not found with ID: {}", request.getParentId());
                        return new ResourceNotFoundException("Menu", "id", request.getParentId());
                    });
            menu.setParent(parent);
        }
        
        auditingHelper.setAuditFields(menu);
        
        Menu savedMenu = menuRepository.save(menu);
        logger.info("Menu created successfully with ID: {}", savedMenu.getId());
        
        return MenuDTO.fromEntity(savedMenu);
    }
    
    @Override
    @CacheEvict(value = {"menus", "menusPage", "menuTree", "menu"}, key = "#id")
    public MenuDTO updateMenu(Long id, MenuUpdateRequest request) {
        logger.info("Updating menu with ID: {}", id);
        
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Menu not found with ID: {}", id);
                    return new ResourceNotFoundException("Menu", "id", id);
                });
        
        // Update fields if provided
        if (request.getTitle() != null) {
            menu.setTitle(request.getTitle());
        }
        
        if (request.getPath() != null) {
            menu.setPath(request.getPath());
        }
        
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        
        if (request.getOrderIndex() != null) {
            menu.setOrderIndex(request.getOrderIndex());
        }
        
        // Update parent if provided
        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu", "id", request.getParentId()));
            menu.setParent(parent);
        } else if (request.getParentId() == null && menu.getParent() != null) {
            // Explicitly set to null if parentId is null in request
            menu.setParent(null);
        }
        
        auditingHelper.setModifiedAudit(menu);
        
        Menu updatedMenu = menuRepository.save(menu);
        logger.info("Menu updated successfully with ID: {}", id);
        
        return MenuDTO.fromEntity(updatedMenu);
    }
    
    @Override
    @CacheEvict(value = {"menus", "menusPage", "menuTree", "menu"}, key = "#id", allEntries = true)
    public void deleteMenu(Long id) {
        logger.info("Deleting menu with ID: {}", id);
        
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Menu not found with ID: {}", id);
                    return new ResourceNotFoundException("Menu", "id", id);
                });
        
        menuRepository.delete(menu);
        logger.info("Menu deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menuTree", unless = "#result == null || #result.isEmpty()")
    public List<MenuDTO> getMenuTree() {
        logger.debug("Fetching menu tree structure (cache miss - fetching from database)");
        
        // Fetch all root menus (menus with no parent)
        List<Menu> rootMenus = menuRepository.findAllByParentIsNull();
        
        // Convert to DTOs - the fromEntity method will recursively convert children
        return rootMenus.stream()
                .map(MenuDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menus", unless = "#result == null || #result.isEmpty()")
    public List<MenuDTO> getAllMenus() {
        logger.debug("Fetching all menus (cache miss - fetching from database)");
        return menuRepository.findAll().stream()
                .map(MenuDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MenuDTO> getAllMenus(PageRequestDTO pageRequest) {
        logger.debug("Fetching menu with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        
        Specification<Menu> searchSpec = SpecificationHelper.buildSearchSpecification(
                pageRequest.getSearch(),
                "title", "path", "icon"
        );
        
        Specification<Menu> filterSpec = SpecificationHelper.buildFilterSpecification(
                pageRequest.getFilters()
        );
        
        Specification<Menu> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
        org.springframework.data.domain.Pageable pageable = PaginationHelper.createPageable(pageRequest);
        
        long totalRecords = menuRepository.count();
        Page<Menu> page = combinedSpec != null 
                ? menuRepository.findAll(combinedSpec, pageable)
                : menuRepository.findAll(pageable);
        
        long filteredRecords = combinedSpec != null 
                ? menuRepository.count(combinedSpec)
                : totalRecords;
        
        List<MenuDTO> menuDTOs = page.getContent().stream()
                .map(MenuDTO::fromEntity)
                .collect(Collectors.toList());
        
        return PageResponseDTO.of(
                menuDTOs,
                totalRecords,
                filteredRecords,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menu", key = "#id", unless = "#result == null")
    public MenuDTO getMenuById(Long id) {
        logger.debug("Fetching menu with ID: {} (cache miss - fetching from database)", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Menu not found with ID: {}", id);
                    return new ResourceNotFoundException("Menu", "id", id);
                });
        return MenuDTO.fromEntity(menu);
    }
}

