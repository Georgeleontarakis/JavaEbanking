package com.bankoftuc.model;

/**
 * Abstract class representing a customer (either Individual or Business)
 */
public abstract class Customer extends User {
    private static final long serialVersionUID = 1L;
    
    protected String vatNumber;
    
    public Customer(String id, String username, String password, String phoneNumber, String vatNumber) {
        super(id, username, password, phoneNumber);
        this.vatNumber = vatNumber;
    }
    
    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }
}
