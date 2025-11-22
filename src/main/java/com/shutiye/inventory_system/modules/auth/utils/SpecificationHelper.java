package com.shutiye.inventory_system.modules.auth.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building JPA Specifications for dynamic queries.
 * Supports search across multiple fields and field-specific filtering.
 */
public class SpecificationHelper {
    
    /**
     * Build a specification for searching across multiple fields.
     * Uses LIKE query with case-insensitive matching.
     * 
     * @param searchTerm Search term to look for
     * @param searchableFields Array of field names to search in
     * @return Specification for the search
     */
    public static <T> Specification<T> buildSearchSpecification(String searchTerm, String... searchableFields) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || searchableFields.length == 0) {
            return null;
        }
        
        String searchPattern = "%" + searchTerm.toLowerCase().trim() + "%";
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            for (String field : searchableFields) {
                try {
                    // Handle nested fields (e.g., "user.name")
                    String[] fieldParts = field.split("\\.");
                    jakarta.persistence.criteria.Path<?> path = root.get(fieldParts[0]);
                    
                    for (int i = 1; i < fieldParts.length; i++) {
                        path = path.get(fieldParts[i]);
                    }
                    
                    // Create LIKE predicate (case-insensitive)
                    Predicate likePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(path.as(String.class)),
                        searchPattern
                    );
                    predicates.add(likePredicate);
                } catch (Exception e) {
                    // Skip fields that don't exist or can't be searched
                    // Log warning in production
                }
            }
            
            // Combine with OR (search term can match any field)
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Build a specification for field-specific filters.
     * Uses exact match or LIKE depending on field type.
     * 
     * @param filters Map of field names to filter values
     * @return Specification for the filters
     */
    public static <T> Specification<T> buildFilterSpecification(Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String fieldName = entry.getKey();
                String filterValue = entry.getValue();
                
                if (filterValue == null || filterValue.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Handle nested fields
                    String[] fieldParts = fieldName.split("\\.");
                    jakarta.persistence.criteria.Path<?> path = root.get(fieldParts[0]);
                    
                    for (int i = 1; i < fieldParts.length; i++) {
                        path = path.get(fieldParts[i]);
                    }
                    
                    // Determine filter type based on field type
                    Class<?> fieldType = path.getJavaType();
                    
                    if (fieldType == Boolean.class || fieldType == boolean.class) {
                        // Boolean filter
                        Boolean boolValue = Boolean.parseBoolean(filterValue);
                        predicates.add(criteriaBuilder.equal(path, boolValue));
                    } else if (fieldType == Long.class || fieldType == long.class || 
                               fieldType == Integer.class || fieldType == int.class) {
                        // Numeric filter
                        try {
                            if (fieldType == Long.class || fieldType == long.class) {
                                Long longValue = Long.parseLong(filterValue);
                                predicates.add(criteriaBuilder.equal(path, longValue));
                            } else {
                                Integer intValue = Integer.parseInt(filterValue);
                                predicates.add(criteriaBuilder.equal(path, intValue));
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid numeric values
                        }
                    } else {
                        // String filter (exact match or LIKE)
                        if (filterValue.contains("%") || filterValue.contains("*")) {
                            // Pattern-based filter
                            String pattern = filterValue.replace("*", "%");
                            predicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(path.as(String.class)),
                                pattern.toLowerCase()
                            ));
                        } else {
                            // Exact match (case-insensitive)
                            predicates.add(criteriaBuilder.equal(
                                criteriaBuilder.lower(path.as(String.class)),
                                filterValue.toLowerCase()
                            ));
                        }
                    }
                } catch (Exception e) {
                    // Skip fields that don't exist
                }
            }
            
            // Combine with AND (all filters must match)
            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Combine multiple specifications with AND logic.
     * 
     * @param specs Specifications to combine
     * @return Combined specification
     */
    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        Specification<T> result = null;
        for (Specification<T> spec : specs) {
            if (spec != null) {
                result = result == null ? spec : result.and(spec);
            }
        }
        return result;
    }
}


