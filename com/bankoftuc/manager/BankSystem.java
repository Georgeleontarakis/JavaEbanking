package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import com.bankoftuc.model.Bill.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main system class implementing Singleton pattern.
 * Coordinates all managers and provides central access point.
 * Uses CSV files for data persistence.
 */
public class BankSystem {
    
    private static BankSystem instance;
    
    private List<User> users;
    private List<Account> accounts;
    private List<Bill> bills;
    private List<StandingOrder> standingOrders;
    private List<Transaction> transactions;
    
    private UserManager userManager;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BillManager billManager;
    private StandingOrderManager standingOrderManager;
    private AuthManager authManager;
    
    private LocalDate currentDate;
    
    private BankSystem() {
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.bills = new ArrayList<>();
        this.standingOrders = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.currentDate = LocalDate.now();
        
        initializeManagers();
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized BankSystem getInstance() {
        if (instance == null) {
            instance = new BankSystem();
            instance.loadFromCSV();
        }
        return instance;
    }
    
    /**
     * Reset the singleton (for testing purposes)
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    /**
     * Initialize all managers with current data
     */
    private void initializeManagers() {
        this.userManager = new UserManager(users);
        this.accountManager = new AccountManager(accounts);
        this.transactionManager = new TransactionManager(transactions);
        this.billManager = new BillManager(bills);
        this.standingOrderManager = new StandingOrderManager(standingOrders);
        this.authManager = new AuthManager(users);
    }
    
    /**
     * Initialize demo data for testing
     */
    public void initDemoData() {
        if (!users.isEmpty()) {
            System.out.println("Data already exists. Skipping demo data initialization.");
            return;
        }
        
        System.out.println("Initializing demo data...");
        
        // Create admin user
        AdminUser admin = userManager.registerAdminUser("admin", "admin123", "6900000000", 1);
        
        // Create individual users
        IndividualUser john = userManager.registerIndividualUser(
            "john", "pass123", "John Smith", "123 Main St, Athens", "6901234567", "123456789");
        IndividualUser maria = userManager.registerIndividualUser(
            "maria", "pass123", "Maria Papadopoulou", "456 Oak Ave, Thessaloniki", "6902345678", "987654321");
        IndividualUser nikos = userManager.registerIndividualUser(
            "nikos", "pass123", "Nikos Georgiou", "789 Pine Rd, Patras", "6903456789", "456789123");
        
        // Create business users
        BusinessUser techCorp = userManager.registerBusinessUser(
            "techcorp", "pass123", "TechCorp Solutions", "6904567890", "EL123456789");
        BusinessUser utilityCompany = userManager.registerBusinessUser(
            "utility", "pass123", "Greek Utilities Co", "6905678901", "EL987654321");
        
        // Create accounts
        PersonalAccount johnAccount1 = accountManager.createPersonalAccount(john, new BigDecimal("5000.00"));
        PersonalAccount johnAccount2 = accountManager.createPersonalAccount(john, new BigDecimal("2500.00"));
        PersonalAccount mariaAccount = accountManager.createPersonalAccount(maria, new BigDecimal("3500.00"));
        PersonalAccount nikosAccount = accountManager.createPersonalAccount(nikos, new BigDecimal("1500.00"));
        
        // Add Maria as co-owner to John's second account
        johnAccount2.addSecondaryOwner(maria);
        
        BusinessAccount techCorpAccount = accountManager.createBusinessAccount(techCorp, new BigDecimal("50000.00"));
        BusinessAccount utilityAccount = accountManager.createBusinessAccount(utilityCompany, new BigDecimal("100000.00"));
        
        // Create some bills
        billManager.createBill(john, utilityCompany, "Greek Utilities Co", 
            new BigDecimal("85.50"), currentDate.plusDays(15), "RF00001234");
        billManager.createBill(john, techCorp, "TechCorp Solutions", 
            new BigDecimal("199.99"), currentDate.plusDays(30), "RF00001235");
        billManager.createBill(maria, utilityCompany, "Greek Utilities Co", 
            new BigDecimal("72.30"), currentDate.plusDays(20), "RF00001236");
        billManager.createBill(nikos, utilityCompany, "Greek Utilities Co", 
            new BigDecimal("95.00"), currentDate.plusDays(10), "RF00001237");
        
        // Create some transactions
        transactionManager.deposit(johnAccount1, new BigDecimal("1000.00"), "Initial deposit");
        transactionManager.transfer(johnAccount1, mariaAccount, new BigDecimal("250.00"), "Birthday gift");
        transactionManager.withdraw(mariaAccount, new BigDecimal("100.00"), "ATM withdrawal");
        
        // Create a standing order
        standingOrderManager.createTransferStandingOrder(
            johnAccount1, mariaAccount, new BigDecimal("100.00"), 
            1, 15, "Monthly allowance", john);
        
        saveToCSV();
        System.out.println("Demo data initialized successfully!");
        System.out.println("\nDemo Users:");
        System.out.println("  Admin: username=admin, password=admin123");
        System.out.println("  Individual: username=john, password=pass123");
        System.out.println("  Individual: username=maria, password=pass123");
        System.out.println("  Individual: username=nikos, password=pass123");
        System.out.println("  Business: username=techcorp, password=pass123");
        System.out.println("  Business: username=utility, password=pass123");
    }

    /**
     * Advance the system date by exactly one day.
     * Added for GUI compatibility.
     */
    public void advanceDay() {
        simulateTimePassing(this.currentDate.plusDays(1));
    }

    /**
     * Simulate a specific number of days passing.
     * Added for GUI compatibility.
     * @param days The number of days to fast-forward
     */
    public void simulateDays(int days) {
        if (days > 0) {
            simulateTimePassing(this.currentDate.plusDays(days));
        }
    }
    
    /**
     * Force reset the system date back to the actual real-world today.
     */
    public void resetSystemDate() {
        this.currentDate = LocalDate.now();
        saveToCSV(); // Save the reset date immediately
        System.out.println("Date reset to today: " + this.currentDate);
    }

    /**
     * Simulate time passing (for demo/testing)
     */
    public void simulateTimePassing(LocalDate targetDate) {
        if (targetDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("Cannot simulate backwards in time");
        }
        
        System.out.println("Simulating time from " + currentDate + " to " + targetDate);
        
        while (!currentDate.isAfter(targetDate)) {
            // Daily interest accrual
            for (Account account : accounts) {
                if (account.getStatus() == Account.AccountStatus.ACTIVE) {
                    account.accrueInterest();
                }
            }
            
            // Check for month end
            LocalDate nextDay = currentDate.plusDays(1);
            if (nextDay.getMonthValue() != currentDate.getMonthValue() || 
                currentDate.equals(targetDate)) {
                
                // Apply monthly interest
                for (Account account : accounts) {
                    if (account.getStatus() == Account.AccountStatus.ACTIVE) {
                        BigDecimal interest = account.applyMonthlyInterest();
                        if (interest.compareTo(BigDecimal.ZERO) > 0) {
                            transactionManager.recordInterest(account, interest, "Monthly interest");
                        }
                    }
                }
                
                // Apply maintenance fees for business accounts
                for (BusinessAccount ba : accountManager.getAllBusinessAccounts()) {
                    if (ba.getStatus() == Account.AccountStatus.ACTIVE) {
                        BigDecimal fee = ba.applyMaintenanceFee();
                        if (fee.compareTo(BigDecimal.ZERO) > 0) {
                            transactionManager.recordMaintenanceFee(ba, fee);
                        }
                    }
                }
            }
            
            // Execute standing orders
            standingOrderManager.executeDueOrders(currentDate, transactionManager, billManager);
            
            // Update overdue bills
            billManager.updateOverdueBills(currentDate);
            
            currentDate = currentDate.plusDays(1);
        }
        
        currentDate = targetDate;
        saveToCSV();
        System.out.println("Time simulation complete. Current date: " + currentDate);
    }
    
    /**
     * Pay a bill
     */
    public Transaction payBill(Bill bill, Account sourceAccount) {
        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already paid");
        }
        
        BigDecimal totalAmount = bill.getAmount().add(TransactionManager.getBillPaymentFee());
        
        if (sourceAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        sourceAccount.withdraw(totalAmount);
        bill.markAsPaid(LocalDateTime.now());
        
        // Credit the business account
        if (bill.getIssuer() != null) {
            List<BusinessAccount> issuerAccounts = accountManager.getAccountsForBusinessUser(bill.getIssuer());
            if (!issuerAccounts.isEmpty()) {
                issuerAccounts.get(0).deposit(bill.getAmount());
            }
        }
        
        Transaction transaction = new Transaction(
            transactions.size() + 1,
            sourceAccount, null,
            bill.getAmount(),
            Transaction.TransactionType.BILL_PAYMENT,
            "Bill payment: " + bill.getProviderName() + " (RF: " + bill.getRfCode() + ")"
        );
        transaction.setBalanceAfter(sourceAccount.getBalance());
        transactions.add(transaction);
        
        saveToCSV();
        return transaction;
    }
    
    /**
     * Save all data to CSV files
     */
    public void saveToCSV() {
        DataManager.saveUsers(users);
        DataManager.saveAccounts(accounts);
        DataManager.saveCoOwners(accounts);
        DataManager.saveTransactions(transactions);
        DataManager.saveBills(bills);
        DataManager.saveStandingOrders(standingOrders);
        DataManager.saveSystemState(currentDate);
    }
    
    /**
     * Alias for saveToCSV for compatibility
     */
    public void saveToFile() {
        saveToCSV();
    }
    
    /**
     * Load all data from CSV files
     */
    private void loadFromCSV() {
        if (!DataManager.dataExists()) {
            return;
        }
        
        // Load in correct order due to dependencies
        users.clear();
        users.addAll(DataManager.loadUsers());
        
        accounts.clear();
        accounts.addAll(DataManager.loadAccounts(users));
        
        transactions.clear();
        transactions.addAll(DataManager.loadTransactions(accounts));
        
        bills.clear();
        bills.addAll(DataManager.loadBills(users));
        
        standingOrders.clear();
        standingOrders.addAll(DataManager.loadStandingOrders(accounts, users));
        
        currentDate = DataManager.loadSystemDate();
        
        // Reinitialize managers with loaded data
        initializeManagers();
    }
    
    /**
     * Delete all saved data
     */
    public static void deleteSavedData() {
        DataManager.deleteAllData();
    }
    
    // Getters for managers
    public UserManager getUserManager() { return userManager; }
    public AccountManager getAccountManager() { return accountManager; }
    public TransactionManager getTransactionManager() { return transactionManager; }
    public BillManager getBillManager() { return billManager; }
    public StandingOrderManager getStandingOrderManager() { return standingOrderManager; }
    public AuthManager getAuthManager() { return authManager; }
    
    // Getters for data
    public List<User> getUsers() { return users; }
    public List<Account> getAccounts() { return accounts; }
    public List<Bill> getBills() { return bills; }
    public List<StandingOrder> getStandingOrders() { return standingOrders; }
    public List<Transaction> getTransactions() { return transactions; }
    public LocalDate getCurrentDate() { return currentDate; }
}
