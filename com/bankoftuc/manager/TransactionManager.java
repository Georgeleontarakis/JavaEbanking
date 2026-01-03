package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import com.bankoftuc.model.Transaction.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Manages all transactions including deposits, withdrawals, and transfers.
 */
public class TransactionManager {
    
    private List<Transaction> transactions;
    private AtomicLong transactionIdCounter;
    
    // Fee configurations
    private static final BigDecimal SEPA_FEE = new BigDecimal("1.50");
    private static final BigDecimal SWIFT_FEE = new BigDecimal("25.00");
    private static final BigDecimal BILL_PAYMENT_FEE = new BigDecimal("0.50");
    
    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.transactionIdCounter = new AtomicLong(1);
    }
    
    public TransactionManager(List<Transaction> transactions) {
        this.transactions = transactions;
        this.transactionIdCounter = new AtomicLong(transactions.size() + 1);
    }
    
    /**
     * Deposit money into an account
     */
    public Transaction deposit(Account account, BigDecimal amount, String description) {
        account.deposit(amount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            null, account,
            amount,
            TransactionType.DEPOSIT,
            description != null ? description : "Cash deposit"
        );
        transaction.setBalanceAfter(account.getBalance());
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Withdraw money from an account
     */
    public Transaction withdraw(Account account, BigDecimal amount, String description) {
        account.withdraw(amount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            account, null,
            amount,
            TransactionType.WITHDRAWAL,
            description != null ? description : "Cash withdrawal"
        );
        transaction.setBalanceAfter(account.getBalance());
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Transfer money between accounts (internal transfer)
     */
    public Transaction transfer(Account fromAccount, Account toAccount, 
                                BigDecimal amount, String description) {
        // Validate
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        // Perform transfer
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        
        // Create outgoing transaction
        Transaction outgoing = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, toAccount,
            amount,
            TransactionType.TRANSFER_OUT,
            description != null ? description : "Transfer to " + toAccount.getIban()
        );
        outgoing.setBalanceAfter(fromAccount.getBalance());
        transactions.add(outgoing);
        
        // Create incoming transaction
        Transaction incoming = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, toAccount,
            amount,
            TransactionType.TRANSFER_IN,
            description != null ? description : "Transfer from " + fromAccount.getIban()
        );
        incoming.setBalanceAfter(toAccount.getBalance());
        transactions.add(incoming);
        
        return outgoing;
    }
    /**
     * Record a bill payment transaction
     * This is used for standing order bill payments and manual bill payments
     */
    public Transaction recordBillPayment(Account fromAccount, BigDecimal amount, String description) {
        // Note: The actual withdrawal should already be done before calling this
        // This method just records the transaction
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.BILL_PAYMENT,
            description != null ? description : "Bill payment"
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Pay a bill - withdraws from account and records transaction
     */
    public Transaction payBill(Account fromAccount, BigDecimal amount, String description) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        fromAccount.withdraw(amount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.BILL_PAYMENT,
            description != null ? description : "Bill payment"
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        return transaction;
    }

    
    /**
     * Execute a SEPA transfer using the Bank Transfer API
     * API has 75% success rate, 25% failure rate
     */
    public Transaction sepaTransfer(Account fromAccount, String toIban, 
                                     BigDecimal amount, String description) {
        BigDecimal totalAmount = amount.add(SEPA_FEE);
        
        if (fromAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds (including SEPA fee of " + SEPA_FEE + "€)");
        }
        
        // Call the external SEPA API
        System.out.println("[INFO] Connecting to SEPA transfer API...");
        BankTransferAPI.TransferResult result = BankTransferAPI.executeSepaTransfer(
            amount.doubleValue(),
            toIban
        );
        
        if (!result.isSuccess()) {
            // API returned failure - do not withdraw money
            throw new IllegalStateException("SEPA transfer failed: " + result.getMessage());
        }
        
        // API success - withdraw money and record transaction
        System.out.println("[OK] SEPA transfer approved. Transaction ID: " + result.getTransactionId());
        fromAccount.withdraw(totalAmount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.SEPA_TRANSFER,
            String.format("SEPA transfer to %s - %s (Fee: %.2f€) [API TxID: %s]", 
                toIban, description != null ? description : "", SEPA_FEE, result.getTransactionId())
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        
        return transaction;
    }
    
    /**
     * Execute a SEPA transfer with full details using the Bank Transfer API
     */
    public Transaction sepaTransferFull(Account fromAccount, String toIban, 
                                         BigDecimal amount, String description,
                                         String creditorName, String creditorBankBic,
                                         String creditorBankName, String charges) {
        BigDecimal totalAmount = amount.add(SEPA_FEE);
        
        if (fromAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds (including SEPA fee of " + SEPA_FEE + "€)");
        }
        
        // Call the external SEPA API with full details
        System.out.println("[INFO] Connecting to SEPA transfer API...");
        BankTransferAPI.TransferResult result = BankTransferAPI.executeSepaTransfer(
            amount.doubleValue(),
            creditorName,
            toIban,
            creditorBankBic,
            creditorBankName,
            java.time.LocalDate.now(),
            charges
        );
        
        if (!result.isSuccess()) {
            throw new IllegalStateException("SEPA transfer failed: " + result.getMessage());
        }
        
        System.out.println("[OK] SEPA transfer approved. Transaction ID: " + result.getTransactionId());
        fromAccount.withdraw(totalAmount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.SEPA_TRANSFER,
            String.format("SEPA transfer to %s (%s) - %s (Fee: %.2f€) [API TxID: %s]", 
                creditorName, toIban, description != null ? description : "", SEPA_FEE, result.getTransactionId())
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        
        return transaction;
    }
    
    /**
     * Execute a SWIFT transfer using the Bank Transfer API
     * API has 75% success rate, 25% failure rate
     */
    public Transaction swiftTransfer(Account fromAccount, String toAccount, 
                                      BigDecimal amount, String description) {
        BigDecimal totalAmount = amount.add(SWIFT_FEE);
        
        if (fromAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds (including SWIFT fee of " + SWIFT_FEE + "€)");
        }
        
        // Call the external SWIFT API
        System.out.println("[INFO] Connecting to SWIFT transfer API...");
        BankTransferAPI.TransferResult result = BankTransferAPI.executeSwiftTransfer(
            amount.doubleValue(),
            toAccount,
            "UNKNOWN" // Default SWIFT code when not provided
        );
        
        if (!result.isSuccess()) {
            // API returned failure - do not withdraw money
            throw new IllegalStateException("SWIFT transfer failed: " + result.getMessage());
        }
        
        // API success - withdraw money and record transaction
        System.out.println("[OK] SWIFT transfer approved. Transaction ID: " + result.getTransactionId());
        fromAccount.withdraw(totalAmount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.SWIFT_TRANSFER,
            String.format("SWIFT transfer to %s - %s (Fee: %.2f€) [API TxID: %s]", 
                toAccount, description != null ? description : "", SWIFT_FEE, result.getTransactionId())
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        
        return transaction;
    }
    
    /**
     * Execute a SWIFT transfer with full details using the Bank Transfer API
     */
    public Transaction swiftTransferFull(Account fromAccount, String beneficiaryAccount, 
                                          BigDecimal amount, String description,
                                          String currency, String beneficiaryName,
                                          String beneficiaryAddress, String bankName,
                                          String swiftCode, String bankCountry,
                                          String chargingModel) {
        BigDecimal totalAmount = amount.add(SWIFT_FEE);
        
        if (fromAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds (including SWIFT fee of " + SWIFT_FEE + "€)");
        }
        
        // Call the external SWIFT API with full details
        System.out.println("[INFO] Connecting to SWIFT transfer API...");
        BankTransferAPI.TransferResult result = BankTransferAPI.executeSwiftTransfer(
            currency,
            amount.doubleValue(),
            beneficiaryName,
            beneficiaryAddress,
            beneficiaryAccount,
            bankName,
            swiftCode,
            bankCountry,
            chargingModel
        );
        
        if (!result.isSuccess()) {
            throw new IllegalStateException("SWIFT transfer failed: " + result.getMessage());
        }
        
        System.out.println("[OK] SWIFT transfer approved. Transaction ID: " + result.getTransactionId());
        fromAccount.withdraw(totalAmount);
        
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            fromAccount, null,
            amount,
            TransactionType.SWIFT_TRANSFER,
            String.format("SWIFT transfer to %s (%s) - %s (Fee: %.2f€) [API TxID: %s]", 
                beneficiaryName, beneficiaryAccount, description != null ? description : "", SWIFT_FEE, result.getTransactionId())
        );
        transaction.setBalanceAfter(fromAccount.getBalance());
        transactions.add(transaction);
        
        return transaction;
    }
    
    /**
     * Record an interest payment
     */
    public Transaction recordInterest(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            null, account,
            amount,
            TransactionType.INTEREST,
            description != null ? description : "Monthly interest"
        );
        transaction.setBalanceAfter(account.getBalance());
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Record a maintenance fee
     */
    public Transaction recordMaintenanceFee(Account account, BigDecimal amount) {
        Transaction transaction = new Transaction(
            transactionIdCounter.getAndIncrement(),
            account, null,
            amount,
            TransactionType.MAINTENANCE_FEE,
            "Monthly maintenance fee"
        );
        transaction.setBalanceAfter(account.getBalance());
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Get all transactions for an account
     */
    public List<Transaction> getTransactionsForAccount(Account account) {
        return transactions.stream()
            .filter(t -> (t.getFromAccount() != null && t.getFromAccount().getIban().equals(account.getIban())) ||
                        (t.getToAccount() != null && t.getToAccount().getIban().equals(account.getIban())))
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent transactions for an account
     */
    public List<Transaction> getRecentTransactions(Account account, int limit) {
        List<Transaction> accountTransactions = getTransactionsForAccount(account);
        int startIndex = Math.max(0, accountTransactions.size() - limit);
        return accountTransactions.subList(startIndex, accountTransactions.size());
    }
    
    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Get the transactions list reference
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    /**
     * Get fee for SEPA transfer
     */
    public static BigDecimal getSepaFee() {
        return SEPA_FEE;
    }
    
    /**
     * Get fee for SWIFT transfer
     */
    public static BigDecimal getSwiftFee() {
        return SWIFT_FEE;
    }
    
    /**
     * Get fee for bill payment
     */
    public static BigDecimal getBillPaymentFee() {
        return BILL_PAYMENT_FEE;
    }
}