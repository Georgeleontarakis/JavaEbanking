package com.bankoftuc.model;

/**
 * Represents an individual customer (physical person) in the banking system.
 */
public class IndividualUser extends Customer {
    private static final long serialVersionUID = 1L;
    
    private String fullName;
    private String address;
    
    public IndividualUser(String id, String username, String password, 
                          String fullName, String address, String phoneNumber, String vatNumber) {
        super(id, username, password, phoneNumber, vatNumber);
        this.fullName = fullName;
        this.address = address;
    }
    
    @Override
    public String getRole() {
        return "INDIVIDUAL";
    }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    @Override
    public String toString() {
        return String.format("IndividualUser[id=%s, username=%s, fullName=%s]", 
            id, username, fullName);
    }
}
