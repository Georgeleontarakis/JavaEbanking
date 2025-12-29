package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages user creation, retrieval, and updates.
 */
public class UserManager {
    
    private List<User> users;
    private AtomicInteger userIdCounter;
    
    public UserManager() {
        this.users = new ArrayList<>();
        this.userIdCounter = new AtomicInteger(1);
    }
    
    public UserManager(List<User> users) {
        this.users = users;
        // Find the highest ID to continue from there
        int maxId = users.stream()
            .mapToInt(u -> {
                try {
                    return Integer.parseInt(u.getId().replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);
        this.userIdCounter = new AtomicInteger(maxId + 1);
    }
    
    /**
     * Register a new individual user
     */
    public IndividualUser registerIndividualUser(String username, String password, 
                                                  String fullName, String address, 
                                                  String phoneNumber, String vatNumber) {
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        String id = "IND" + String.format("%06d", userIdCounter.getAndIncrement());
        IndividualUser user = new IndividualUser(id, username, password, fullName, 
                                                  address, phoneNumber, vatNumber);
        users.add(user);
        return user;
    }
    
    /**
     * Register a new business user
     */
    public BusinessUser registerBusinessUser(String username, String password, 
                                              String businessName, String phoneNumber, 
                                              String vatNumber) {
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        String id = "BUS" + String.format("%06d", userIdCounter.getAndIncrement());
        BusinessUser user = new BusinessUser(id, username, password, businessName, 
                                              phoneNumber, vatNumber);
        users.add(user);
        return user;
    }
    
    /**
     * Register a new admin user
     */
    public AdminUser registerAdminUser(String username, String password, 
                                        String phoneNumber, int adminLevel) {
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        String id = "ADM" + String.format("%06d", userIdCounter.getAndIncrement());
        AdminUser user = new AdminUser(id, username, password, phoneNumber, adminLevel);
        users.add(user);
        return user;
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Find user by ID
     */
    public User findById(String id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Lock a user's account
     */
    public void lockUser(User user) {
        user.setLocked(true);
    }
    
    /**
     * Unlock a user's account
     */
    public void unlockUser(User user) {
        user.setLocked(false);
        user.resetFailedAttempts();
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Get all individual users
     */
    public List<IndividualUser> getAllIndividualUsers() {
        List<IndividualUser> result = new ArrayList<>();
        for (User user : users) {
            if (user instanceof IndividualUser) {
                result.add((IndividualUser) user);
            }
        }
        return result;
    }
    
    /**
     * Get all business users
     */
    public List<BusinessUser> getAllBusinessUsers() {
        List<BusinessUser> result = new ArrayList<>();
        for (User user : users) {
            if (user instanceof BusinessUser) {
                result.add((BusinessUser) user);
            }
        }
        return result;
    }
    
    /**
     * Get all admin users
     */
    public List<AdminUser> getAllAdminUsers() {
        List<AdminUser> result = new ArrayList<>();
        for (User user : users) {
            if (user instanceof AdminUser) {
                result.add((AdminUser) user);
            }
        }
        return result;
    }
    
    /**
     * Get the users list reference
     */
    public List<User> getUsers() {
        return users;
    }
}
