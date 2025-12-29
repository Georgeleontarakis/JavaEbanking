package com.bankoftuc.dao;

import com.bankoftuc.model.User;
import java.util.Optional;
import java.util.List;

/**
 * DAO Pattern - User Data Access Object Interface.
 * Defines operations specific to User entities.
 */
public interface UserDAO extends GenericDAO<User, String> {
    
    /**
     * Find user by username
     * @param username Username to search
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find users by type (INDIVIDUAL, BUSINESS, ADMIN)
     * @param userType Type of user
     * @return List of users of given type
     */
    List<User> findByType(String userType);
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    boolean usernameExists(String username);
    
    /**
     * Find all active (non-locked) users
     * @return List of active users
     */
    List<User> findAllActive();
    
    /**
     * Find all locked users
     * @return List of locked users
     */
    List<User> findAllLocked();
}
