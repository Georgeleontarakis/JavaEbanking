package com.bankoftuc.manager;

import com.bankoftuc.model.User;
import java.util.List;

/**
 * Manages user authentication (login/logout).
 */
public class AuthManager {
    
    private User currentUser;
    private List<User> users;
    
    public AuthManager(List<User> users) {
        this.users = users;
        this.currentUser = null;
    }
    
    /**
     * Attempt to log in with username and password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.isLocked()) {
                    System.out.println("Account is locked. Please contact an administrator.");
                    return false;
                }
                if (user.verifyPassword(password)) {
                    user.resetFailedAttempts();
                    currentUser = user;
                    return true;
                } else {
                    user.recordFailedLogin();
                    if (user.isLocked()) {
                        System.out.println("Too many failed attempts. Account has been locked.");
                    }
                    return false;
                }
            }
        }
        return false;
    }
    
    /**
     * Log out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Get the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if anyone is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Update the users list reference
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
