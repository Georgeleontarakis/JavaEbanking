package com.bankoftuc.model;

/**
 * Represents an administrator user in the banking system.
 */
public class AdminUser extends User {
    private static final long serialVersionUID = 1L;
    
    private int adminLevel;
    
    public AdminUser(String id, String username, String password, 
                     String phoneNumber, int adminLevel) {
        super(id, username, password, phoneNumber);
        this.adminLevel = adminLevel;
    }
    
    @Override
    public String getRole() {
        return "ADMIN";
    }
    
    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }
    
    @Override
    public String toString() {
        return String.format("AdminUser[id=%s, username=%s, level=%d]", 
            id, username, adminLevel);
    }
}
