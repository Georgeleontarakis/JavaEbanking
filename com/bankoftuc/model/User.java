package com.bankoftuc.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Abstract base class for all users in the Bank of TUC system.
 * Implements common authentication and user management functionality.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String username;
    protected String passwordHash;
    protected String phoneNumber;
    protected int failedLoginAttempts;
    protected boolean locked;
    
    public User(String id, String username, String password, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.phoneNumber = phoneNumber;
        this.failedLoginAttempts = 0;
        this.locked = false;
    }
    
    /**
     * Hash password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Verify if the provided password matches the stored hash
     */
    public boolean verifyPassword(String password) {
        return passwordHash.equals(hashPassword(password));
    }
    
    /**
     * Change user's password
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (verifyPassword(oldPassword)) {
            this.passwordHash = hashPassword(newPassword);
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }
    
    /**
     * Record a failed login attempt
     */
    public void recordFailedLogin() {
        failedLoginAttempts++;
        if (failedLoginAttempts >= 5) {
            locked = true;
        }
    }
    
    /**
     * Reset failed login attempts after successful login
     */
    public void resetFailedAttempts() {
        failedLoginAttempts = 0;
    }
    
    /**
     * Get user role type
     */
    public abstract String getRole();
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    
    @Override
    public String toString() {
        return String.format("User[id=%s, username=%s, role=%s, locked=%s]", 
            id, username, getRole(), locked);
    }
}
