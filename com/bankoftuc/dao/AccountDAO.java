package com.bankoftuc.dao;

import com.bankoftuc.model.Account;
import com.bankoftuc.model.User;
import java.util.List;
import java.util.Optional;

/**
 * DAO Pattern - Account Data Access Object Interface.
 * Defines operations specific to Account entities.
 */
public interface AccountDAO extends GenericDAO<Account, String> {
    
    /**
     * Find account by IBAN
     * @param iban IBAN to search
     * @return Optional containing account if found
     */
    Optional<Account> findByIban(String iban);
    
    /**
     * Find all accounts for a user
     * @param owner Account owner
     * @return List of accounts owned by user
     */
    List<Account> findByOwner(User owner);
    
    /**
     * Find all accounts for a username
     * @param username Owner's username
     * @return List of accounts owned by username
     */
    List<Account> findByOwnerUsername(String username);
    
    /**
     * Find all personal accounts
     * @return List of personal accounts
     */
    List<Account> findAllPersonal();
    
    /**
     * Find all business accounts
     * @return List of business accounts
     */
    List<Account> findAllBusiness();
    
    /**
     * Find all active accounts
     * @return List of active (non-closed) accounts
     */
    List<Account> findAllActive();
    
    /**
     * Check if IBAN exists
     * @param iban IBAN to check
     * @return true if IBAN exists
     */
    boolean ibanExists(String iban);
}
