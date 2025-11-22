package com.shutiye.inventory_system.modules.auth.utils;

import com.shutiye.inventory_system.entity.Menu;
import com.shutiye.inventory_system.entity.Permission;
import com.shutiye.inventory_system.entity.Role;
import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.repository.MenuRepository;
import com.shutiye.inventory_system.repository.PermissionRepository;
import com.shutiye.inventory_system.repository.RoleRepository;
import com.shutiye.inventory_system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data seeder component.
 * Seeds initial data into the database on application startup.
 * Creates super admin role, basic permissions, default menus, and default admin user.
 */
@Component
public class DataSeeder implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataSeeder(RoleRepository roleRepository,
                     PermissionRepository permissionRepository,
                     MenuRepository menuRepository,
                     UserRepository userRepository,
                     PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data seeding...");
        
        // Seed permissions first
        Set<Permission> permissions = seedPermissions();
        logger.info("Seeded {} permissions", permissions.size());
        
        // Seed menus
        Set<Menu> menus = seedMenus();
        logger.info("Seeded {} menus", menus.size());
        
        // Seed roles
        Role superAdminRole = seedRoles(permissions, menus);
        logger.info("Seeded roles");
        
        // Seed default admin user
        seedAdminUser(superAdminRole);
        logger.info("Seeded default admin user");
        
        logger.info("Data seeding completed successfully");
    }
    
    /**
     * Seed basic permissions.
     * Creates common permissions for user and product management.
     */
    private Set<Permission> seedPermissions() {
        // Use List first to avoid HashSet issues with lazy-loaded entities
        List<Permission> permissionList = new ArrayList<>();
        
        // User permissions
        permissionList.add(createPermissionIfNotExists("User View", "USER_VIEW", "View users"));
        permissionList.add(createPermissionIfNotExists("User Create", "USER_CREATE", "Create users"));
        permissionList.add(createPermissionIfNotExists("User Update", "USER_UPDATE", "Update users"));
        permissionList.add(createPermissionIfNotExists("User Delete", "USER_DELETE", "Delete users"));
        
        // Product permissions
        permissionList.add(createPermissionIfNotExists("Product View", "PRODUCT_VIEW", "View products"));
        permissionList.add(createPermissionIfNotExists("Product Create", "PRODUCT_CREATE", "Create products"));
        permissionList.add(createPermissionIfNotExists("Product Update", "PRODUCT_UPDATE", "Update products"));
        permissionList.add(createPermissionIfNotExists("Product Delete", "PRODUCT_DELETE", "Delete products"));
        
        // Inventory permissions
        permissionList.add(createPermissionIfNotExists("Inventory View", "INVENTORY_VIEW", "View inventory"));
        permissionList.add(createPermissionIfNotExists("Inventory Update", "INVENTORY_UPDATE", "Update inventory"));
        
        // Role permissions
        permissionList.add(createPermissionIfNotExists("Role View", "ROLE_VIEW", "View roles"));
        permissionList.add(createPermissionIfNotExists("Role Create", "ROLE_CREATE", "Create roles"));
        permissionList.add(createPermissionIfNotExists("Role Update", "ROLE_UPDATE", "Update roles"));
        permissionList.add(createPermissionIfNotExists("Role Delete", "ROLE_DELETE", "Delete roles"));
        
        // Permission permissions
        permissionList.add(createPermissionIfNotExists("Permission View", "PERMISSION_VIEW", "View permissions"));
        permissionList.add(createPermissionIfNotExists("Permission Create", "PERMISSION_CREATE", "Create permissions"));
        
        // Menu permissions
        permissionList.add(createPermissionIfNotExists("Menu View", "MENU_VIEW", "View menus"));
        permissionList.add(createPermissionIfNotExists("Menu Create", "MENU_CREATE", "Create menus"));
        permissionList.add(createPermissionIfNotExists("Menu Update", "MENU_UPDATE", "Update menus"));
        permissionList.add(createPermissionIfNotExists("Menu Delete", "MENU_DELETE", "Delete menus"));
        
        // Flush and clear before converting to Set
        entityManager.flush();
        entityManager.clear();
        
        // Reload all permissions fresh by their codes to avoid lazy loading issues
        Set<Permission> permissions = new HashSet<>();
        for (Permission p : permissionList) {
            permissionRepository.findByCode(p.getCode())
                    .ifPresent(permissions::add);
        }
        
        return permissions;
    }
    
    /**
     * Create permission if it doesn't exist.
     */
    private Permission createPermissionIfNotExists(String name, String code, String description) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> {
                    Permission permission = Permission.builder()
                            .name(name)
                            .code(code)
                            .description(description)
                            .build();
                    return permissionRepository.save(permission);
                });
    }
    
    /**
     * Seed default menus.
     * Creates hierarchical menu structure.
     */
    private Set<Menu> seedMenus() {
        // Use List first to avoid HashSet issues with lazy-loaded entities
        List<Menu> menuList = new ArrayList<>();
        
        // Root menus
        Menu dashboard = createMenuIfNotExists("Dashboard", "/dashboard", "dashboard", 1, null);
        Menu inventory = createMenuIfNotExists("Inventory", "/inventory", "inventory", 2, null);
        Menu users = createMenuIfNotExists("Users", "/users", "users", 3, null);
        Menu settings = createMenuIfNotExists("Settings", "/settings", "settings", 4, null);
        
        menuList.add(dashboard);
        menuList.add(inventory);
        menuList.add(users);
        menuList.add(settings);
        
        // Child menus for Inventory
        Menu products = createMenuIfNotExists("Products", "/inventory/products", "products", 1, inventory.getId());
        Menu stock = createMenuIfNotExists("Stock", "/inventory/stock", "stock", 2, inventory.getId());
        menuList.add(products);
        menuList.add(stock);
        
        // Child menus for Settings
        Menu roles = createMenuIfNotExists("Roles", "/settings/roles", "roles", 1, settings.getId());
        Menu permissions = createMenuIfNotExists("Permissions", "/settings/permissions", "permissions", 2, settings.getId());
        Menu menuManagement = createMenuIfNotExists("Menus", "/settings/menus", "menus", 3, settings.getId());
        menuList.add(roles);
        menuList.add(permissions);
        menuList.add(menuManagement);
        
        // Flush and clear before converting to Set
        entityManager.flush();
        entityManager.clear();
        
        // Reload all menus fresh by their paths to avoid lazy loading issues
        Set<Menu> menus = new HashSet<>();
        for (Menu m : menuList) {
            menuRepository.findByPath(m.getPath())
                    .ifPresent(menus::add);
        }
        
        return menus;
    }
    
    /**
     * Create menu if it doesn't exist.
     */
    private Menu createMenuIfNotExists(String title, String path, String icon, Integer orderIndex, Long parentId) {
        return menuRepository.findByPath(path)
                .orElseGet(() -> {
                    Menu menu = Menu.builder()
                            .title(title)
                            .path(path)
                            .icon(icon)
                            .orderIndex(orderIndex)
                            .build();
                    
                    if (parentId != null) {
                        Menu parent = menuRepository.findById(parentId)
                                .orElseThrow(() -> new RuntimeException("Parent menu not found"));
                        menu.setParent(parent);
                    }
                    
                    return menuRepository.save(menu);
                });
    }
    
    /**
     * Seed roles.
     * Creates super admin role with all permissions and menus.
     */
    private Role seedRoles(Set<Permission> permissions, Set<Menu> menus) {
        // Flush and clear to ensure we're working with fresh entities
        entityManager.flush();
        entityManager.clear();
        
        // Check if role exists first
        Role superAdmin = roleRepository.findByName("SUPER_ADMIN")
                .orElseGet(() -> {
                    // Create new role
                    Role role = Role.builder()
                            .name("SUPER_ADMIN")
                            .description("Super Administrator with full system access")
                            .build();
                    return roleRepository.save(role);
                });
        
        // Flush and clear again before modifying collections
        entityManager.flush();
        entityManager.clear();
        
        // Reload fresh to avoid lazy loading issues
        superAdmin = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
        
        // Detach to work with detached entity
        entityManager.detach(superAdmin);
        
        // Set permissions and menus (always set fresh collections)
        superAdmin.setPermissions(new HashSet<>(permissions));
        superAdmin.setMenus(new HashSet<>(menus));
        
        // Merge and save the role
        superAdmin = entityManager.merge(superAdmin);
        entityManager.flush();
        
        return superAdmin;
    }
    
    /**
     * Seed default admin user.
     * Creates admin user with super admin role.
     */
    private void seedAdminUser(Role superAdminRole) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@inventory.com")
                    .fullName("System Administrator")
                    .password(passwordEncoder.encode("admin123")) // Default password - should be changed in production
                    .active(true)
                    .build();
            
            Set<Role> roles = new HashSet<>();
            roles.add(superAdminRole);
            admin.setRoles(roles);
            
            userRepository.save(admin);
            logger.info("Default admin user created: admin / admin123");
        } else {
            logger.info("Admin user already exists, skipping creation");
        }
    }
}

