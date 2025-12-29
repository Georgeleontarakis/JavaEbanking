package com.bankoftuc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a personal bank account owned by an individual customer.
 * Supports multiple co-owners (secondary owners).
 */
public class PersonalAccount extends Account {
    private static final long serialVersionUID = 1L;
    
    private IndividualUser primaryOwner;
    private List<IndividualUser> secondaryOwners;
    
    public PersonalAccount(String iban, BigDecimal balance, IndividualUser primaryOwner) {
        super(iban, balance);
        this.primaryOwner = primaryOwner;
        this.secondaryOwners = new ArrayList<>();
    }
    
    @Override
    public String getAccountType() {
        return "PERSONAL";
    }
    
    @Override
    public Customer getPrimaryOwner() {
        return primaryOwner;
    }
    
    /**
     * Add a secondary owner (co-owner) to the account
     */
    public void addSecondaryOwner(IndividualUser owner) {
        if (!secondaryOwners.contains(owner) && !owner.equals(primaryOwner)) {
            secondaryOwners.add(owner);
        }
    }
    
    /**
     * Remove a secondary owner from the account
     */
    public void removeSecondaryOwner(IndividualUser owner) {
        secondaryOwners.remove(owner);
    }
    
    /**
     * Check if a user is an owner (primary or secondary) of this account
     */
    public boolean isOwner(User user) {
        if (primaryOwner.getId().equals(user.getId())) {
            return true;
        }
        for (IndividualUser owner : secondaryOwners) {
            if (owner.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a user is the primary owner
     */
    public boolean isPrimaryOwner(User user) {
        return primaryOwner.getId().equals(user.getId());
    }
    
    public void setPrimaryOwner(IndividualUser primaryOwner) {
        this.primaryOwner = primaryOwner;
    }
    
    public List<IndividualUser> getSecondaryOwners() {
        return new ArrayList<>(secondaryOwners);
    }
    
    public List<IndividualUser> getAllOwners() {
        List<IndividualUser> allOwners = new ArrayList<>();
        allOwners.add(primaryOwner);
        allOwners.addAll(secondaryOwners);
        return allOwners;
    }
    
    @Override
    public String toString() {
        return String.format("PersonalAccount[IBAN=%s, balance=%.2f€, owner=%s, coOwners=%d]", 
            iban, balance, primaryOwner.getFullName(), secondaryOwners.size());
    }
}
