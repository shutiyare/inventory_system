# Doctype Implementation Guide

This guide provides step-by-step instructions for adding a new doctype (entity/model) to the Inventory System following the established patterns and best practices.

## Table of Contents
1. [Overview](#overview)
2. [Architecture Pattern](#architecture-pattern)
3. [Step-by-Step Implementation](#step-by-step-implementation)
4. [Example: Adding a "Product" Doctype](#example-adding-a-product-doctype)
5. [Pagination, Search, and Filtering](#pagination-search-and-filtering)
6. [Redis Caching](#redis-caching)
7. [Best Practices](#best-practices)
8. [Common Patterns](#common-patterns)

---

## Overview

Every doctype in the system follows a consistent structure with these components:

1. **Entity** - JPA entity extending `BaseEntity`
2. **Repository** - Spring Data JPA repository interface
3. **DTOs** - Data Transfer Objects (DTO, CreateRequest, UpdateRequest)
4. **Service Interface** - Service contract
5. **Service Implementation** - Business logic implementation
6. **Controller** - REST API endpoints

All doctypes automatically inherit:
- `id` (auto-generated)
- `createdDate`, `modifiedDate` (automatic timestamps)
- `createdById`, `modifiedById` (audit tracking)
- `owner`, `modifier` (username tracking)

---

## Architecture Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                     Controller Layer                        │
│  (REST endpoints: GET, POST, PUT, DELETE)                   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│  (Business logic, validation, transactions)                 │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer                         │
│  (Data access, custom queries)                              │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      Entity Layer                           │
│  (JPA entities, relationships)                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Step-by-Step Implementation

### Step 1: Create Entity

**Location:** `src/main/java/com/shutiye/inventory_system/entity/`

**Template:**
```java
package com.shutiye.inventory_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * [EntityName] entity description.
 * Extends BaseEntity for common auditing fields.
 */
@Entity
@Table(name = "[table_name]")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"[collection_fields]"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class [EntityName] extends BaseEntity {

    // Required fields
    @Column(nullable = false, unique = true, length = 100)
    private String [uniqueField];

    @Column(nullable = false, length = 255)
    private String [requiredField];

    // Optional fields
    @Column(length = 500)
    private String description;

    // Relationships (if any)
    // @ManyToOne, @OneToMany, @ManyToMany, etc.
    // Always use FetchType.LAZY except for special cases
}
```

**Key Points:**
- ✅ Always extend `BaseEntity`
- ✅ Use `@SuperBuilder` (not `@Builder`)
- ✅ Exclude collection fields from `@EqualsAndHashCode` to avoid lazy loading issues
- ✅ Use `FetchType.LAZY` for relationships (except special cases like `User.roles`)
- ✅ Use `@Column` annotations with appropriate constraints

---

### Step 2: Create Repository

**Location:** `src/main/java/com/shutiye/inventory_system/repository/`

**Template:**
```java
package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.[EntityName];
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for [EntityName] entity.
 * Provides data access methods for [entity] operations.
 */
@Repository
public interface [EntityName]Repository extends JpaRepository<[EntityName], Long> {
    
    /**
     * Find by unique field (if applicable).
     */
    Optional<[EntityName]> findBy[UniqueField](String [uniqueField]);
    
    /**
     * Check if unique field exists.
     */
    boolean existsBy[UniqueField](String [uniqueField]);
    
    // Add custom query methods as needed
}
```

**Key Points:**
- ✅ Extend `JpaRepository<Entity, Long>`
- ✅ Add `findBy*` methods for unique fields
- ✅ Add `existsBy*` methods for validation
- ✅ Use `Optional<>` for single results

---

### Step 3: Create DTOs

**Location:** `src/main/java/com/shutiye/inventory_system/modules/auth/dtos/`

#### 3.1: Main DTO (Response DTO)

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.[EntityName];
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for [EntityName] entity.
 * Used for transferring [entity] data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "[Entity] information")
public class [EntityName]DTO {
    
    @Schema(description = "[Entity] ID", example = "1")
    private Long id;
    
    @Schema(description = "[Field description]", example = "[example_value]")
    private String [fieldName];
    
    // Include all BaseEntity fields
    @Schema(description = "Creation date", example = "2025-11-22T10:00:00")
    private LocalDateTime createdDate;
    
    @Schema(description = "Last modification date", example = "2025-11-22T10:00:00")
    private LocalDateTime modifiedDate;
    
    @Schema(description = "ID of user who created this record", example = "1")
    private Long createdById;
    
    @Schema(description = "ID of user who last modified this record", example = "1")
    private Long modifiedById;
    
    @Schema(description = "Username of creator", example = "admin")
    private String owner;
    
    @Schema(description = "Username of last modifier", example = "admin")
    private String modifier;
    
    /**
     * Static utility method to convert [EntityName] entity to [EntityName]DTO.
     */
    public static [EntityName]DTO fromEntity([EntityName] entity) {
        if (entity == null) {
            return null;
        }
        
        return [EntityName]DTO.builder()
                .id(entity.getId())
                .[fieldName](entity.get[FieldName]())
                // Map all fields
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .createdById(entity.getCreatedById())
                .modifiedById(entity.getModifiedById())
                .owner(entity.getOwner())
                .modifier(entity.getModifier())
                .build();
    }
}
```

#### 3.2: Create Request DTO

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new [entity].
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new [entity]")
public class [EntityName]CreateRequest {
    
    @NotBlank(message = "[Field] is required")
    @Size(max = 100, message = "[Field] must not exceed 100 characters")
    @Schema(description = "[Field description]", example = "[example]", required = true)
    private String [fieldName];
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "[Entity] description", example = "[example]")
    private String description;
}
```

#### 3.3: Update Request DTO

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a [entity].
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update [entity] information")
public class [EntityName]UpdateRequest {
    
    @Size(max = 100, message = "[Field] must not exceed 100 characters")
    @Schema(description = "[Field description]", example = "[example]")
    private String [fieldName];
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "[Entity] description", example = "[example]")
    private String description;
}
```

**Key Points:**
- ✅ Always include `@Schema` annotations for Swagger documentation
- ✅ Use validation annotations (`@NotBlank`, `@Size`, `@Email`, etc.)
- ✅ CreateRequest: Use `@NotBlank` for required fields
- ✅ UpdateRequest: All fields optional (use `@Size` for validation)
- ✅ Include `fromEntity()` static method in main DTO

---

### Step 4: Create Service Interface

**Location:** `src/main/java/com/shutiye/inventory_system/modules/auth/services/`

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]CreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]DTO;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]UpdateRequest;

import java.util.List;

/**
 * Service interface for [EntityName] operations.
 */
public interface [EntityName]Service {
    
    /**
     * Create a new [entity].
     * 
     * @param request [Entity] creation request
     * @return Created [entity] DTO
     */
    [EntityName]DTO create[EntityName]([EntityName]CreateRequest request);
    
    /**
     * Update an existing [entity].
     * 
     * @param id [Entity] ID
     * @param request [Entity] update request
     * @return Updated [entity] DTO
     */
    [EntityName]DTO update[EntityName](Long id, [EntityName]UpdateRequest request);
    
    /**
     * Delete a [entity] by ID.
     * 
     * @param id [Entity] ID
     */
    void delete[EntityName](Long id);
    
    /**
     * Get all [entities] in the system.
     * 
     * @return List of all [entity] DTOs
     */
    List<[EntityName]DTO> getAll[EntityName]s();
    
    /**
     * Get a [entity] by ID.
     * 
     * @param id [Entity] ID
     * @return [Entity] DTO if found
     */
    [EntityName]DTO get[EntityName]ById(Long id);
}
```

---

### Step 5: Create Service Implementation

**Location:** `src/main/java/com/shutiye/inventory_system/modules/auth/services/impl/`

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.[EntityName];
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]CreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]DTO;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]UpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.[EntityName]Service;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.repository.[EntityName]Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of [EntityName]Service interface.
 * Handles [entity] CRUD operations.
 */
@Service
@Transactional
public class [EntityName]ServiceImpl implements [EntityName]Service {
    
    private static final Logger logger = LoggerFactory.getLogger([EntityName]ServiceImpl.class);
    
    private final [EntityName]Repository [entityName]Repository;
    private final AuditingHelper auditingHelper;
    
    public [EntityName]ServiceImpl([EntityName]Repository [entityName]Repository,
                                   AuditingHelper auditingHelper) {
        this.[entityName]Repository = [entityName]Repository;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    public [EntityName]DTO create[EntityName]([EntityName]CreateRequest request) {
        logger.info("Creating new [entity] with [uniqueField]: {}", request.get[UniqueField]());
        
        // Validate uniqueness (if applicable)
        if ([entityName]Repository.existsBy[UniqueField](request.get[UniqueField]())) {
            logger.warn("[Entity] with [uniqueField] already exists: {}", request.get[UniqueField]());
            throw new ResourceAlreadyExistsException("[EntityName]", "[uniqueField]", request.get[UniqueField]());
        }
        
        // Build entity
        [EntityName] entity = [EntityName].builder()
                .[fieldName](request.get[FieldName]())
                .description(request.getDescription())
                .build();
        
        // Set auditing fields
        auditingHelper.setAuditFields(entity);
        
        // Save entity
        [EntityName] savedEntity = [entityName]Repository.save(entity);
        logger.info("[Entity] created successfully with ID: {}", savedEntity.getId());
        
        return [EntityName]DTO.fromEntity(savedEntity);
    }
    
    @Override
    public [EntityName]DTO update[EntityName](Long id, [EntityName]UpdateRequest request) {
        logger.info("Updating [entity] with ID: {}", id);
        
        // Find entity
        [EntityName] entity = [entityName]Repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[Entity] not found with ID: {}", id);
                    return new ResourceNotFoundException("[EntityName]", "id", id);
                });
        
        // Update fields if provided
        if (request.get[FieldName]() != null) {
            // Check uniqueness if changed (if applicable)
            if (!entity.get[FieldName]().equals(request.get[FieldName]()) 
                    && [entityName]Repository.existsBy[UniqueField](request.get[FieldName]())) {
                throw new ResourceAlreadyExistsException("[EntityName]", "[uniqueField]", request.get[FieldName]());
            }
            entity.set[FieldName](request.get[FieldName]());
        }
        
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        
        // Update auditing fields
        auditingHelper.setModifiedAudit(entity);
        
        // Save updated entity
        [EntityName] updatedEntity = [entityName]Repository.save(entity);
        logger.info("[Entity] updated successfully with ID: {}", id);
        
        return [EntityName]DTO.fromEntity(updatedEntity);
    }
    
    @Override
    public void delete[EntityName](Long id) {
        logger.info("Deleting [entity] with ID: {}", id);
        
        // Find entity
        [EntityName] entity = [entityName]Repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[Entity] not found with ID: {}", id);
                    return new ResourceNotFoundException("[EntityName]", "id", id);
                });
        
        // Delete entity
        [entityName]Repository.delete(entity);
        logger.info("[Entity] deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<[EntityName]DTO> getAll[EntityName]s() {
        logger.debug("Fetching all [entities]");
        return [entityName]Repository.findAll().stream()
                .map([EntityName]DTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public [EntityName]DTO get[EntityName]ById(Long id) {
        logger.debug("Fetching [entity] with ID: {}", id);
        [EntityName] entity = [entityName]Repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[Entity] not found with ID: {}", id);
                    return new ResourceNotFoundException("[EntityName]", "id", id);
                });
        return [EntityName]DTO.fromEntity(entity);
    }
}
```

**Key Points:**
- ✅ Inject `AuditingHelper` for audit field management
- ✅ Use `@Transactional` for write operations
- ✅ Use `@Transactional(readOnly = true)` for read operations
- ✅ Validate uniqueness before create/update
- ✅ Use `ResourceNotFoundException` and `ResourceAlreadyExistsException`
- ✅ Log all operations

---

### Step 6: Create Controller

**Location:** `src/main/java/com/shutiye/inventory_system/modules/auth/controllers/`

**Template:**
```java
package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]CreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]DTO;
import com.shutiye.inventory_system.modules.auth.dtos.[EntityName]UpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.[EntityName]Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for [entity] management operations.
 * Requires authentication for all endpoints.
 */
