package com.bankoftuc.command;

import com.bankoftuc.model.Account;
import com.bankoftuc.model.Transaction;
import com.bankoftuc.manager.TransactionManager;
import java.math.BigDecimal;

/**
 * Command Pattern - Transfer Command.
 * Encapsulates an internal transfer operation.
 */
public class TransferCommand implements Command {
    
    private Account fromAccount;
    private Account toAccount;
    private BigDecimal amount;
    private String description;
    private TransactionManager transactionManager;
    private Transaction executedTransaction;
    private boolean executed;
    
    public TransferCommand(Account fromAccount, Account toAccount, BigDecimal amount,
                           String description, TransactionManager transactionManager) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.description = description;
        this.transactionManager = transactionManager;
        this.executed = false;
    }
    
    @Override
    public boolean execute() {
        if (executed) {
            return false; // Already executed
        }
        
        // Check sufficient funds
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            System.err.println("Insufficient funds for transfer");
            return false;
        }
        
        try {
            executedTransaction = transactionManager.transfer(fromAccount, toAccount, amount, description);
            executed = true;
            return true;
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed || executedTransaction == null) {
            return false; // Nothing to undo
        }
        
        // Check if toAccount has sufficient funds to reverse
        if (toAccount.getBalance().compareTo(amount) < 0) {
            System.err.println("Cannot undo transfer: destination account has insufficient funds");
            return false;
        }
        
        try {
            // Reverse the transfer
            toAccount.withdraw(amount);
            fromAccount.deposit(amount);
            executed = false;
            return true;
        } catch (Exception e) {
            System.err.println("Undo transfer failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Transfer %.2f EUR from %s to %s", 
            amount, fromAccount.getIban(), toAccount.getIban());
    }
    
    @Override
    public boolean isUndoable() {
        return executed && toAccount.getBalance().compareTo(amount) >= 0;
    }
    
    public Transaction getExecutedTransaction() {
        return executedTransaction;
    }
}
