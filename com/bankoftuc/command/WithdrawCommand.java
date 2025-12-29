package com.bankoftuc.command;

import com.bankoftuc.model.Account;
import com.bankoftuc.model.Transaction;
import com.bankoftuc.manager.TransactionManager;
import java.math.BigDecimal;

/**
 * Command Pattern - Withdraw Command.
 * Encapsulates a withdrawal operation.
 */
public class WithdrawCommand implements Command {
    
    private Account account;
    private BigDecimal amount;
    private String description;
    private TransactionManager transactionManager;
    private Transaction executedTransaction;
    private boolean executed;
    
    public WithdrawCommand(Account account, BigDecimal amount, String description,
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
        
        // Check sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            System.err.println("Insufficient funds for withdrawal");
            return false;
        }
        
        try {
            executedTransaction = transactionManager.withdraw(account, amount, description);
            executed = true;
            return true;
        } catch (Exception e) {
            System.err.println("Withdrawal failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed || executedTransaction == null) {
            return false; // Nothing to undo
        }
        
        try {
            // Reverse the withdrawal by depositing
            account.deposit(amount);
            executed = false;
            return true;
        } catch (Exception e) {
            System.err.println("Undo withdrawal failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Withdraw %.2f EUR from %s", amount, account.getIban());
    }
    
    @Override
    public boolean isUndoable() {
        return executed;
    }
    
    public Transaction getExecutedTransaction() {
        return executedTransaction;
    }
}
