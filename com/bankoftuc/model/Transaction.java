package com.bankoftuc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a bank transaction (deposit, withdrawal, transfer, payment, etc.)
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT,
        BILL_PAYMENT,
        INTEREST,
        MAINTENANCE_FEE,
        SEPA_TRANSFER,
        SWIFT_TRANSFER
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    private long id;
    private LocalDateTime dateTime;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private BigDecimal balanceAfter;
    private Account fromAccount;
    private Account toAccount;
    private TransactionStatus status;
    
    public Transaction(long id, Account fromAccount, Account toAccount, 
                       BigDecimal amount, TransactionType type, String description) {
        this.id = id;
        this.dateTime = LocalDateTime.now();
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.status = TransactionStatus.COMPLETED;
        
        // Record balance after transaction for the relevant account
        if (fromAccount != null) {
            this.balanceAfter = fromAccount.getBalance();
        } else if (toAccount != null) {
            this.balanceAfter = toAccount.getBalance();
        }
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    public Account getFromAccount() { return fromAccount; }
    public void setFromAccount(Account fromAccount) { this.fromAccount = fromAccount; }
    
    public Account getToAccount() { return toAccount; }
    public void setToAccount(Account toAccount) { this.toAccount = toAccount; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sign = (type == TransactionType.DEPOSIT || type == TransactionType.TRANSFER_IN || 
                       type == TransactionType.INTEREST) ? "+" : "-";
        return String.format("[%s] %s %s%.2f€ - %s (Balance: %.2f€)", 
            dateTime.format(formatter), type, sign, amount, description, balanceAfter);
    }
    
    public String toShortString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String sign = (type == TransactionType.DEPOSIT || type == TransactionType.TRANSFER_IN || 
                       type == TransactionType.INTEREST) ? "+" : "-";
        return String.format("%s | %s%8.2f€ | %s", 
            dateTime.format(formatter), sign, amount, description);
    }
}
