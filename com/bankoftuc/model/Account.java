package com.bankoftuc.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Abstract base class for bank accounts.
 * Supports both Personal and Business accounts.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, FROZEN, CLOSED
    }
    
    protected String iban;
    protected BigDecimal balance;
    protected AccountStatus status;
    protected BigDecimal interestRate;
    protected BigDecimal accruedInterest;
    
    public Account(String iban, BigDecimal balance) {
        this.iban = iban;
        this.balance = balance;
        this.status = AccountStatus.ACTIVE;
        this.interestRate = new BigDecimal("0.01"); // 1% default interest rate
        this.accruedInterest = BigDecimal.ZERO;
    }
    
    /**
     * Deposit money into the account
     */
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        balance = balance.add(amount);
    }
    
    /**
     * Withdraw money from the account
     */
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }
    
    /**
     * Calculate daily interest
     */
    public void accrueInterest() {
        BigDecimal dailyRate = interestRate.divide(new BigDecimal("365"), 10, java.math.RoundingMode.HALF_UP);
        BigDecimal interest = balance.multiply(dailyRate);
        accruedInterest = accruedInterest.add(interest);
    }
    
    /**
     * Apply monthly interest to balance
     */
    public BigDecimal applyMonthlyInterest() {
        BigDecimal interestToApply = accruedInterest;
        balance = balance.add(accruedInterest);
        accruedInterest = BigDecimal.ZERO;
        return interestToApply;
    }
    
    /**
     * Get account type description
     */
    public abstract String getAccountType();
    
    /**
     * Get the owner of the account
     */
    public abstract Customer getPrimaryOwner();
    
    // Getters and Setters
    public String getIban() { return iban; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public BigDecimal getAccruedInterest() { return accruedInterest; }
    
    @Override
    public String toString() {
        return String.format("Account[IBAN=%s, balance=%.2f€, status=%s]", 
            iban, balance, status);
    }
}
