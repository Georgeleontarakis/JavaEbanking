package com.bankoftuc.model;

import java.math.BigDecimal;

/**
 * Represents a business bank account owned by a business customer.
 * Includes monthly maintenance fee functionality.
 */
public class BusinessAccount extends Account {
    private static final long serialVersionUID = 1L;
    
    private BusinessUser owner;
    private BigDecimal monthlyMaintenanceFee;
    
    public BusinessAccount(String iban, BigDecimal balance, BusinessUser owner, 
                           BigDecimal monthlyMaintenanceFee) {
        super(iban, balance);
        this.owner = owner;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }
    
    public BusinessAccount(String iban, BigDecimal balance, BusinessUser owner) {
        this(iban, balance, owner, new BigDecimal("25.00")); // Default €25 monthly fee
    }
    
    @Override
    public String getAccountType() {
        return "BUSINESS";
    }
    
    @Override
    public Customer getPrimaryOwner() {
        return owner;
    }
    
    /**
     * Apply monthly maintenance fee to the account
     * @return the fee amount that was charged
     */
    public BigDecimal applyMaintenanceFee() {
        if (status == AccountStatus.ACTIVE) {
            if (balance.compareTo(monthlyMaintenanceFee) >= 0) {
                balance = balance.subtract(monthlyMaintenanceFee);
                return monthlyMaintenanceFee;
            } else {
                // If not enough balance, take what's available
                BigDecimal charged = balance;
                balance = BigDecimal.ZERO;
                return charged;
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Check if a user is the owner of this account
     */
    public boolean isOwner(User user) {
        return owner.getId().equals(user.getId());
    }
    
    public BusinessUser getOwner() { return owner; }
    public void setOwner(BusinessUser owner) { this.owner = owner; }
    
    public BigDecimal getMonthlyMaintenanceFee() { return monthlyMaintenanceFee; }
    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) { 
        this.monthlyMaintenanceFee = monthlyMaintenanceFee; 
    }
    
    @Override
    public String toString() {
        return String.format("BusinessAccount[IBAN=%s, balance=%.2f€, business=%s, fee=%.2f€]", 
            iban, balance, owner.getBusinessName(), monthlyMaintenanceFee);
    }
}
