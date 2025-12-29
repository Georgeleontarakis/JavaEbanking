package com.bankoftuc.factory;

import com.bankoftuc.model.*;

/**
 * Factory Pattern - Creates different types of users.
 * Centralizes user creation logic and ensures consistent initialization.
 */
public class UserFactory {
    
    private static long individualCounter = 1;
    private static long businessCounter = 1;
    private static long adminCounter = 1;
    
    /**
     * User type enumeration
     */
    public enum UserType {
        INDIVIDUAL,
        BUSINESS,
        ADMIN
    }
    
    /**
     * Create a user based on type
     */
    public static User createUser(UserType type, String username, String password, String phoneNumber) {
        switch (type) {
            case INDIVIDUAL:
                return createIndividualUser(username, password, phoneNumber, "", "", "");
            case BUSINESS:
                return createBusinessUser(username, password, phoneNumber, "", "");
            case ADMIN:
                return createAdminUser(username, password, phoneNumber, 1);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
    
    /**
     * Create an Individual User
     */
    public static IndividualUser createIndividualUser(String username, String password, 
            String phoneNumber, String fullName, String address, String vatNumber) {
        String id = String.format("IND%06d", individualCounter++);
        IndividualUser user = new IndividualUser(id, username, password, fullName, address, phoneNumber, vatNumber);
        return user;
    }
    
    /**
     * Create a Business User
     */
    public static BusinessUser createBusinessUser(String username, String password,
            String phoneNumber, String businessName, String vatNumber) {
        String id = String.format("BUS%06d", businessCounter++);
        BusinessUser user = new BusinessUser(id, username, password, businessName, phoneNumber, vatNumber);
        return user;
    }
    
    /**
     * Create an Admin User
     */
    public static AdminUser createAdminUser(String username, String password,
            String phoneNumber, int adminLevel) {
        String id = String.format("ADM%06d", adminCounter++);
        AdminUser user = new AdminUser(id, username, password, phoneNumber, adminLevel);
        return user;
    }
    
    /**
     * Set counters (used when loading from persistence)
     */
    public static void setCounters(long individual, long business, long admin) {
        individualCounter = individual;
        businessCounter = business;
        adminCounter = admin;
    }
    
    /**
     * Get next individual ID without incrementing
     */
    public static String peekNextIndividualId() {
        return String.format("IND%06d", individualCounter);
    }
    
    /**
     * Get next business ID without incrementing
     */
    public static String peekNextBusinessId() {
        return String.format("BUS%06d", businessCounter);
    }
    
    /**
     * Get next admin ID without incrementing
     */
    public static String peekNextAdminId() {
        return String.format("ADM%06d", adminCounter);
    }
}
