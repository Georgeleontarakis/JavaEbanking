package com.bankoftuc.command;

import com.bankoftuc.model.Account;
import com.bankoftuc.model.Transaction;
import com.bankoftuc.manager.TransactionManager;
import java.math.BigDecimal;

/**
 * Command Pattern - Deposit Command.
 * Encapsulates a deposit operation.
 */
public class DepositCommand implements Command {
    
    private Account account;
    private BigDecimal amount;
    private String description;
    private TransactionManager transactionManager;
    private Transaction executedTransaction;
    private boolean executed;
    
    public DepositCommand(Account account, BigDecimal amount, String description, 
                          TransactionManager transactionManager) {
        this.account = account;
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
        
        try {
            executedTransaction = transactionManager.deposit(account, amount, description);
            executed = true;
            return true;
        } catch (Exception e) {
            System.err.println("Deposit failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed || executedTransaction == null) {
            return false; // Nothing to undo
        }
        
        try {
            // Reverse the deposit by withdrawing
            account.withdraw(amount);
            executed = false;
            return true;
        } catch (Exception e) {
            System.err.println("Undo deposit failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Deposit %.2f EUR to %s", amount, account.getIban());
    }
    
    @Override
    public boolean isUndoable() {
        return executed && account.getBalance().compareTo(amount) >= 0;
    }
    
    public Transaction getExecutedTransaction() {
        return executedTransaction;
    }
}
