package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages bank accounts - creation, retrieval, and account operations.
 */
public class AccountManager {
    
    private List<Account> accounts;
    private AtomicLong accountCounter;
    private static final String COUNTRY_CODE = "GR";
    
    public AccountManager() {
        this.accounts = new ArrayList<>();
        this.accountCounter = new AtomicLong(1);
    }
    
    public AccountManager(List<Account> accounts) {
        this.accounts = accounts;
        this.accountCounter = new AtomicLong(accounts.size() + 1);
    }
    
    /**
     * Generate IBAN for a personal account
     * Format: GR + 100 (personal code) + 15 digit account number
     */
    private String generatePersonalIBAN() {
        String accountNumber = String.format("%015d", accountCounter.getAndIncrement());
        return COUNTRY_CODE + "100" + accountNumber;
    }
    
    /**
     * Generate IBAN for a business account
     * Format: GR + 200 (business code) + 15 digit account number
     */
    private String generateBusinessIBAN() {
        String accountNumber = String.format("%015d", accountCounter.getAndIncrement());
        return COUNTRY_CODE + "200" + accountNumber;
    }
    
    /**
     * Create a new personal account
     */
    public PersonalAccount createPersonalAccount(IndividualUser owner, BigDecimal initialBalance) {
        String iban = generatePersonalIBAN();
        PersonalAccount account = new PersonalAccount(iban, initialBalance, owner);
        accounts.add(account);
        return account;
    }
    
    /**
     * Create a new business account
     */
    public BusinessAccount createBusinessAccount(BusinessUser owner, BigDecimal initialBalance,
                                                  BigDecimal monthlyFee) {
        String iban = generateBusinessIBAN();
        BusinessAccount account = new BusinessAccount(iban, initialBalance, owner, monthlyFee);
        accounts.add(account);
        return account;
    }
    
    /**
     * Create a new business account with default fee
     */
    public BusinessAccount createBusinessAccount(BusinessUser owner, BigDecimal initialBalance) {
        return createBusinessAccount(owner, initialBalance, new BigDecimal("25.00"));
    }
    
    /**
     * Find account by IBAN
     */
    public Account findByIban(String iban) {
        return accounts.stream()
            .filter(a -> a.getIban().equals(iban))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all accounts for an individual user
     */
    public List<PersonalAccount> getAccountsForIndividualUser(IndividualUser user) {
        List<PersonalAccount> result = new ArrayList<>();
        for (Account account : accounts) {
            if (account instanceof PersonalAccount) {
                PersonalAccount pa = (PersonalAccount) account;
                if (pa.isOwner(user)) {
                    result.add(pa);
                }
            }
        }
        return result;
    }
    
    /**
     * Get all accounts for a business user
     */
    public List<BusinessAccount> getAccountsForBusinessUser(BusinessUser user) {
        List<BusinessAccount> result = new ArrayList<>();
        for (Account account : accounts) {
            if (account instanceof BusinessAccount) {
                BusinessAccount ba = (BusinessAccount) account;
                if (ba.isOwner(user)) {
                    result.add(ba);
                }
            }
        }
        return result;
    }
    
    /**
     * Get all accounts owned by a customer (works for both individual and business)
     */
    public List<Account> getAccountsForCustomer(Customer customer) {
        List<Account> result = new ArrayList<>();
        if (customer instanceof IndividualUser) {
            result.addAll(getAccountsForIndividualUser((IndividualUser) customer));
        } else if (customer instanceof BusinessUser) {
            result.addAll(getAccountsForBusinessUser((BusinessUser) customer));
        }
        return result;
    }
    
    /**
     * Close an account (set status to CLOSED)
     */
    public void closeAccount(Account account) {
        account.setStatus(Account.AccountStatus.CLOSED);
    }
    
    /**
     * Freeze an account
     */
    public void freezeAccount(Account account) {
        account.setStatus(Account.AccountStatus.FROZEN);
    }
    
    /**
     * Activate an account
     */
    public void activateAccount(Account account) {
        account.setStatus(Account.AccountStatus.ACTIVE);
    }
    
    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }
    
    /**
     * Get all personal accounts
     */
    public List<PersonalAccount> getAllPersonalAccounts() {
        List<PersonalAccount> result = new ArrayList<>();
        for (Account account : accounts) {
            if (account instanceof PersonalAccount) {
                result.add((PersonalAccount) account);
            }
        }
        return result;
    }
    
    /**
     * Get all business accounts
     */
    public List<BusinessAccount> getAllBusinessAccounts() {
        List<BusinessAccount> result = new ArrayList<>();
        for (Account account : accounts) {
            if (account instanceof BusinessAccount) {
                result.add((BusinessAccount) account);
            }
        }
        return result;
    }
    
    /**
     * Get the accounts list reference
     */
    public List<Account> getAccounts() {
        return accounts;
    }
    
    /**
     * Check if user owns the account
     */
    public boolean userOwnsAccount(User user, Account account) {
        if (account instanceof PersonalAccount) {
            return ((PersonalAccount) account).isOwner(user);
        } else if (account instanceof BusinessAccount) {
            return ((BusinessAccount) account).isOwner(user);
        }
        return false;
    }
}