@RestController
@RequestMapping("/api/[entities]")
@Tag(name = "[Entities]", description = "[Entity] management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class [EntityName]Controller {
    
    private static final Logger logger = LoggerFactory.getLogger([EntityName]Controller.class);
    
    private final [EntityName]Service [entityName]Service;
    
    public [EntityName]Controller([EntityName]Service [entityName]Service) {
        this.[entityName]Service = [entityName]Service;
    }
    
    /**
     * Create a new [entity].
     * 
     * @param request [Entity] creation request
     * @return Created [entity] DTO
     */
    @PostMapping
    @Operation(summary = "Create [entity]", description = "Create a new [entity]")
    public ResponseEntity<[EntityName]DTO> create[EntityName](@Valid @RequestBody [EntityName]CreateRequest request) {
        logger.info("Creating [entity]: {}", request.get[UniqueField]());
        [EntityName]DTO [entityName] = [entityName]Service.create[EntityName](request);
        return ResponseEntity.status(HttpStatus.CREATED).body([entityName]);
    }
    
    /**
     * Get all [entities].
     * 
     * @return List of all [entity] DTOs
     */
    @GetMapping
    @Operation(summary = "Get all [entities]", description = "Retrieve all [entities] in the system")
    public ResponseEntity<List<[EntityName]DTO>> getAll[EntityName]s() {
        logger.debug("Fetching all [entities]");
        List<[EntityName]DTO> [entities] = [entityName]Service.getAll[EntityName]s();
        return ResponseEntity.ok([entities]);
    }
    
    /**
     * Get [entity] by ID.
     * 
     * @param id [Entity] ID
     * @return [Entity] DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get [entity] by ID", description = "Retrieve a specific [entity] by ID")
    public ResponseEntity<[EntityName]DTO> get[EntityName]ById(@PathVariable Long id) {
        logger.debug("Fetching [entity] with ID: {}", id);
        [EntityName]DTO [entityName] = [entityName]Service.get[EntityName]ById(id);
        return ResponseEntity.ok([entityName]);
    }
    
    /**
     * Update a [entity].
     * 
     * @param id [Entity] ID
     * @param request [Entity] update request
     * @return Updated [entity] DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update [entity]", description = "Update [entity] information")
    public ResponseEntity<[EntityName]DTO> update[EntityName](@PathVariable Long id,
                                                             @Valid @RequestBody [EntityName]UpdateRequest request) {
        logger.info("Updating [entity] with ID: {}", id);
        [EntityName]DTO [entityName] = [entityName]Service.update[EntityName](id, request);
        return ResponseEntity.ok([entityName]);
    }
    
    /**
     * Delete a [entity].
     * 
     * @param id [Entity] ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete [entity]", description = "Delete a [entity] by ID")
    public ResponseEntity<Void> delete[EntityName](@PathVariable Long id) {
        logger.info("Deleting [entity] with ID: {}", id);
        [entityName]Service.delete[EntityName](id);
        return ResponseEntity.noContent().build();
    }
}
```

**Key Points:**
- ✅ Use `@RestController` and `@RequestMapping`
- ✅ Add `@Tag` for Swagger grouping
- ✅ Add `@SecurityRequirement(name = "bearerAuth")` for authentication
- ✅ Use `@Valid` for request validation
- ✅ Use appropriate HTTP status codes (CREATED for POST, NO_CONTENT for DELETE)
- ✅ Log all operations

---

## Example: Adding a "Product" Doctype

Let's walk through adding a complete "Product" doctype as a concrete example.

### 1. Entity: `Product.java`

```java
package com.shutiye.inventory_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String sku; // Stock Keeping Unit

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean active = true;
}
```

### 2. Repository: `ProductRepository.java`

```java
package com.shutiye.inventory_system.repository;

import com.shutiye.inventory_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
}
```

### 3. DTOs

**ProductDTO.java:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product information")
public class ProductDTO {
    
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product SKU", example = "PROD-001")
    private String sku;
    
    @Schema(description = "Product name", example = "Laptop")
    private String name;
    
    @Schema(description = "Product description", example = "High-performance laptop")
    private String description;
    
    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;
    
    @Schema(description = "Stock quantity", example = "50")
    private Integer stockQuantity;
    
    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active;
    
    // BaseEntity fields
    @Schema(description = "Creation date", example = "2025-11-22T10:00:00")
    private LocalDateTime createdDate;
    
    @Schema(description = "Last modification date", example = "2025-11-22T10:00:00")
    private LocalDateTime modifiedDate;
    
    @Schema(description = "ID of user who created this record", example = "1")
    private Long createdById;
    
    @Schema(description = "ID of user who last modified this record", example = "1")
    private Long modifiedById;
    
    @Schema(description = "Username of creator", example = "admin")
    private String owner;
    
    @Schema(description = "Username of last modifier", example = "admin")
    private String modifier;
    
    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .createdDate(product.getCreatedDate())
                .modifiedDate(product.getModifiedDate())
                .createdById(product.getCreatedById())
                .modifiedById(product.getModifiedById())
                .owner(product.getOwner())
                .modifier(product.getModifier())
                .build();
    }
}
```

**ProductCreateRequest.java:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new product")
public class ProductCreateRequest {
    
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Schema(description = "Product SKU", example = "PROD-001", required = true)
    private String sku;
    
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    @Schema(description = "Product name", example = "Laptop", required = true)
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "High-performance laptop")
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Schema(description = "Product price", example = "999.99", required = true)
    private BigDecimal price;
    
    @Schema(description = "Stock quantity", example = "50")
    private Integer stockQuantity;
    
    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active;
}
```

**ProductUpdateRequest.java:**
```java
package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update product information")
public class ProductUpdateRequest {
    
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Schema(description = "Product SKU", example = "PROD-001")
    private String sku;
    
    @Size(max = 200, message = "Name must not exceed 200 characters")
    @Schema(description = "Product name", example = "Laptop")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "High-performance laptop")
    private String description;
    
    @Positive(message = "Price must be positive")
    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;
    
    @Schema(description = "Stock quantity", example = "50")
    private Integer stockQuantity;
    
    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active;
}
```

### 4. Service Interface: `ProductService.java`

```java
package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.ProductCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.ProductDTO;
import com.shutiye.inventory_system.modules.auth.dtos.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductCreateRequest request);
    ProductDTO updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
}
```

### 5. Service Implementation: `ProductServiceImpl.java`

```java
package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.Product;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.ProductCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.ProductDTO;
import com.shutiye.inventory_system.modules.auth.dtos.ProductUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.ProductService;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ProductRepository productRepository;
    private final AuditingHelper auditingHelper;
    
    public ProductServiceImpl(ProductRepository productRepository,
                              AuditingHelper auditingHelper) {
        this.productRepository = productRepository;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        logger.info("Creating new product with SKU: {}", request.getSku());
        
        if (productRepository.existsBySku(request.getSku())) {
            logger.warn("Product with SKU already exists: {}", request.getSku());
            throw new ResourceAlreadyExistsException("Product", "sku", request.getSku());
        }
        
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        
        auditingHelper.setAuditFields(product);
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return ProductDTO.fromEntity(savedProduct);
    }
    
    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        logger.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });
        
        if (request.getSku() != null) {
            if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
                throw new ResourceAlreadyExistsException("Product", "sku", request.getSku());
            }
            product.setSku(request.getSku());
        }
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        
        auditingHelper.setModifiedAudit(product);
        
        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", id);
        
        return ProductDTO.fromEntity(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });
        
        productRepository.delete(product);
        logger.info("Product deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        logger.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        logger.debug("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });
        return ProductDTO.fromEntity(product);
    }
}
```

### 6. Controller: `ProductController.java`

```java
package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.ProductCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.ProductDTO;
import com.shutiye.inventory_system.modules.auth.dtos.ProductUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        logger.info("Creating product: {}", request.getSku());
        ProductDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products in the system")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        logger.debug("Fetching all products");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        logger.debug("Fetching product with ID: {}", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update product information")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @Valid @RequestBody ProductUpdateRequest request) {
        logger.info("Updating product with ID: {}", id);
        ProductDTO product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Redis Caching

All doctypes implement Redis caching to improve performance by reducing database queries for frequently accessed data.

### Overview

The system uses Spring Cache abstraction with Redis as the cache provider:
- **Automatic caching** of frequently accessed data
- **Cache invalidation** on create/update/delete operations
- **TTL (Time To Live)** - Cache expires after 1 hour
- **JSON serialization** for complex objects
- **Connection pooling** for optimal performance

### Cache Names Convention

Use consistent cache names following this pattern:
- `[entityName]s` - List of all entities (e.g., `users`, `roles`, `permissions`)
- `[entityName]` - Individual entity by ID (e.g., `user`, `role`, `permission`)
- `[entityName]sPage` - Paginated results (e.g., `usersPage`, `rolesPage`)
- `[entityName]Tree` - Tree structures (e.g., `menuTree`)

### Implementation Steps

#### Step 1: Add Cache Annotations to Service Implementation

**Required Imports:**
```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
```

**Cache GET Methods:**
```java
@Override
@Transactional(readOnly = true)
@Cacheable(value = "[entityName]s", unless = "#result == null || #result.isEmpty()")
public List<[EntityName]DTO> getAll[EntityName]s() {
    logger.debug("Fetching all [entities] (cache miss - fetching from database)");
    return [entityName]Repository.findAll().stream()
            .map([EntityName]DTO::fromEntity)
            .collect(Collectors.toList());
}

@Override
@Transactional(readOnly = true)
@Cacheable(value = "[entityName]", key = "#id", unless = "#result == null")
public [EntityName]DTO get[EntityName]ById(Long id) {
    logger.debug("Fetching [entity] with ID: {} (cache miss - fetching from database)", id);
    [EntityName] entity = [entityName]Repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("[EntityName]", "id", id));
    return [EntityName]DTO.fromEntity(entity);
}
```

**Evict Cache on Create/Update/Delete:**
```java
@Override
@CacheEvict(value = {"[entityName]s", "[entityName]sPage"}, allEntries = true)
public [EntityName]DTO create[EntityName]([EntityName]CreateRequest request) {
    // ... implementation
}

@Override
@CacheEvict(value = {"[entityName]s", "[entityName]sPage", "[entityName]"}, key = "#id")
public [EntityName]DTO update[EntityName](Long id, [EntityName]UpdateRequest request) {
    // ... implementation
}

@Override
@CacheEvict(value = {"[entityName]s", "[entityName]sPage", "[entityName]"}, key = "#id", allEntries = true)
public void delete[EntityName](Long id) {
    // ... implementation
}
```

**Evict Cache on Relationship Updates:**
```java
@Override
@CacheEvict(value = {"[entityName]s", "[entityName]sPage", "[entityName]"}, key = "#id")
public [EntityName]DTO assign[RelatedEntity]s(Long id, Set<Long> [relatedEntity]Ids) {
    // ... implementation
}
```

### Cache Annotations Explained

#### @Cacheable
- **Purpose**: Cache the result of a method
- **When**: Used on GET/READ methods
- **Key**: `value` = cache name, `key` = cache key (default: method parameters)
- **Condition**: `unless` = condition to skip caching

**Example:**
```java
@Cacheable(value = "users", unless = "#result == null || #result.isEmpty()")
public List<UserDTO> getAllUsers() { ... }

@Cacheable(value = "user", key = "#id", unless = "#result == null")
public UserDTO getUserById(Long id) { ... }
```

#### @CacheEvict
- **Purpose**: Remove entries from cache
- **When**: Used on CREATE/UPDATE/DELETE methods
- **Key**: `value` = cache name(s), `key` = specific key to evict
- **allEntries**: If true, clears entire cache

**Example:**
```java
@CacheEvict(value = {"users", "usersPage"}, allEntries = true)
public UserDTO createUser(...) { ... }

@CacheEvict(value = {"users", "usersPage", "user"}, key = "#id")
public UserDTO updateUser(Long id, ...) { ... }
```

### Complete Example: Product Service with Caching

```java
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    // ... dependencies
    
    @Override
    @CacheEvict(value = {"products", "productsPage"}, allEntries = true)
    public ProductDTO createProduct(ProductCreateRequest request) {
        // ... create logic
    }
    
    @Override
    @CacheEvict(value = {"products", "productsPage", "product"}, key = "#id")
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        // ... update logic
    }
    
    @Override
    @CacheEvict(value = {"products", "productsPage", "product"}, key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        // ... delete logic
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", unless = "#result == null || #result.isEmpty()")
    public List<ProductDTO> getAllProducts() {
        logger.debug("Fetching all products (cache miss - fetching from database)");
        return productRepository.findAll().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public ProductDTO getProductById(Long id) {
        logger.debug("Fetching product with ID: {} (cache miss - fetching from database)", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductDTO.fromEntity(product);
    }
}
```

### Cache Configuration

Cache is configured in `application.properties`:

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000  # 1 hour
spring.cache.redis.cache-null-values=false
spring.cache.redis.key-prefix=inventory:
```

### Performance Benefits

**Without Cache:**
- Every request: Database query (~10-50ms)
- High database load
- Slower response times

**With Cache:**
- First request: Database query + cache write (~10-50ms)
- Subsequent requests: Cache lookup (~1-5ms)
- **~95% faster** for cached requests
- Reduced database load by ~95%

### Best Practices

1. **Cache Read Operations**: Use `@Cacheable` on all GET methods
2. **Evict on Write**: Use `@CacheEvict` on all CREATE/UPDATE/DELETE methods
3. **Evict Related Caches**: Clear list caches when individual items change
4. **Use Appropriate Keys**: Use entity ID as cache key for individual items
5. **Conditional Caching**: Use `unless` to avoid caching null/empty results
6. **Log Cache Misses**: Add logging to track cache performance

### Cache Keys in Redis

Cache keys follow this format:
- `inventory:users::SimpleKey []` - All users list
- `inventory:user:1` - User with ID 1
- `inventory:roles::SimpleKey []` - All roles list
- `inventory:role:2` - Role with ID 2

### Monitoring Cache

**Check cached keys:**
```bash
redis-cli KEYS "inventory:*"
```

**Get specific cache:**
```bash
redis-cli GET "inventory:users::SimpleKey []"
```

**Check TTL:**
```bash
redis-cli TTL "inventory:users::SimpleKey []"
```

**Clear specific cache:**
```bash
redis-cli DEL "inventory:users::SimpleKey []"
```

### Troubleshooting

**Cache not working:**
1. Check Redis is running: `redis-cli ping`
2. Verify `@EnableCaching` is in configuration
3. Check cache annotations are on service methods
4. Review application logs for cache errors

**Cache not invalidating:**
1. Ensure `@CacheEvict` is on all write methods
2. Check cache names match between `@Cacheable` and `@CacheEvict`
3. Verify `allEntries = true` when needed

---

## Best Practices

### 1. Entity Design
- ✅ Always extend `BaseEntity`
- ✅ Use `@SuperBuilder` (not `@Builder`)
- ✅ Exclude collections from `@EqualsAndHashCode`
- ✅ Use `FetchType.LAZY` for relationships
- ✅ Add proper `@Column` constraints
- ✅ Use appropriate data types (BigDecimal for money, etc.)

### 2. Repository Design
- ✅ Add `findBy*` methods for unique fields
- ✅ Add `existsBy*` methods for validation
- ✅ Use `Optional<>` for single results
- ✅ Add custom queries only when needed

### 3. DTO Design
- ✅ Never expose sensitive data (passwords, etc.)
- ✅ Always include `@Schema` annotations
- ✅ Use validation annotations appropriately
- ✅ CreateRequest: Required fields with `@NotBlank`
- ✅ UpdateRequest: All fields optional
- ✅ Include `fromEntity()` static method

### 4. Service Design
- ✅ Inject `AuditingHelper` for audit fields
- ✅ Use `@Transactional` for write operations
- ✅ Use `@Transactional(readOnly = true)` for reads
- ✅ Validate uniqueness before create/update
- ✅ Use proper exception types
- ✅ Log all operations

### 5. Controller Design
- ✅ Use RESTful conventions
- ✅ Add Swagger annotations
- ✅ Use `@Valid` for validation
- ✅ Return appropriate HTTP status codes
- ✅ Log all operations

---

## Common Patterns

### Pattern 1: Relationships

**Many-to-Many:**
```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "entity1_entity2",
    joinColumns = @JoinColumn(name = "entity1_id"),
    inverseJoinColumns = @JoinColumn(name = "entity2_id")
)
private Set<Entity2> entity2s = new HashSet<>();
```

**One-to-Many:**
```java
@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private Set<Child> children = new HashSet<>();
```

**Many-to-One:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id")
private Parent parent;
```

### Pattern 2: Custom Service Methods

For additional business logic:
```java
// In Service Interface
List<EntityDTO> findByStatus(String status);
EntityDTO activate(Long id);
EntityDTO deactivate(Long id);

// In Service Implementation
@Override
public List<EntityDTO> findByStatus(String status) {
    return repository.findByStatus(status).stream()
            .map(EntityDTO::fromEntity)
            .collect(Collectors.toList());
}
```

### Pattern 3: Custom Endpoints

For additional REST endpoints:
```java
@PutMapping("/{id}/activate")
@Operation(summary = "Activate entity", description = "Activate an entity")
public ResponseEntity<EntityDTO> activate(@PathVariable Long id) {
    EntityDTO entity = entityService.activate(id);
    return ResponseEntity.ok(entity);
}
```

---

## Pagination, Search, and Filtering

All "get all" endpoints support pagination, search, and filtering using a DataTable-like format.

### Overview

The system provides:
- **Pagination**: Page-based navigation (page number, page size)
- **Global Search**: Search across multiple fields simultaneously
- **Field Filters**: Filter by specific field values
- **Sorting**: Sort by any column in ascending/descending order

### Common DTOs

The system provides two common DTOs that all doctypes use:

#### PageRequestDTO
Request DTO for pagination parameters:
```java
{
  "page": 0,              // Page number (0-indexed)
  "size": 10,             // Records per page
  "search": "john",       // Global search term
  "sortBy": "username",   // Column to sort by
  "sortDir": "asc",       // Sort direction: "asc" or "desc"
  "filters": {            // Field-specific filters
    "active": "true",
    "roleId": "1"
  }
}
```

#### PageResponseDTO
Response DTO with paginated data:
```java
{
  "data": [...],          // Array of entity DTOs
  "totalRecords": 100,    // Total records (before filtering)
  "filteredRecords": 50,  // Total records (after filtering)
  "currentPage": 0,       // Current page number
  "pageSize": 10,         // Records per page
  "totalPages": 5,        // Total number of pages
  "hasNext": true,        // Whether there is a next page
  "hasPrevious": false    // Whether there is a previous page
}
```

### Implementation Steps

#### Step 1: Update Repository

Add `JpaSpecificationExecutor` to your repository:

```java
@Repository
public interface [EntityName]Repository extends JpaRepository<[EntityName], Long>, 
                                                JpaSpecificationExecutor<[EntityName]> {
    // ... existing methods
}
```

#### Step 2: Update Service Interface

Add pagination method to service interface:

```java
public interface [EntityName]Service {
    // ... existing methods
    
    /**
     * Get all [entities] (without pagination - for backward compatibility).
     */
    List<[EntityName]DTO> getAll[EntityName]s();
    
    /**
     * Get [entities] with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter request
     * @return Paginated response with [entity] DTOs
     */
    PageResponseDTO<[EntityName]DTO> getAll[EntityName]s(PageRequestDTO pageRequest);
}
```

#### Step 3: Update Service Implementation

Implement pagination method:

```java
@Override
@Transactional(readOnly = true)
public PageResponseDTO<[EntityName]DTO> getAll[EntityName]s(PageRequestDTO pageRequest) {
    logger.debug("Fetching [entities] with pagination: page={}, size={}, search={}", 
            pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
    
    // Build specifications for search and filters
    Specification<[EntityName]> searchSpec = SpecificationHelper.buildSearchSpecification(
            pageRequest.getSearch(),
            "field1", "field2", "field3"  // Fields to search in
    );
    
    Specification<[EntityName]> filterSpec = SpecificationHelper.buildFilterSpecification(
            pageRequest.getFilters()
    );
    
    // Combine specifications
    Specification<[EntityName]> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
    
    // Create pageable
    Pageable pageable = PaginationHelper.createPageable(pageRequest);
    
    // Get total count (before filtering)
    long totalRecords = [entityName]Repository.count();
    
    // Execute query with pagination
    Page<[EntityName]> page = combinedSpec != null 
            ? [entityName]Repository.findAll(combinedSpec, pageable)
            : [entityName]Repository.findAll(pageable);
    
    // Get filtered count
    long filteredRecords = combinedSpec != null 
            ? [entityName]Repository.count(combinedSpec)
            : totalRecords;
    
    // Convert to DTOs
    List<[EntityName]DTO> dtos = page.getContent().stream()
            .map([EntityName]DTO::fromEntity)
            .collect(Collectors.toList());
    
    return PageResponseDTO.of(
            dtos,
            totalRecords,
            filteredRecords,
            pageRequest.getPageNumber(),
            pageRequest.getPageSize()
    );
}
```

**Required Imports:**
```java
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.utils.PaginationHelper;
import com.shutiye.inventory_system.modules.auth.utils.SpecificationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
```

#### Step 4: Update Controller

Add paginated endpoint:

```java
/**
 * Get all [entities] (without pagination - for backward compatibility).
 */
@GetMapping("/all")
@Operation(summary = "Get all [entities] (no pagination)", 
           description = "Retrieve all [entities] without pagination")
public ResponseEntity<List<[EntityName]DTO>> getAll[EntityName]s() {
    logger.debug("Fetching all [entities]");
    List<[EntityName]DTO> [entities] = [entityName]Service.getAll[EntityName]s();
    return ResponseEntity.ok([entities]);
}

/**
 * Get [entities] with pagination, search, and filtering.
 * 
 * @param pageRequest Pagination, search, and filter parameters
 * @return Paginated response with [entity] DTOs
 */
@GetMapping
@Operation(summary = "Get [entities] (paginated)", 
           description = "Retrieve [entities] with pagination, search, and filtering. " +
                       "Supports: page, size, search (global), sortBy, sortDir, filters (field-specific)")
public ResponseEntity<PageResponseDTO<[EntityName]DTO>> getAll[EntityName]sPaginated(
        @ModelAttribute PageRequestDTO pageRequest) {
    logger.debug("Fetching [entities] with pagination: page={}, size={}, search={}", 
            pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
    PageResponseDTO<[EntityName]DTO> response = [entityName]Service.getAll[EntityName]s(pageRequest);
    return ResponseEntity.ok(response);
}
```

### Usage Examples

#### Example 1: Basic Pagination

**Request:**
```
GET /api/users?page=0&size=10
```

**Response:**
```json
{
  "data": [...],
  "totalRecords": 100,
  "filteredRecords": 100,
  "currentPage": 0,
  "pageSize": 10,
  "totalPages": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Example 2: Pagination with Search

**Request:**
```
GET /api/users?page=0&size=10&search=john
```

Searches for "john" in: username, email, fullName fields.

#### Example 3: Pagination with Sorting

**Request:**
```
GET /api/users?page=0&size=10&sortBy=username&sortDir=desc
```

Sorts by username in descending order.

#### Example 4: Pagination with Filters

**Request:**
```
GET /api/users?page=0&size=10&filters[active]=true&filters[roleId]=1
```

Filters users where `active=true` AND `roleId=1`.

#### Example 5: Combined (Search + Filter + Sort + Pagination)

**Request:**
```
GET /api/users?page=0&size=20&search=admin&sortBy=createdDate&sortDir=desc&filters[active]=true
```

Combines all features:
- Search for "admin" in username/email/fullName
- Filter by active=true
- Sort by createdDate descending
- Paginate: page 0, 20 records per page

### Searchable Fields

When implementing `buildSearchSpecification`, specify which fields should be searchable:

```java
SpecificationHelper.buildSearchSpecification(
    pageRequest.getSearch(),
    "username",      // Search in username field
    "email",         // Search in email field
    "fullName"       // Search in fullName field
);
```

**Best Practices:**
- Include text fields that users commonly search
- Exclude sensitive fields (passwords, tokens)
- Exclude large text fields (descriptions can be included if needed)
- For relationships, use nested field notation: `"role.name"`

### Filterable Fields

Filters work automatically for all fields. The system detects field types:

- **Boolean fields**: Exact match (`true`/`false`)
- **Numeric fields**: Exact match (Long, Integer)
- **String fields**: Case-insensitive exact match or pattern match (if contains `%` or `*`)

**Example Filters:**
```json
{
  "filters": {
    "active": "true",           // Boolean filter
    "roleId": "1",              // Numeric filter
    "username": "admin",        // String exact match
    "email": "%@example.com"    // String pattern match
  }
}
```

### Utilities

The system provides two utility classes:

#### PaginationHelper
```java
// Create Pageable from PageRequestDTO
Pageable pageable = PaginationHelper.createPageable(pageRequest);

// Create Pageable with custom parameters
Pageable pageable = PaginationHelper.createPageable(0, 10, "username", true);
```

#### SpecificationHelper
```java
// Build search specification
Specification<Entity> searchSpec = SpecificationHelper.buildSearchSpecification(
    "search term",
    "field1", "field2"
);

// Build filter specification
Specification<Entity> filterSpec = SpecificationHelper.buildFilterSpecification(
    Map.of("active", "true", "roleId", "1")
);

// Combine specifications
Specification<Entity> combined = SpecificationHelper.and(searchSpec, filterSpec);
```

### Complete Example: Product Service with Pagination

```java
@Override
@Transactional(readOnly = true)
public PageResponseDTO<ProductDTO> getAllProducts(PageRequestDTO pageRequest) {
    logger.debug("Fetching products with pagination: page={}, size={}, search={}", 
            pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
    
    // Build search specification (search in name, sku, description)
    Specification<Product> searchSpec = SpecificationHelper.buildSearchSpecification(
            pageRequest.getSearch(),
            "name", "sku", "description"
    );
    
    // Build filter specification
    Specification<Product> filterSpec = SpecificationHelper.buildFilterSpecification(
            pageRequest.getFilters()
    );
    
    // Combine specifications
    Specification<Product> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
    
    // Create pageable
    Pageable pageable = PaginationHelper.createPageable(pageRequest);
    
    // Get counts
    long totalRecords = productRepository.count();
    Page<Product> page = combinedSpec != null 
            ? productRepository.findAll(combinedSpec, pageable)
            : productRepository.findAll(pageable);
    long filteredRecords = combinedSpec != null 
            ? productRepository.count(combinedSpec)
            : totalRecords;
    
    // Convert to DTOs
    List<ProductDTO> productDTOs = page.getContent().stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    
    return PageResponseDTO.of(
            productDTOs,
            totalRecords,
            filteredRecords,
            pageRequest.getPageNumber(),
            pageRequest.getPageSize()
    );
}
```

### API Endpoint Structure

For each doctype, you'll have:

1. **GET `/api/[entities]/all`** - Get all (no pagination) - for backward compatibility
2. **GET `/api/[entities]`** - Get all with pagination, search, and filtering

Both endpoints return the same data structure, but the paginated endpoint supports:
- Query parameters for pagination
- Search across multiple fields
- Field-specific filters
- Sorting

### Testing Pagination

**Example cURL commands:**

```bash
# Basic pagination
curl "http://localhost:8080/api/users?page=0&size=10"

# With search
curl "http://localhost:8080/api/users?page=0&size=10&search=john"

# With sorting
curl "http://localhost:8080/api/users?page=0&size=10&sortBy=username&sortDir=desc"

# With filters
curl "http://localhost:8080/api/users?page=0&size=10&filters[active]=true"

# Combined
curl "http://localhost:8080/api/users?page=0&size=10&search=admin&sortBy=createdDate&sortDir=desc&filters[active]=true"
```

### Important Notes

1. **Backward Compatibility**: The `/all` endpoint maintains backward compatibility
2. **Default Values**: PageRequestDTO has defaults (page=0, size=10, sortBy="id", sortDir="asc")
3. **Max Page Size**: Page size is capped at 100 records per page
4. **Search**: Case-insensitive, uses LIKE with wildcards
5. **Filters**: Support exact match and pattern matching
6. **Performance**: Use indexes on frequently searched/filtered fields

---

## Checklist

When adding a new doctype, ensure:

- [ ] Entity extends `BaseEntity`
- [ ] Entity uses `@SuperBuilder`
- [ ] Collections excluded from `@EqualsAndHashCode`
- [ ] Repository extends `JpaRepository<Entity, Long>, JpaSpecificationExecutor<Entity>`
- [ ] Repository has `findBy*` and `existsBy*` methods
- [ ] DTO has `@Schema` annotations
- [ ] DTO has `fromEntity()` method
- [ ] CreateRequest has validation annotations
- [ ] UpdateRequest has optional fields
- [ ] Service interface has CRUD methods
- [ ] Service interface has pagination method: `PageResponseDTO<EntityDTO> getAllEntities(PageRequestDTO pageRequest)`
- [ ] Service implementation implements pagination with search and filtering
- [ ] Service implementation uses `AuditingHelper`
- [ ] Service methods have caching annotations (`@Cacheable` for GET, `@CacheEvict` for CREATE/UPDATE/DELETE)
- [ ] Service implementation has proper logging
- [ ] Controller has all CRUD endpoints
- [ ] Controller has paginated GET endpoint: `GET /api/[entities]` with `@ModelAttribute PageRequestDTO`
- [ ] Controller has non-paginated GET endpoint: `GET /api/[entities]/all` for backward compatibility
- [ ] Redis caching is configured and working
- [ ] Cache annotations are properly implemented in service methods
- [ ] Controller has Swagger annotations
- [ ] Controller uses `@Valid` for requests
- [ ] All files compile without errors
- [ ] Swagger UI shows proper documentation

---

## Quick Reference

### File Locations

```
src/main/java/com/shutiye/inventory_system/
├── entity/
│   └── [EntityName].java
├── repository/
│   └── [EntityName]Repository.java
└── modules/auth/
    ├── dtos/
    │   ├── [EntityName]DTO.java
    │   ├── [EntityName]CreateRequest.java
    │   └── [EntityName]UpdateRequest.java
    ├── services/
    │   ├── [EntityName]Service.java
    │   └── impl/
    │       └── [EntityName]ServiceImpl.java
    └── controllers/
        └── [EntityName]Controller.java
```

### Naming Conventions

- **Entity:** PascalCase (e.g., `Product`, `OrderItem`)
- **Repository:** `[EntityName]Repository` (e.g., `ProductRepository`)
- **DTO:** `[EntityName]DTO` (e.g., `ProductDTO`)
- **Service:** `[EntityName]Service` (e.g., `ProductService`)
- **Controller:** `[EntityName]Controller` (e.g., `ProductController`)
- **Table Name:** snake_case, plural (e.g., `products`, `order_items`)
- **API Endpoint:** `/api/[entities]` (e.g., `/api/products`)

---

## Support

For questions or issues, refer to existing implementations:
- `User` - Complex entity with relationships
- `Permission` - Simple entity example
- `Role` - Entity with multiple relationships
- `Menu` - Entity with self-referencing relationship

---

**Last Updated:** 2025-11-22
**Version:** 3.0 (Added Redis Caching)

---

## Changelog

### Version 3.0 (2025-11-22)
- ✅ Added Redis caching implementation
- ✅ Comprehensive caching section in guide
- ✅ Cache annotations on all service methods
- ✅ Cache eviction on create/update/delete operations
- ✅ Performance optimization with ~95% faster cached requests
- ✅ Updated checklist to include caching requirements

### Version 2.0 (2025-11-22)
- ✅ Added pagination, search, and filtering support
- ✅ Created `PageRequestDTO` and `PageResponseDTO` for standardized pagination
- ✅ Created `PaginationHelper` and `SpecificationHelper` utilities
- ✅ Updated all repositories to support `JpaSpecificationExecutor`
- ✅ Updated all service interfaces and implementations with pagination methods
- ✅ Updated all controllers with paginated endpoints

### Version 1.0 (2025-11-22)
- ✅ Initial guide creation
- ✅ Complete CRUD implementation pattern
- ✅ BaseEntity pattern
- ✅ DTO pattern with Swagger annotations

