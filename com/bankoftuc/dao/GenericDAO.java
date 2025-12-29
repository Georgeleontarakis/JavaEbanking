package com.bankoftuc.dao;

import java.util.List;
import java.util.Optional;

/**
 * DAO Pattern - Generic Data Access Object Interface.
 * Defines standard CRUD operations for data persistence.
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Save an entity
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);
    
    /**
     * Find entity by ID
     * @param id Entity ID
     * @return Optional containing entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Update an entity
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);
    
    /**
     * Delete an entity
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete entity by ID
     * @param id Entity ID
     */
    void deleteById(ID id);
    
    /**
     * Check if entity exists by ID
     * @param id Entity ID
     * @return true if exists
     */
    boolean existsById(ID id);
    
    /**
     * Count total entities
     * @return Count of entities
     */
    long count();
}
