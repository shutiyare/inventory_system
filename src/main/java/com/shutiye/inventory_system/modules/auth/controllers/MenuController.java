package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.MenuCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.MenuDTO;
import com.shutiye.inventory_system.modules.auth.dtos.MenuUpdateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.services.MenuService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for menu management operations.
 * Requires authentication for all endpoints.
 */
@RestController
@RequestMapping("/api/menus")
@Tag(name = "Menus", description = "Menu management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    
    private final MenuService menuService;
    
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }
    
    /**
     * Create a new menu item.
     * 
     * @param request Menu creation request
     * @return Created menu DTO
     */
    @PostMapping
    @Operation(summary = "Create menu", description = "Create a new menu item")
    public ResponseEntity<MenuDTO> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        logger.info("Creating menu: {}", request.getTitle());
        MenuDTO menu = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(menu);
    }
    
    /**
     * Update a menu.
     * 
     * @param id Menu ID
     * @param request Menu update request
     * @return Updated menu DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update menu", description = "Update menu information")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable Long id,
                                             @Valid @RequestBody MenuUpdateRequest request) {
        logger.info("Updating menu with ID: {}", id);
        MenuDTO menu = menuService.updateMenu(id, request);
        return ResponseEntity.ok(menu);
    }
    
    /**
     * Delete a menu.
     * 
     * @param id Menu ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete menu", description = "Delete a menu by ID")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        logger.info("Deleting menu with ID: {}", id);
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all menus in tree structure.
     * Returns only root menus with their children recursively loaded.
     * 
     * @return List of root menu DTOs with nested children
     */
    @GetMapping("/tree")
    @Operation(summary = "Get menu tree", description = "Retrieve all menus in hierarchical tree structure")
    public ResponseEntity<List<MenuDTO>> getMenuTree() {
        logger.debug("Fetching menu tree");
        List<MenuDTO> menus = menuService.getMenuTree();
        return ResponseEntity.ok(menus);
    }
    
    /**
     * Get all menus (flat list, without pagination - for backward compatibility).
     * 
     * @return List of all menu DTOs
     */
    @GetMapping("/all")
    @Operation(summary = "Get all menus (no pagination)", description = "Retrieve all menus as a flat list without pagination")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        logger.debug("Fetching all menus");
        List<MenuDTO> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }
    
    /**
     * Get menus with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter parameters
     * @return Paginated response with menu DTOs
     */
    @GetMapping
    @Operation(summary = "Get menus (paginated)", 
               description = "Retrieve menus with pagination, search, and filtering. " +
                           "Supports: page, size, search (global), sortBy, sortDir, filters (field-specific)")
    public ResponseEntity<PageResponseDTO<MenuDTO>> getAllMenusPaginated(
            @ModelAttribute PageRequestDTO pageRequest) {
        logger.debug("Fetching menus with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        PageResponseDTO<MenuDTO> response = menuService.getAllMenus(pageRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get menu by ID.
     * 
     * @param id Menu ID
     * @return Menu DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get menu by ID", description = "Retrieve a specific menu by ID")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long id) {
        logger.debug("Fetching menu with ID: {}", id);
        MenuDTO menu = menuService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }
}

