package com.bankoftuc.model;

/**
 * Represents a business customer (company/organization) in the banking system.
 */
public class BusinessUser extends Customer {
    private static final long serialVersionUID = 1L;
    
    private String businessName;
    
    public BusinessUser(String id, String username, String password, 
                        String businessName, String phoneNumber, String vatNumber) {
        super(id, username, password, phoneNumber, vatNumber);
        this.businessName = businessName;
    }
    
    @Override
    public String getRole() {
        return "BUSINESS";
    }
    
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    
    @Override
    public String toString() {
        return String.format("BusinessUser[id=%s, username=%s, businessName=%s]", 
            id, username, businessName);
    }
}
