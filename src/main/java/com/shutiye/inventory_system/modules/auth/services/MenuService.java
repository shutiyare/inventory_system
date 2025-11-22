package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.MenuCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.MenuDTO;
import com.shutiye.inventory_system.modules.auth.dtos.MenuUpdateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;

import java.util.List;

/**
 * Service interface for Menu operations.
 */
public interface MenuService {
    
    /**
     * Create a new menu item.
     * 
     * @param request Menu creation request
     * @return Created menu DTO
     */
    MenuDTO createMenu(MenuCreateRequest request);
    
    /**
     * Update an existing menu.
     * 
     * @param id Menu ID
     * @param request Menu update request
     * @return Updated menu DTO
     */
    MenuDTO updateMenu(Long id, MenuUpdateRequest request);
    
    /**
     * Delete a menu by ID.
     * 
     * @param id Menu ID
     */
    void deleteMenu(Long id);
    
    /**
     * Get all menus in a tree structure.
     * Returns only root menus with their children recursively loaded.
     * 
     * @return List of root menu DTOs with nested children
     */
    List<MenuDTO> getMenuTree();
    
    /**
     * Get all menus (flat list, without pagination).
     * 
     * @return List of all menu DTOs
     */
    List<MenuDTO> getAllMenus();
    
    /**
     * Get menus with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter request
     * @return Paginated response with menu DTOs
     */
    PageResponseDTO<MenuDTO> getAllMenus(PageRequestDTO pageRequest);
    
    /**
     * Get a menu by ID.
     * 
     * @param id Menu ID
     * @return Menu DTO if found
     */
    MenuDTO getMenuById(Long id);
}

