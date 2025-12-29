package com.bankoftuc.dao;

import com.bankoftuc.model.Transaction;
import com.bankoftuc.model.Account;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO Pattern - Transaction Data Access Object Interface.
 * Defines operations specific to Transaction entities.
 */
public interface TransactionDAO extends GenericDAO<Transaction, Long> {
    
    /**
     * Find all transactions for an account (as source or destination)
     * @param account Account to search
     * @return List of transactions involving this account
     */
    List<Transaction> findByAccount(Account account);
    
    /**
     * Find all transactions from an account
     * @param account Source account
     * @return List of outgoing transactions
     */
    List<Transaction> findByFromAccount(Account account);
    
    /**
     * Find all transactions to an account
     * @param account Destination account
     * @return List of incoming transactions
     */
    List<Transaction> findByToAccount(Account account);
    
    /**
     * Find transactions within date range
     * @param start Start date/time
     * @param end End date/time
     * @return List of transactions in range
     */
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find transactions by type
     * @param type Transaction type
     * @return List of transactions of given type
     */
    List<Transaction> findByType(Transaction.TransactionType type);
    
    /**
     * Find recent transactions for an account
     * @param account Account to search
     * @param limit Maximum number to return
     * @return List of most recent transactions
     */
    List<Transaction> findRecentByAccount(Account account, int limit);
}
