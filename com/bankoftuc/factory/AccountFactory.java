package com.bankoftuc.factory;

import com.bankoftuc.model.*;
import java.math.BigDecimal;

/**
 * Factory Pattern - Creates different types of accounts.
 * Centralizes account creation logic with proper IBAN generation.
 */
public class AccountFactory {
    
    private static long personalAccountCounter = 1;
    private static long businessAccountCounter = 1;
    
    // IBAN prefix for Bank of TUC (Greece)
    private static final String COUNTRY_CODE = "GR";
    private static final String PERSONAL_TYPE_CODE = "100";  // Personal accounts
    private static final String BUSINESS_TYPE_CODE = "200";  // Business accounts
    
    /**
     * Account type enumeration
     */
    public enum AccountType {
        PERSONAL,
        BUSINESS
    }
    
    /**
     * Create an account based on type
     */
    public static Account createAccount(AccountType type, User owner) {
        switch (type) {
            case PERSONAL:
                if (!(owner instanceof IndividualUser)) {
                    throw new IllegalArgumentException("Personal accounts require IndividualUser");
                }
                return createPersonalAccount((IndividualUser) owner);
            case BUSINESS:
                if (!(owner instanceof BusinessUser)) {
                    throw new IllegalArgumentException("Business accounts require BusinessUser");
                }
                return createBusinessAccount((BusinessUser) owner);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
    
    /**
     * Create a Personal Account
     */
    public static PersonalAccount createPersonalAccount(IndividualUser owner) {
        String iban = generatePersonalIban();
        PersonalAccount account = new PersonalAccount(iban, BigDecimal.ZERO, owner);
        account.setInterestRate(new BigDecimal("0.01")); // 1% default interest
        return account;
    }
    
    /**
     * Create a Personal Account with initial balance
     */
    public static PersonalAccount createPersonalAccount(IndividualUser owner, BigDecimal initialBalance) {
        String iban = generatePersonalIban();
        PersonalAccount account = new PersonalAccount(iban, initialBalance, owner);
        account.setInterestRate(new BigDecimal("0.01")); // 1% default interest
        return account;
    }
    
    /**
     * Create a Business Account
     */
    public static BusinessAccount createBusinessAccount(BusinessUser owner) {
        String iban = generateBusinessIban();
        BusinessAccount account = new BusinessAccount(iban, BigDecimal.ZERO, owner);
        account.setInterestRate(new BigDecimal("0.005")); // 0.5% default interest for business
        account.setMonthlyMaintenanceFee(new BigDecimal("25.00")); // €25 monthly fee
        return account;
    }
    
    /**
     * Create a Business Account with initial balance
     */
    public static BusinessAccount createBusinessAccount(BusinessUser owner, BigDecimal initialBalance) {
        String iban = generateBusinessIban();
        BusinessAccount account = new BusinessAccount(iban, initialBalance, owner);
        account.setInterestRate(new BigDecimal("0.005")); // 0.5% default interest for business
        account.setMonthlyMaintenanceFee(new BigDecimal("25.00")); // €25 monthly fee
        return account;
    }
    
    /**
     * Generate IBAN for personal account
     * Format: GR + 100 (type) + 15-digit number
     */
    private static String generatePersonalIban() {
        String accountNumber = String.format("%015d", personalAccountCounter++);
        return COUNTRY_CODE + PERSONAL_TYPE_CODE + accountNumber;
    }
    
    /**
     * Generate IBAN for business account
     * Format: GR + 200 (type) + 15-digit number
     */
    private static String generateBusinessIban() {
        String accountNumber = String.format("%015d", businessAccountCounter++);
        return COUNTRY_CODE + BUSINESS_TYPE_CODE + accountNumber;
    }
    
    /**
     * Set counters (used when loading from persistence)
     */
    public static void setCounters(long personal, long business) {
        personalAccountCounter = personal;
        businessAccountCounter = business;
    }
    
    /**
     * Parse IBAN to determine account type
     */
    public static AccountType getAccountTypeFromIban(String iban) {
        if (iban == null || iban.length() < 5) {
            throw new IllegalArgumentException("Invalid IBAN format");
        }
        String typeCode = iban.substring(2, 5);
        if (PERSONAL_TYPE_CODE.equals(typeCode)) {
            return AccountType.PERSONAL;
        } else if (BUSINESS_TYPE_CODE.equals(typeCode)) {
            return AccountType.BUSINESS;
        }
        throw new IllegalArgumentException("Unknown account type code: " + typeCode);
    }
    
    /**
     * Validate IBAN format
     */
    public static boolean isValidIban(String iban) {
        if (iban == null || iban.length() != 20) {
            return false;
        }
        if (!iban.startsWith(COUNTRY_CODE)) {
            return false;
        }
        String typeCode = iban.substring(2, 5);
        return PERSONAL_TYPE_CODE.equals(typeCode) || BUSINESS_TYPE_CODE.equals(typeCode);
    }
}
