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
    
// =====================================================
// PATCH FOR BankSystem.java - Replace initDemoData() method
// =====================================================
// Find the method: public void initDemoData() { ... }
// Replace the ENTIRE method with the code below
// =====================================================

    /**
     * Initialize demo data with professor's sample users and rich dataset
     */
    public void initDemoData() {
        if (!users.isEmpty()) {
            System.out.println("Data already exists. Skipping demo data initialization.");
            return;
        }
        
        System.out.println("Initializing demo data with professor's sample...");
        
        // ==================== ADMIN USERS ====================
        AdminUser admin = userManager.registerAdminUser("admin", "pass123", "6900000000", 1);
        AdminUser sysadmin = userManager.registerAdminUser("sysadmin", "pass123", "6900000001", 1);
        
        // ==================== INDIVIDUAL USERS (Professor's sample) ====================
        IndividualUser ngioldasis = userManager.registerIndividualUser(
            "ngioldasis", "pass123", "ΝΕΚΤΑΡΙΟΣ ΓΙΟΛΔΑΣΗΣ", 
            "Λεωφ. Βουλιαγμένης 192, Ιωάννινα", "6901000001", "067688302");
        IndividualUser pappas = userManager.registerIndividualUser(
            "pappas", "pass123", "ΝΙΚΟΣ ΠΑΠΠΑΣ", 
            "Λεωφ. Βουλιαγμένης 125, Βόλος", "6901000002", "067688303");
        IndividualUser gchalkiadakis = userManager.registerIndividualUser(
            "gchalkiadakis", "pass123", "ΓΙΩΡΓΟΣ ΧΑΛΚΙΑΔΑΚΗΣ", 
            "Λεωφ. Αλεξάνδρας 183, Θεσσαλονίκη", "6901000003", "067688304");
        IndividualUser mteranova = userManager.registerIndividualUser(
            "mteranova", "pass123", "ΜΑΡΙΑ ΤΕΡΑΝΟΒΑ", 
            "Λεωφ. Κηφισίας 132, Ηράκλειο", "6901000004", "067688305");
        IndividualUser skarakosta = userManager.registerIndividualUser(
            "skarakosta", "pass123", "ΣΟΦΙΑ ΚΑΡΑΚΩΣΤΑ", 
            "Λεωφ. Βουλιαγμένης 94, Βόλος", "6901000005", "067688306");
        IndividualUser edimitriou = userManager.registerIndividualUser(
            "edimitriou", "pass123", "ΕΛΕΝΗ ΔΗΜΗΤΡΙΟΥ", 
            "Οδός Ακαδημίας 70, Χανιά", "6901000006", "067688307");
        
        // Additional individual users
        IndividualUser kpapadopoulos = userManager.registerIndividualUser(
            "kpapadopoulos", "pass123", "ΚΩΣΤΑΣ ΠΑΠΑΔΟΠΟΥΛΟΣ", 
            "Λεωφ. Αλεξάνδρας 124, Ηράκλειο", "6901000007", "067688308");
        IndividualUser anikolaou = userManager.registerIndividualUser(
            "anikolaou", "pass123", "ΑΝΝΑ ΝΙΚΟΛΑΟΥ", 
            "Οδός Πατησίων 52, Ιωάννινα", "6901000008", "067688309");
        IndividualUser dathanasiou = userManager.registerIndividualUser(
            "dathanasiou", "pass123", "ΔΗΜΗΤΡΗΣ ΑΘΑΝΑΣΙΟΥ", 
            "Οδός Ακαδημίας 3, Ιωάννινα", "6901000009", "067688310");
        IndividualUser ikonstantinou = userManager.registerIndividualUser(
            "ikonstantinou", "pass123", "ΙΩΑΝΝΑ ΚΩΝΣΤΑΝΤΙΝΟΥ", 
            "Οδός Ακαδημίας 76, Χανιά", "6901000010", "067688311");
        IndividualUser pgeorgiou = userManager.registerIndividualUser(
            "pgeorgiou", "pass123", "ΠΕΤΡΟΣ ΓΕΩΡΓΙΟΥ", 
            "Οδός Ακαδημίας 129, Αθήνα", "6901000011", "067688312");
        IndividualUser cvasileiou = userManager.registerIndividualUser(
            "cvasileiou", "pass123", "ΧΡΙΣΤΙΝΑ ΒΑΣΙΛΕΙΟΥ", 
            "Οδός Ακαδημίας 184, Θεσσαλονίκη", "6901000012", "067688313");
        IndividualUser amichail = userManager.registerIndividualUser(
            "amichail", "pass123", "ΑΛΕΞΑΝΔΡΟΣ ΜΙΧΑΗΛ", 
            "Λεωφ. Κηφισίας 179, Αθήνα", "6901000013", "067688314");
        IndividualUser kstavrou = userManager.registerIndividualUser(
            "kstavrou", "pass123", "ΚΑΤΕΡΙΝΑ ΣΤΑΥΡΟΥ", 
            "Οδός Σταδίου 197, Ηράκλειο", "6901000014", "067688315");
        IndividualUser tpantelis = userManager.registerIndividualUser(
            "tpantelis", "pass123", "ΘΑΝΑΣΗΣ ΠΑΝΤΕΛΗΣ", 
            "Οδός Πατησίων 132, Ηράκλειο", "6901000015", "067688316");
        IndividualUser mlouka = userManager.registerIndividualUser(
            "mlouka", "pass123", "ΜΑΡΙΑΝΝΑ ΛΟΥΚΑ", 
            "Λεωφ. Βουλιαγμένης 124, Λάρισα", "6901000016", "067688317");
        IndividualUser santoniou = userManager.registerIndividualUser(
            "santoniou", "pass123", "ΣΤΕΛΙΟΣ ΑΝΤΩΝΙΟΥ", 
            "Οδός Πατησίων 116, Βόλος", "6901000017", "067688318");
        IndividualUser vchristou = userManager.registerIndividualUser(
            "vchristou", "pass123", "ΒΑΣΙΛΙΚΗ ΧΡΙΣΤΟΥ", 
            "Λεωφ. Αλεξάνδρας 125, Πάτρα", "6901000018", "067688319");
        
        // ==================== BUSINESS USERS (Utility providers) ====================
        BusinessUser vodafone = userManager.registerBusinessUser(
            "vodafone", "pass123", "VODAFONE GR", "6902000001", "090800700");
        BusinessUser cosmote = userManager.registerBusinessUser(
            "cosmote", "pass123", "COSMOTE", "6902000002", "090800800");
        BusinessUser dei = userManager.registerBusinessUser(
            "dei", "pass123", "ΔΕΗ", "6902000003", "090800900");
        BusinessUser nova = userManager.registerBusinessUser(
            "nova", "pass123", "NOVA", "6902000004", "090800600");
        BusinessUser ote = userManager.registerBusinessUser(
            "ote", "pass123", "OTE", "6902000005", "090800500");
        BusinessUser wind = userManager.registerBusinessUser(
            "wind", "pass123", "WIND", "6902000006", "090800400");
        BusinessUser eydap = userManager.registerBusinessUser(
            "eydap", "pass123", "ΕΥΔΑΠ", "6902000007", "090800300");
        BusinessUser deddie = userManager.registerBusinessUser(
            "deddie", "pass123", "ΔΕΔΔΗΕ", "6902000008", "090800200");
        
        // ==================== PERSONAL ACCOUNTS ====================
        // Each individual gets 1-2 accounts with realistic balances
        PersonalAccount acc01 = accountManager.createPersonalAccount(ngioldasis, new BigDecimal("4567.89"));
        PersonalAccount acc02 = accountManager.createPersonalAccount(ngioldasis, new BigDecimal("12050.00"));
        PersonalAccount acc03 = accountManager.createPersonalAccount(pappas, new BigDecimal("3421.56"));
        PersonalAccount acc04 = accountManager.createPersonalAccount(pappas, new BigDecimal("8932.11"));
        PersonalAccount acc05 = accountManager.createPersonalAccount(gchalkiadakis, new BigDecimal("2156.78"));
        PersonalAccount acc06 = accountManager.createPersonalAccount(gchalkiadakis, new BigDecimal("15678.90"));
        PersonalAccount acc07 = accountManager.createPersonalAccount(mteranova, new BigDecimal("6789.23"));
        PersonalAccount acc08 = accountManager.createPersonalAccount(skarakosta, new BigDecimal("1234.56"));
        PersonalAccount acc09 = accountManager.createPersonalAccount(skarakosta, new BigDecimal("9876.54"));
        PersonalAccount acc10 = accountManager.createPersonalAccount(edimitriou, new BigDecimal("4321.09"));
        PersonalAccount acc11 = accountManager.createPersonalAccount(kpapadopoulos, new BigDecimal("7654.32"));
        PersonalAccount acc12 = accountManager.createPersonalAccount(anikolaou, new BigDecimal("2345.67"));
        PersonalAccount acc13 = accountManager.createPersonalAccount(dathanasiou, new BigDecimal("8765.43"));
        PersonalAccount acc14 = accountManager.createPersonalAccount(ikonstantinou, new BigDecimal("3456.78"));
        PersonalAccount acc15 = accountManager.createPersonalAccount(pgeorgiou, new BigDecimal("6543.21"));
        PersonalAccount acc16 = accountManager.createPersonalAccount(cvasileiou, new BigDecimal("4567.89"));
        PersonalAccount acc17 = accountManager.createPersonalAccount(amichail, new BigDecimal("9012.34"));
        PersonalAccount acc18 = accountManager.createPersonalAccount(kstavrou, new BigDecimal("1098.76"));
        PersonalAccount acc19 = accountManager.createPersonalAccount(tpantelis, new BigDecimal("5432.10"));
        PersonalAccount acc20 = accountManager.createPersonalAccount(mlouka, new BigDecimal("7890.12"));
        PersonalAccount acc21 = accountManager.createPersonalAccount(santoniou, new BigDecimal("2109.87"));
        PersonalAccount acc22 = accountManager.createPersonalAccount(vchristou, new BigDecimal("6789.01"));
        
        // ==================== BUSINESS ACCOUNTS ====================
        BusinessAccount vodafoneAcc = accountManager.createBusinessAccount(vodafone, new BigDecimal("500000.00"));
        BusinessAccount cosmoteAcc = accountManager.createBusinessAccount(cosmote, new BigDecimal("750000.00"));
        BusinessAccount deiAcc = accountManager.createBusinessAccount(dei, new BigDecimal("1000000.00"));
        BusinessAccount novaAcc = accountManager.createBusinessAccount(nova, new BigDecimal("350000.00"));
        BusinessAccount oteAcc = accountManager.createBusinessAccount(ote, new BigDecimal("600000.00"));
        BusinessAccount windAcc = accountManager.createBusinessAccount(wind, new BigDecimal("400000.00"));
        BusinessAccount eydapAcc = accountManager.createBusinessAccount(eydap, new BigDecimal("800000.00"));
        BusinessAccount deddieAcc = accountManager.createBusinessAccount(deddie, new BigDecimal("900000.00"));
        
        // ==================== CO-OWNERS ====================
        acc02.addSecondaryOwner(pappas);  // pappas co-owner on ngioldasis account
        acc06.addSecondaryOwner(mteranova);  // mteranova co-owner on gchalkiadakis account
        acc09.addSecondaryOwner(edimitriou);  // edimitriou co-owner on skarakosta account
        
        // ==================== BILLS ====================
        // Create bills from various providers to individuals
        // Due dates in 2026, some paid, some unpaid
        
        // VODAFONE bills
        billManager.createBill(pappas, vodafone, "VODAFONE GR", new BigDecimal("46.98"), currentDate.plusDays(47));
        billManager.createBill(gchalkiadakis, vodafone, "VODAFONE GR", new BigDecimal("38.68"), currentDate.plusDays(47));
        billManager.createBill(skarakosta, vodafone, "VODAFONE GR", new BigDecimal("73.57"), currentDate.plusDays(73));
        billManager.createBill(anikolaou, vodafone, "VODAFONE GR", new BigDecimal("68.30"), currentDate.plusDays(44));
        billManager.createBill(amichail, vodafone, "VODAFONE GR", new BigDecimal("28.09"), currentDate.plusDays(79));
        billManager.createBill(vchristou, vodafone, "VODAFONE GR", new BigDecimal("69.48"), currentDate.plusDays(12));
        
        // COSMOTE bills
        billManager.createBill(pappas, cosmote, "COSMOTE", new BigDecimal("36.25"), currentDate.plusDays(79));
        billManager.createBill(mteranova, cosmote, "COSMOTE", new BigDecimal("54.89"), currentDate.plusDays(56));
        billManager.createBill(kpapadopoulos, cosmote, "COSMOTE", new BigDecimal("89.41"), currentDate.plusDays(23));
        billManager.createBill(dathanasiou, cosmote, "COSMOTE", new BigDecimal("42.15"), currentDate.plusDays(67));
        
        // ΔΕΗ bills
        billManager.createBill(pappas, dei, "ΔΕΗ", new BigDecimal("85.71"), currentDate.plusDays(51));
        billManager.createBill(ngioldasis, dei, "ΔΕΗ", new BigDecimal("126.94"), currentDate.plusDays(38));
        billManager.createBill(gchalkiadakis, dei, "ΔΕΗ", new BigDecimal("153.75"), currentDate.plusDays(62));
        billManager.createBill(skarakosta, dei, "ΔΕΗ", new BigDecimal("98.50"), currentDate.plusDays(19));
        billManager.createBill(mteranova, dei, "ΔΕΗ", new BigDecimal("112.30"), currentDate.plusDays(45));
        
        // NOVA bills
        billManager.createBill(pappas, nova, "NOVA", new BigDecimal("48.22"), currentDate.plusDays(39));
        billManager.createBill(gchalkiadakis, nova, "NOVA", new BigDecimal("23.10"), currentDate.plusDays(55));
        billManager.createBill(edimitriou, nova, "NOVA", new BigDecimal("35.90"), currentDate.plusDays(28));
        
        // OTE bills
        billManager.createBill(pappas, ote, "OTE", new BigDecimal("65.21"), currentDate.plusDays(38));
        billManager.createBill(ikonstantinou, ote, "OTE", new BigDecimal("48.75"), currentDate.plusDays(71));
        
        // WIND bills
        billManager.createBill(pappas, wind, "WIND", new BigDecimal("21.75"), currentDate.plusDays(24));
        billManager.createBill(pgeorgiou, wind, "WIND", new BigDecimal("33.40"), currentDate.plusDays(52));
        
        // ΕΥΔΑΠ bills
        billManager.createBill(pappas, eydap, "ΕΥΔΑΠ", new BigDecimal("70.70"), currentDate.plusDays(22));
        billManager.createBill(cvasileiou, eydap, "ΕΥΔΑΠ", new BigDecimal("58.90"), currentDate.plusDays(41));
        
        // ΔΕΔΔΗΕ bills
        billManager.createBill(gchalkiadakis, deddie, "ΔΕΔΔΗΕ", new BigDecimal("84.22"), currentDate.plusDays(33));
        billManager.createBill(kstavrou, deddie, "ΔΕΔΔΗΕ", new BigDecimal("76.50"), currentDate.plusDays(59));
        
        // ==================== STANDING ORDERS (Transfers only) ====================
        standingOrderManager.createTransferStandingOrder(
            acc01, acc03, new BigDecimal("100.00"), 1, 5, "Monthly allowance", ngioldasis);
        standingOrderManager.createTransferStandingOrder(
            acc05, acc07, new BigDecimal("250.00"), 1, 3, "Rent payment", gchalkiadakis);
        standingOrderManager.createTransferStandingOrder(
            acc11, acc12, new BigDecimal("50.00"), 1, 28, "Shared expenses", kpapadopoulos);
        standingOrderManager.createTransferStandingOrder(
            acc13, acc14, new BigDecimal("75.00"), 1, 21, "Gym membership share", dathanasiou);
        standingOrderManager.createTransferStandingOrder(
            acc15, acc16, new BigDecimal("200.00"), 1, 6, "Loan repayment", pgeorgiou);
        standingOrderManager.createTransferStandingOrder(
            acc17, acc19, new BigDecimal("150.00"), 1, 9, "Monthly contribution", amichail);
        
        // ==================== SAMPLE TRANSACTIONS ====================
        // Some deposits
        transactionManager.deposit(acc01, new BigDecimal("500.00"), "Salary deposit");
        transactionManager.deposit(acc03, new BigDecimal("1200.00"), "Freelance payment");
        transactionManager.deposit(acc05, new BigDecimal("800.00"), "Bonus");
        transactionManager.deposit(acc07, new BigDecimal("350.00"), "Gift");
        
        // Some transfers
        transactionManager.transfer(acc01, acc03, new BigDecimal("150.00"), "Birthday gift");
        transactionManager.transfer(acc05, acc07, new BigDecimal("200.00"), "Shared dinner");
        transactionManager.transfer(acc11, acc13, new BigDecimal("75.00"), "Concert tickets");
        
        // Some withdrawals
        transactionManager.withdraw(acc03, new BigDecimal("100.00"), "ATM withdrawal");
        transactionManager.withdraw(acc07, new BigDecimal("50.00"), "ATM withdrawal");
        transactionManager.withdraw(acc15, new BigDecimal("200.00"), "Cash withdrawal");
        
        saveToCSV();
        System.out.println("Demo data initialized successfully!");
        System.out.println("\n========================================");
        System.out.println("  Demo Users (password: pass123)");
        System.out.println("========================================");
        System.out.println("  ADMINS:");
        System.out.println("    admin, sysadmin");
        System.out.println("  INDIVIDUALS (Professor's sample):");
        System.out.println("    ngioldasis, pappas, gchalkiadakis");
        System.out.println("    mteranova, skarakosta, edimitriou");
        System.out.println("  INDIVIDUALS (Additional):");
        System.out.println("    kpapadopoulos, anikolaou, dathanasiou");
        System.out.println("    ikonstantinou, pgeorgiou, cvasileiou");
        System.out.println("    amichail, kstavrou, tpantelis");
        System.out.println("    mlouka, santoniou, vchristou");
        System.out.println("  BUSINESSES:");
        System.out.println("    vodafone, cosmote, dei, nova");
        System.out.println("    ote, wind, eydap, deddie");
        System.out.println("========================================");
    }

// =====================================================
// END OF PATCH
// =====================================================

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
