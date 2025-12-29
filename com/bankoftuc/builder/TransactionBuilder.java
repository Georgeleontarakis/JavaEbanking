package com.bankoftuc.builder;

import com.bankoftuc.model.*;
import com.bankoftuc.model.Transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Builder Pattern - Builds Transaction objects step by step.
 * Allows flexible construction of complex Transaction objects.
 */
public class TransactionBuilder {
    
    private long id;
    private LocalDateTime dateTime;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private BigDecimal balanceAfter;
    private Account fromAccount;
    private Account toAccount;
    private Transaction.TransactionStatus status;
    
    public TransactionBuilder() {
        // Set defaults
        this.dateTime = LocalDateTime.now();
        this.status = Transaction.TransactionStatus.COMPLETED;
        this.description = "";
    }
    
    /**
     * Set transaction ID
     */
    public TransactionBuilder withId(long id) {
        this.id = id;
        return this;
    }
    
    /**
     * Set transaction date/time
     */
    public TransactionBuilder withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }
    
    /**
     * Set transaction amount
     */
    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    /**
     * Set transaction amount from double
     */
    public TransactionBuilder withAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
        return this;
    }
    
    /**
     * Set transaction type
     */
    public TransactionBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }
    
    /**
     * Set description
     */
    public TransactionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * Set balance after transaction
     */
    public TransactionBuilder withBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
        return this;
    }
    
    /**
     * Set source account
     */
    public TransactionBuilder fromAccount(Account account) {
        this.fromAccount = account;
        return this;
    }
    
    /**
     * Set destination account
     */
    public TransactionBuilder toAccount(Account account) {
        this.toAccount = account;
        return this;
    }
    
    /**
     * Set transaction status
     */
    public TransactionBuilder withStatus(Transaction.TransactionStatus status) {
        this.status = status;
        return this;
    }
    
    /**
     * Configure as deposit transaction
     */
    public TransactionBuilder asDeposit(Account account, BigDecimal amount) {
        this.type = TransactionType.DEPOSIT;
        this.toAccount = account;
        this.fromAccount = null;
        this.amount = amount;
        this.description = "Cash deposit";
        return this;
    }
    
    /**
     * Configure as withdrawal transaction
     */
    public TransactionBuilder asWithdrawal(Account account, BigDecimal amount) {
        this.type = TransactionType.WITHDRAWAL;
        this.fromAccount = account;
        this.toAccount = null;
        this.amount = amount;
        this.description = "Cash withdrawal";
        return this;
    }
    
    /**
     * Configure as transfer transaction
     */
    public TransactionBuilder asTransfer(Account from, Account to, BigDecimal amount) {
        this.type = TransactionType.TRANSFER_OUT;
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.description = "Transfer to " + to.getIban();
        return this;
    }
    
    /**
     * Configure as SEPA transfer
     */
    public TransactionBuilder asSepaTransfer(Account from, String toIban, BigDecimal amount, String apiTxId) {
        this.type = TransactionType.SEPA_TRANSFER;
        this.fromAccount = from;
        this.toAccount = null;
        this.amount = amount;
        this.description = String.format("SEPA transfer to %s [API TxID: %s]", toIban, apiTxId);
        return this;
    }
    
    /**
     * Configure as SWIFT transfer
     */
    public TransactionBuilder asSwiftTransfer(Account from, String toAccount, BigDecimal amount, String apiTxId) {
        this.type = TransactionType.SWIFT_TRANSFER;
        this.fromAccount = from;
        this.toAccount = null;
        this.amount = amount;
        this.description = String.format("SWIFT transfer to %s [API TxID: %s]", toAccount, apiTxId);
        return this;
    }
    
    /**
     * Configure as bill payment
     */
    public TransactionBuilder asBillPayment(Account from, String rfCode, BigDecimal amount) {
        this.type = TransactionType.BILL_PAYMENT;
        this.fromAccount = from;
        this.toAccount = null;
        this.amount = amount;
        this.description = "Bill payment RF: " + rfCode;
        return this;
    }
    
    /**
     * Build the Transaction object
     */
    public Transaction build() {
        validate();
        
        Transaction transaction = new Transaction(
            id, fromAccount, toAccount, amount, type, description
        );
        transaction.setBalanceAfter(balanceAfter);
        transaction.setStatus(status);
        
        return transaction;
    }
    
    /**
     * Validate required fields
     */
    private void validate() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Transaction amount must be positive");
        }
        if (type == null) {
            throw new IllegalStateException("Transaction type is required");
        }
    }
    
    /**
     * Reset builder for reuse
     */
    public TransactionBuilder reset() {
        this.id = 0;
        this.dateTime = LocalDateTime.now();
        this.amount = null;
        this.type = null;
        this.description = "";
        this.balanceAfter = null;
        this.fromAccount = null;
        this.toAccount = null;
        this.status = Transaction.TransactionStatus.COMPLETED;
        return this;
    }
}
