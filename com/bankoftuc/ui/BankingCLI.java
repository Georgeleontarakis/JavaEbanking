package com.bankoftuc.ui;

import com.bankoftuc.manager.*;
import com.bankoftuc.model.*;
import com.bankoftuc.util.ConsoleUI;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Command-Line Interface for the Bank of TUC eBanking System.
 */
public class BankingCLI {
    
    private BankSystem bankSystem;
    private ConsoleUI ui;
    private boolean running;
    
    public BankingCLI() {
        this.bankSystem = BankSystem.getInstance();
        this.ui = new ConsoleUI();
        this.running = true;
    }
    
    /**
     * Start the CLI application
     */
    public void start() {
        ui.printHeader("Welcome to Bank of TUC - eBanking System");
        ui.printInfo("Current Date: " + bankSystem.getCurrentDate());
        
        // Initialize demo data if needed
        if (bankSystem.getUsers().isEmpty()) {
            if (ui.readYesNo("No data found. Initialize demo data?")) {
                bankSystem.initDemoData();
            }
        }
        
        while (running) {
            if (!bankSystem.getAuthManager().isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        
        ui.printInfo("Thank you for using Bank of TUC. Goodbye!");
        ui.close();
    }
    
    /**
     * Show login menu
     */
    private void showLoginMenu() {
        ui.printHeader("Bank of TUC - Login");
        ui.printInfo("System Date: " + bankSystem.getCurrentDate());
        ui.printBlank();
        ui.printMenuOption(1, "Login");
        ui.printMenuOption(2, "Reset Demo Data");
        ui.printMenuOption(0, "Exit");
        ui.printBlank();
        
        int choice = ui.readIntInRange("Select option", 0, 2);
        
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleResetData();
                break;
            case 0:
                running = false;
                break;
        }
    }
    
    /**
     * Handle login process
     */
    private void handleLogin() {
        ui.printSubHeader("Login");
        String username = ui.readString("Username");
        String password = ui.readPassword("Password");
        
        if (bankSystem.getAuthManager().login(username, password)) {
            ui.printSuccess("Login successful! Welcome, " + username);
            ui.waitForEnter();
        } else {
            ui.printError("Invalid username or password");
            ui.waitForEnter();
        }
    }
    
    /**
     * Handle data reset
     */
    private void handleResetData() {
        if (ui.readYesNo("Are you sure you want to reset all data?")) {
            BankSystem.deleteSavedData();
            BankSystem.resetInstance();
            bankSystem = BankSystem.getInstance();
            bankSystem.initDemoData();
            ui.printSuccess("Data has been reset");
            ui.waitForEnter();
        }
    }
    
    /**
     * Show main menu based on user role
     */
    private void showMainMenu() {
        User currentUser = bankSystem.getAuthManager().getCurrentUser();
        
        switch (currentUser.getRole()) {
            case "INDIVIDUAL":
                showIndividualMenu((IndividualUser) currentUser);
                break;
            case "BUSINESS":
                showBusinessMenu((BusinessUser) currentUser);
                break;
            case "ADMIN":
                showAdminMenu((AdminUser) currentUser);
                break;
        }
    }
    
    /**
     * Show menu for individual customers
     */
    private void showIndividualMenu(IndividualUser user) {
        ui.printHeader("Individual Customer Menu - " + user.getFullName());
        ui.printInfo("System Date: " + bankSystem.getCurrentDate());
        ui.printBlank();
        ui.printMenuOption(1, "View Account Overview");
        ui.printMenuOption(2, "View Account Statements");
        ui.printMenuOption(3, "Deposit Money");
        ui.printMenuOption(4, "Withdraw Money");
        ui.printMenuOption(5, "Transfer Money");
        ui.printMenuOption(6, "SEPA Transfer (External)");
        ui.printMenuOption(7, "SWIFT Transfer (International)");
        ui.printMenuOption(8, "View Bills");
        ui.printMenuOption(9, "Pay Bill");
        ui.printMenuOption(10, "Manage Standing Orders");
        ui.printMenuOption(11, "Manage Co-Owners");
        ui.printMenuOption(12, "Change Password");
        ui.printMenuOption(0, "Logout");
        ui.printBlank();
        
        int choice = ui.readIntInRange("Select option", 0, 12);
        
        switch (choice) {
            case 1: viewAccountOverview(user); break;
            case 2: viewAccountStatements(user); break;
            case 3: depositMoney(user); break;
            case 4: withdrawMoney(user); break;
            case 5: transferMoney(user); break;
            case 6: sepaTransfer(user); break;
            case 7: swiftTransfer(user); break;
            case 8: viewBills(user); break;
            case 9: payBill(user); break;
            case 10: manageStandingOrders(user); break;
            case 11: manageCoOwners(user); break;
            case 12: changePassword(); break;
            case 0: logout(); break;
        }
    }
    
    /**
     * Show menu for business customers
     */
    private void showBusinessMenu(BusinessUser user) {
        ui.printHeader("Business Customer Menu - " + user.getBusinessName());
        ui.printInfo("System Date: " + bankSystem.getCurrentDate());
        ui.printBlank();
        ui.printMenuOption(1, "View Account Overview");
        ui.printMenuOption(2, "View Account Statements");
        ui.printMenuOption(3, "Deposit Money");
        ui.printMenuOption(4, "Withdraw Money");
        ui.printMenuOption(5, "Transfer Money");
        ui.printMenuOption(6, "SEPA Transfer (External)");
        ui.printMenuOption(7, "SWIFT Transfer (International)");
        ui.printMenuOption(8, "Issue Bill to Customer");
        ui.printMenuOption(9, "View Issued Bills");
        ui.printMenuOption(10, "Manage Standing Orders");
        ui.printMenuOption(11, "Change Password");
        ui.printMenuOption(0, "Logout");
        ui.printBlank();
        
        int choice = ui.readIntInRange("Select option", 0, 11);
        
        switch (choice) {
            case 1: viewBusinessAccountOverview(user); break;
            case 2: viewBusinessAccountStatements(user); break;
            case 3: depositMoneyBusiness(user); break;
            case 4: withdrawMoneyBusiness(user); break;
            case 5: transferMoneyBusiness(user); break;
            case 6: sepaTransferBusiness(user); break;
            case 7: swiftTransferBusiness(user); break;
            case 8: issueBill(user); break;
            case 9: viewIssuedBills(user); break;
            case 10: manageStandingOrdersBusiness(user); break;
            case 11: changePassword(); break;
            case 0: logout(); break;
        }
    }
    
    /**
     * Show menu for admin users
     */
    private void showAdminMenu(AdminUser user) {
        ui.printHeader("Admin Menu");
        ui.printInfo("System Date: " + bankSystem.getCurrentDate());
        ui.printBlank();
        ui.printMenuOption(1, "View All Users");
        ui.printMenuOption(2, "Create New User");
        ui.printMenuOption(3, "Block/Unblock User");
        ui.printMenuOption(4, "View All Accounts");
        ui.printMenuOption(5, "View Account Details");
        ui.printMenuOption(6, "Create Account for User");
        ui.printMenuOption(7, "View All Standing Orders");
        ui.printMenuOption(8, "View All Bills");
        ui.printMenuOption(9, "Simulate Time Passing");
        ui.printMenuOption(10, "Change Password");
        ui.printMenuOption(0, "Logout");
        ui.printBlank();
        
        int choice = ui.readIntInRange("Select option", 0, 10);
        
        switch (choice) {
            case 1: viewAllUsers(); break;
            case 2: createNewUser(); break;
            case 3: blockUnblockUser(); break;
            case 4: viewAllAccounts(); break;
            case 5: viewAccountDetails(); break;
            case 6: createAccountForUser(); break;
            case 7: viewAllStandingOrders(); break;
            case 8: viewAllBills(); break;
            case 9: simulateTimePassing(); break;
            case 10: changePassword(); break;
            case 0: logout(); break;
        }
    }
    
    // ==================== Individual Customer Methods ====================
    
    private void viewAccountOverview(IndividualUser user) {
        ui.printSubHeader("Account Overview");
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts yet.");
        } else {
            for (PersonalAccount account : accounts) {
                System.out.printf("IBAN: %s%n", account.getIban());
                System.out.printf("Balance: %.2f EUR%n", account.getBalance());
                System.out.printf("Status: %s%n", account.getStatus());
                System.out.printf("Type: %s%n", account.isPrimaryOwner(user) ? "Primary Owner" : "Co-Owner");
                ui.printSeparator();
            }
        }
        ui.waitForEnter();
    }
    
    private void viewAccountStatements(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        PersonalAccount account = selectAccount(accounts);
        if (account == null) return;
        
        ui.printSubHeader("Account Statements - " + account.getIban());
        List<Transaction> transactions = bankSystem.getTransactionManager().getTransactionsForAccount(account);
        
        if (transactions.isEmpty()) {
            ui.printInfo("No transactions found.");
        } else {
            System.out.printf("%-12s | %-10s | %-40s%n", "Date", "Amount", "Description");
            ui.printSeparator();
            for (Transaction t : transactions) {
                System.out.println(t.toShortString());
            }
        }
        ui.waitForEnter();
    }
    
    private void depositMoney(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        PersonalAccount account = selectAccount(accounts);
        if (account == null) return;
        
        ui.printSubHeader("Deposit Money");
        double amount = ui.readPositiveDouble("Enter amount to deposit (EUR)");
        
        try {
            bankSystem.getTransactionManager().deposit(account, new BigDecimal(amount), "Cash deposit");
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully deposited %.2f EUR. New balance: %.2f EUR", 
                amount, account.getBalance()));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void withdrawMoney(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        PersonalAccount account = selectAccount(accounts);
        if (account == null) return;
        
        ui.printSubHeader("Withdraw Money");
        System.out.printf("Available balance: %.2f EUR%n", account.getBalance());
        double amount = ui.readPositiveDouble("Enter amount to withdraw (EUR)");
        
        try {
            bankSystem.getTransactionManager().withdraw(account, new BigDecimal(amount), "Cash withdrawal");
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully withdrew %.2f EUR. New balance: %.2f EUR", 
                amount, account.getBalance()));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void transferMoney(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Transfer Money (Internal)");
        ui.printInfo("Select source account:");
        PersonalAccount fromAccount = selectAccount(accounts);
        if (fromAccount == null) return;
        
        System.out.printf("Available balance: %.2f EUR%n", fromAccount.getBalance());
        String toIban = ui.readString("Enter destination IBAN");
        
        Account toAccount = bankSystem.getAccountManager().findByIban(toIban);
        if (toAccount == null) {
            ui.printError("Destination account not found. For external transfers, use SEPA or SWIFT.");
            ui.waitForEnter();
            return;
        }
        
        double amount = ui.readPositiveDouble("Enter amount to transfer (EUR)");
        String description = ui.readString("Enter description (optional)");
        
        try {
            bankSystem.getTransactionManager().transfer(fromAccount, toAccount, 
                new BigDecimal(amount), description.isEmpty() ? null : description);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully transferred %.2f EUR to %s", amount, toIban));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void sepaTransfer(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("SEPA Transfer (European)");
        ui.printInfo("Fee: " + TransactionManager.getSepaFee() + " EUR");
        ui.printWarning("Note: SEPA transfers have a 75% success rate via external API");
        ui.printBlank();
        
        PersonalAccount fromAccount = selectAccount(accounts);
        if (fromAccount == null) return;
        
        System.out.printf("Available balance: %.2f EUR%n", fromAccount.getBalance());
        ui.printSeparator();
        
        // Creditor details
        ui.printInfo("Enter creditor (recipient) details:");
        String creditorName = ui.readString("Creditor name");
        String creditorIban = ui.readString("Creditor IBAN");
        
        // Creditor bank details
        ui.printBlank();
        ui.printInfo("Enter creditor's bank details:");
        String creditorBankBic = ui.readString("Bank BIC (e.g., ETHNGRAA)");
        String creditorBankName = ui.readString("Bank name (e.g., National Bank of Greece)");
        
        // Transfer details
        ui.printBlank();
        double amount = ui.readPositiveDouble("Amount to transfer (EUR)");
        String description = ui.readString("Description/Reference");
        
        // Charges
        ui.printBlank();
        ui.printInfo("Charge model: SHA (shared) or OUR (you pay all fees)");
        String charges = ui.readString("Charges (SHA/OUR)").toUpperCase();
        if (!charges.equals("SHA") && !charges.equals("OUR")) {
            charges = "SHA"; // Default
        }
        
        ui.printBlank();
        ui.printInfo("Processing SEPA transfer...");
        
        try {
            bankSystem.getTransactionManager().sepaTransferFull(
                fromAccount, creditorIban, new BigDecimal(amount), description,
                creditorName, creditorBankBic, creditorBankName, charges);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("SEPA transfer of %.2f EUR completed to %s", amount, creditorName));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void swiftTransfer(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("SWIFT Transfer (International)");
        ui.printInfo("Fee: " + TransactionManager.getSwiftFee() + " EUR");
        ui.printWarning("Note: SWIFT transfers have a 75% success rate via external API");
        ui.printBlank();
        
        PersonalAccount fromAccount = selectAccount(accounts);
        if (fromAccount == null) return;
        
        System.out.printf("Available balance: %.2f EUR%n", fromAccount.getBalance());
        ui.printSeparator();
        
        // Beneficiary details
        ui.printInfo("Enter beneficiary (recipient) details:");
        String beneficiaryName = ui.readString("Beneficiary name");
        String beneficiaryAddress = ui.readString("Beneficiary address");
        String beneficiaryAccount = ui.readString("Beneficiary account/IBAN");
        
        // Beneficiary bank details
        ui.printBlank();
        ui.printInfo("Enter beneficiary's bank details:");
        String bankName = ui.readString("Bank name (e.g., Barclays Bank PLC)");
        String swiftCode = ui.readString("SWIFT/BIC code (e.g., BARCGB22)");
        String bankCountry = ui.readString("Bank country (e.g., UK)");
        
        // Transfer details
        ui.printBlank();
        String currency = ui.readString("Currency (default: EUR)");
        if (currency.isEmpty()) currency = "EUR";
        double amount = ui.readPositiveDouble("Amount to transfer");
        String description = ui.readString("Description/Reference");
        
        // Charges
        ui.printBlank();
        ui.printInfo("Charge model: SHA (shared) or OUR (you pay all fees)");
        String chargingModel = ui.readString("Charges (SHA/OUR)").toUpperCase();
        if (!chargingModel.equals("SHA") && !chargingModel.equals("OUR")) {
            chargingModel = "SHA"; // Default
        }
        
        ui.printBlank();
        ui.printInfo("Processing SWIFT transfer...");
        
        try {
            bankSystem.getTransactionManager().swiftTransferFull(
                fromAccount, beneficiaryAccount, new BigDecimal(amount), description,
                currency, beneficiaryName, beneficiaryAddress, bankName,
                swiftCode, bankCountry, chargingModel);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("SWIFT transfer of %.2f %s completed to %s", amount, currency, beneficiaryName));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void viewBills(IndividualUser user) {
        ui.printSubHeader("Your Bills");
        List<Bill> bills = bankSystem.getBillManager().getBillsForUser(user);
        
        if (bills.isEmpty()) {
            ui.printInfo("You don't have any bills.");
        } else {
            for (Bill bill : bills) {
                System.out.println(bill.toString());
            }
        }
        ui.waitForEnter();
    }
    
    private void payBill(IndividualUser user) {
        List<Bill> unpaidBills = bankSystem.getBillManager().getUnpaidBillsForUser(user);
        
        if (unpaidBills.isEmpty()) {
            ui.printInfo("You don't have any unpaid bills.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Pay Bill");
        ui.printInfo("Unpaid bills:");
        for (int i = 0; i < unpaidBills.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, unpaidBills.get(i).toString());
        }
        
        int choice = ui.readIntInRange("Select bill to pay (0 to cancel)", 0, unpaidBills.size());
        if (choice == 0) return;
        
        Bill selectedBill = unpaidBills.get(choice - 1);
        
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        ui.printInfo("Select account to pay from:");
        PersonalAccount account = selectAccount(accounts);
        if (account == null) return;
        
        try {
            bankSystem.payBill(selectedBill, account);
            ui.printSuccess("Bill paid successfully!");
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void manageStandingOrders(Customer user) {
        ui.printSubHeader("Standing Orders");
        ui.printMenuOption(1, "View Standing Orders");
        ui.printMenuOption(2, "Create Transfer Standing Order");
        ui.printMenuOption(3, "Create Bill Payment Standing Order");
        ui.printMenuOption(4, "Cancel Standing Order");
        ui.printMenuOption(0, "Back");
        
        int choice = ui.readIntInRange("Select option", 0, 4);
        
        switch (choice) {
            case 1:
                viewStandingOrders(user);
                break;
            case 2:
                createTransferStandingOrder(user);
                break;
            case 3:
                createBillPaymentStandingOrder(user);
                break;
            case 4:
                cancelStandingOrder(user);
                break;
        }
    }
    
    private void viewStandingOrders(Customer user) {
        List<StandingOrder> orders = bankSystem.getStandingOrderManager().getStandingOrdersForCustomer(user);
        
        if (orders.isEmpty()) {
            ui.printInfo("You don't have any standing orders.");
        } else {
            for (StandingOrder order : orders) {
                System.out.println(order.toDetailedString());
                ui.printSeparator();
            }
        }
        ui.waitForEnter();
    }
    
    private void createTransferStandingOrder(Customer user) {
        List<Account> accounts = bankSystem.getAccountManager().getAccountsForCustomer(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Create Transfer Standing Order");
        
        ui.printInfo("Select source account:");
        Account sourceAccount = selectAccountGeneric(accounts);
        if (sourceAccount == null) return;
        
        String destIban = ui.readString("Enter destination IBAN");
        Account destAccount = bankSystem.getAccountManager().findByIban(destIban);
        if (destAccount == null) {
            ui.printError("Destination account not found.");
            ui.waitForEnter();
            return;
        }
        
        double amount = ui.readPositiveDouble("Enter amount (EUR)");
        int frequency = ui.readIntInRange("Frequency in months (1-12)", 1, 12);
        int day = ui.readIntInRange("Day of month to execute (1-28)", 1, 28);
        String description = ui.readString("Enter description");
        
        try {
            bankSystem.getStandingOrderManager().createTransferStandingOrder(
                sourceAccount, destAccount, new BigDecimal(amount), 
                frequency, day, description, user);
            bankSystem.saveToFile();
            ui.printSuccess("Standing order created successfully!");
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void createBillPaymentStandingOrder(Customer user) {
        List<Account> accounts = bankSystem.getAccountManager().getAccountsForCustomer(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Create Bill Payment Standing Order");
        
        ui.printInfo("Select source account:");
        Account sourceAccount = selectAccountGeneric(accounts);
        if (sourceAccount == null) return;
        
        String rfCode = ui.readString("Enter RF code to match");
        String provider = ui.readString("Enter provider name");
        
        try {
            bankSystem.getStandingOrderManager().createBillPaymentStandingOrder(
                sourceAccount, rfCode, provider, user);
            bankSystem.saveToFile();
            ui.printSuccess("Bill payment standing order created successfully!");
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void cancelStandingOrder(Customer user) {
        List<StandingOrder> orders = bankSystem.getStandingOrderManager().getStandingOrdersForCustomer(user);
        
        List<StandingOrder> activeOrders = new java.util.ArrayList<>();
        for (StandingOrder order : orders) {
            if (order.getStatus() == StandingOrder.OrderStatus.ACTIVE || 
                order.getStatus() == StandingOrder.OrderStatus.PAUSED) {
                activeOrders.add(order);
            }
        }
        
        if (activeOrders.isEmpty()) {
            ui.printInfo("You don't have any active standing orders to cancel.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Cancel Standing Order");
        for (int i = 0; i < activeOrders.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, activeOrders.get(i).toString());
        }
        
        int choice = ui.readIntInRange("Select order to cancel (0 to go back)", 0, activeOrders.size());
        if (choice == 0) return;
        
        StandingOrder selectedOrder = activeOrders.get(choice - 1);
        if (ui.readYesNo("Are you sure you want to cancel this standing order?")) {
            bankSystem.getStandingOrderManager().cancelOrder(selectedOrder);
            bankSystem.saveToFile();
            ui.printSuccess("Standing order cancelled.");
        }
        ui.waitForEnter();
    }
    
    private void manageCoOwners(IndividualUser user) {
        List<PersonalAccount> accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        // Filter to accounts where user is primary owner
        List<PersonalAccount> ownedAccounts = new java.util.ArrayList<>();
        for (PersonalAccount acc : accounts) {
            if (acc.isPrimaryOwner(user)) {
                ownedAccounts.add(acc);
            }
        }
        
        if (ownedAccounts.isEmpty()) {
            ui.printInfo("You don't have any accounts where you are the primary owner.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Manage Co-Owners");
        PersonalAccount account = selectAccount(ownedAccounts);
        if (account == null) return;
        
        ui.printInfo("Current co-owners:");
        List<IndividualUser> coOwners = account.getSecondaryOwners();
        if (coOwners.isEmpty()) {
            System.out.println("  No co-owners");
        } else {
            for (IndividualUser owner : coOwners) {
                System.out.println("  - " + owner.getFullName() + " (" + owner.getUsername() + ")");
            }
        }
        
        ui.printBlank();
        ui.printMenuOption(1, "Add Co-Owner");
        ui.printMenuOption(2, "Remove Co-Owner");
        ui.printMenuOption(0, "Back");
        
        int choice = ui.readIntInRange("Select option", 0, 2);
        
        switch (choice) {
            case 1:
                addCoOwner(account);
                break;
            case 2:
                removeCoOwner(account);
                break;
        }
    }
    
    private void addCoOwner(PersonalAccount account) {
        String username = ui.readString("Enter username of user to add as co-owner");
        User foundUser = bankSystem.getUserManager().findByUsername(username);
        
        if (foundUser == null) {
            ui.printError("User not found.");
        } else if (!(foundUser instanceof IndividualUser)) {
            ui.printError("Only individual users can be co-owners.");
        } else {
            account.addSecondaryOwner((IndividualUser) foundUser);
            bankSystem.saveToFile();
            ui.printSuccess("Co-owner added successfully!");
        }
        ui.waitForEnter();
    }
    
    private void removeCoOwner(PersonalAccount account) {
        List<IndividualUser> coOwners = account.getSecondaryOwners();
        
        if (coOwners.isEmpty()) {
            ui.printInfo("No co-owners to remove.");
            ui.waitForEnter();
            return;
        }
        
        for (int i = 0; i < coOwners.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, coOwners.get(i).getFullName());
        }
        
        int choice = ui.readIntInRange("Select co-owner to remove (0 to cancel)", 0, coOwners.size());
        if (choice == 0) return;
        
        account.removeSecondaryOwner(coOwners.get(choice - 1));
        bankSystem.saveToFile();
        ui.printSuccess("Co-owner removed successfully!");
        ui.waitForEnter();
    }
    
    // ==================== Business Customer Methods ====================
    
    private void viewBusinessAccountOverview(BusinessUser user) {
        ui.printSubHeader("Business Account Overview");
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any business accounts.");
        } else {
            for (BusinessAccount account : accounts) {
                System.out.printf("IBAN: %s%n", account.getIban());
                System.out.printf("Balance: %.2f EUR%n", account.getBalance());
                System.out.printf("Status: %s%n", account.getStatus());
                System.out.printf("Monthly Fee: %.2f EUR%n", account.getMonthlyMaintenanceFee());
                ui.printSeparator();
            }
        }
        ui.waitForEnter();
    }
    
    private void viewBusinessAccountStatements(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printInfo("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount account = accounts.get(choice - 1);
        ui.printSubHeader("Account Statements - " + account.getIban());
        
        List<Transaction> transactions = bankSystem.getTransactionManager().getTransactionsForAccount(account);
        if (transactions.isEmpty()) {
            ui.printInfo("No transactions found.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t.toShortString());
            }
        }
        ui.waitForEnter();
    }
    
    private void depositMoneyBusiness(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printInfo("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, accounts.get(i).getIban());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount account = accounts.get(choice - 1);
        double amount = ui.readPositiveDouble("Enter amount to deposit (EUR)");
        
        try {
            bankSystem.getTransactionManager().deposit(account, new BigDecimal(amount), "Business deposit");
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully deposited %.2f EUR", amount));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void withdrawMoneyBusiness(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printInfo("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount account = accounts.get(choice - 1);
        double amount = ui.readPositiveDouble("Enter amount to withdraw (EUR)");
        
        try {
            bankSystem.getTransactionManager().withdraw(account, new BigDecimal(amount), "Business withdrawal");
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully withdrew %.2f EUR", amount));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void transferMoneyBusiness(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("Transfer Money");
        ui.printInfo("Select source account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount fromAccount = accounts.get(choice - 1);
        String toIban = ui.readString("Enter destination IBAN");
        
        Account toAccount = bankSystem.getAccountManager().findByIban(toIban);
        if (toAccount == null) {
            ui.printError("Destination account not found.");
            ui.waitForEnter();
            return;
        }
        
        double amount = ui.readPositiveDouble("Enter amount to transfer (EUR)");
        String description = ui.readString("Enter description");
        
        try {
            bankSystem.getTransactionManager().transfer(fromAccount, toAccount, 
                new BigDecimal(amount), description);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("Successfully transferred %.2f EUR", amount));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void sepaTransferBusiness(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("SEPA Transfer (Business)");
        ui.printInfo("Fee: " + TransactionManager.getSepaFee() + " EUR");
        ui.printWarning("Note: SEPA transfers have a 75% success rate via external API");
        ui.printBlank();
        
        ui.printInfo("Select source account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount fromAccount = accounts.get(choice - 1);
        ui.printSeparator();
        
        // Creditor details
        ui.printInfo("Enter creditor (recipient) details:");
        String creditorName = ui.readString("Creditor name");
        String creditorIban = ui.readString("Creditor IBAN");
        
        // Creditor bank details
        ui.printBlank();
        ui.printInfo("Enter creditor's bank details:");
        String creditorBankBic = ui.readString("Bank BIC (e.g., ETHNGRAA)");
        String creditorBankName = ui.readString("Bank name");
        
        // Transfer details
        ui.printBlank();
        double amount = ui.readPositiveDouble("Amount (EUR)");
        String description = ui.readString("Description/Reference");
        String charges = ui.readString("Charges SHA/OUR (default: SHA)").toUpperCase();
        if (!charges.equals("SHA") && !charges.equals("OUR")) charges = "SHA";
        
        ui.printBlank();
        ui.printInfo("Processing SEPA transfer...");
        
        try {
            bankSystem.getTransactionManager().sepaTransferFull(
                fromAccount, creditorIban, new BigDecimal(amount), description,
                creditorName, creditorBankBic, creditorBankName, charges);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("SEPA transfer of %.2f EUR completed to %s", amount, creditorName));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void swiftTransferBusiness(BusinessUser user) {
        List<BusinessAccount> accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            ui.printInfo("You don't have any accounts.");
            ui.waitForEnter();
            return;
        }
        
        ui.printSubHeader("SWIFT Transfer (Business)");
        ui.printInfo("Fee: " + TransactionManager.getSwiftFee() + " EUR");
        ui.printWarning("Note: SWIFT transfers have a 75% success rate via external API");
        ui.printBlank();
        
        ui.printInfo("Select source account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return;
        
        BusinessAccount fromAccount = accounts.get(choice - 1);
        ui.printSeparator();
        
        // Beneficiary details
        ui.printInfo("Enter beneficiary details:");
        String beneficiaryName = ui.readString("Beneficiary name");
        String beneficiaryAddress = ui.readString("Beneficiary address");
        String beneficiaryAccount = ui.readString("Beneficiary account/IBAN");
        
        // Bank details
        ui.printBlank();
        ui.printInfo("Enter beneficiary's bank details:");
        String bankName = ui.readString("Bank name");
        String swiftCode = ui.readString("SWIFT/BIC code");
        String bankCountry = ui.readString("Bank country");
        
        // Transfer details
        ui.printBlank();
        String currency = ui.readString("Currency (default: EUR)");
        if (currency.isEmpty()) currency = "EUR";
        double amount = ui.readPositiveDouble("Amount");
        String description = ui.readString("Description/Reference");
        String chargingModel = ui.readString("Charges SHA/OUR (default: SHA)").toUpperCase();
        if (!chargingModel.equals("SHA") && !chargingModel.equals("OUR")) chargingModel = "SHA";
        
        ui.printBlank();
        ui.printInfo("Processing SWIFT transfer...");
        
        try {
            bankSystem.getTransactionManager().swiftTransferFull(
                fromAccount, beneficiaryAccount, new BigDecimal(amount), description,
                currency, beneficiaryName, beneficiaryAddress, bankName,
                swiftCode, bankCountry, chargingModel);
            bankSystem.saveToFile();
            ui.printSuccess(String.format("SWIFT transfer of %.2f %s completed to %s", amount, currency, beneficiaryName));
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void issueBill(BusinessUser issuer) {
        ui.printSubHeader("Issue Bill to Customer");
        
        String customerUsername = ui.readString("Enter customer username");
        User customer = bankSystem.getUserManager().findByUsername(customerUsername);
        
        if (customer == null || !(customer instanceof IndividualUser)) {
            ui.printError("Customer not found or is not an individual user.");
            ui.waitForEnter();
            return;
        }
        
        double amount = ui.readPositiveDouble("Enter bill amount (EUR)");
        String dueDateStr = ui.readString("Enter due date (dd/MM/yyyy)");
        
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            ui.printError("Invalid date format.");
            ui.waitForEnter();
            return;
        }
        
        String rfCode = ui.readString("Enter RF code (leave empty for auto-generate)");
        
        try {
            Bill bill;
            if (rfCode.isEmpty()) {
                bill = bankSystem.getBillManager().createBill(
                    (IndividualUser) customer, issuer, issuer.getBusinessName(), 
                    new BigDecimal(amount), dueDate);
            } else {
                bill = bankSystem.getBillManager().createBill(
                    (IndividualUser) customer, issuer, issuer.getBusinessName(), 
                    new BigDecimal(amount), dueDate, rfCode);
            }
            bankSystem.saveToFile();
            ui.printSuccess("Bill created successfully!");
            System.out.println("Bill ID: " + bill.getId());
            System.out.println("RF Code: " + bill.getRfCode());
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void viewIssuedBills(BusinessUser user) {
        ui.printSubHeader("Issued Bills");
        List<Bill> bills = bankSystem.getBillManager().getBillsIssuedByBusiness(user);
        
        if (bills.isEmpty()) {
            ui.printInfo("You haven't issued any bills.");
        } else {
            for (Bill bill : bills) {
                System.out.println(bill.toString());
                System.out.println("  Customer: " + bill.getOwner().getFullName());
                ui.printSeparator();
            }
        }
        ui.waitForEnter();
    }
    
    private void manageStandingOrdersBusiness(BusinessUser user) {
        manageStandingOrders(user);
    }
    
    // ==================== Admin Methods ====================
    
    private void viewAllUsers() {
        ui.printSubHeader("All Users");
        List<User> users = bankSystem.getUserManager().getAllUsers();
        
        for (User user : users) {
            System.out.printf("%-10s | %-15s | %-10s | Locked: %s%n",
                user.getId(), user.getUsername(), user.getRole(), user.isLocked());
        }
        ui.waitForEnter();
    }
    
    private void createNewUser() {
        ui.printSubHeader("Create New User");
        ui.printMenuOption(1, "Individual User");
        ui.printMenuOption(2, "Business User");
        ui.printMenuOption(3, "Admin User");
        ui.printMenuOption(0, "Cancel");
        
        int choice = ui.readIntInRange("Select user type", 0, 3);
        
        switch (choice) {
            case 1:
                createIndividualUser();
                break;
            case 2:
                createBusinessUser();
                break;
            case 3:
                createAdminUser();
                break;
        }
    }
    
    private void createIndividualUser() {
        String username = ui.readString("Username");
        String password = ui.readPassword("Password");
        String fullName = ui.readString("Full Name");
        String address = ui.readString("Address");
        String phone = ui.readString("Phone Number");
        String vat = ui.readString("VAT Number");
        
        try {
            IndividualUser user = bankSystem.getUserManager().registerIndividualUser(
                username, password, fullName, address, phone, vat);
            bankSystem.saveToFile();
            ui.printSuccess("User created with ID: " + user.getId());
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void createBusinessUser() {
        String username = ui.readString("Username");
        String password = ui.readPassword("Password");
        String businessName = ui.readString("Business Name");
        String phone = ui.readString("Phone Number");
        String vat = ui.readString("VAT Number");
        
        try {
            BusinessUser user = bankSystem.getUserManager().registerBusinessUser(
                username, password, businessName, phone, vat);
            bankSystem.saveToFile();
            ui.printSuccess("User created with ID: " + user.getId());
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void createAdminUser() {
        String username = ui.readString("Username");
        String password = ui.readPassword("Password");
        String phone = ui.readString("Phone Number");
        int level = ui.readIntInRange("Admin Level (1-3)", 1, 3);
        
        try {
            AdminUser user = bankSystem.getUserManager().registerAdminUser(
                username, password, phone, level);
            bankSystem.saveToFile();
            ui.printSuccess("Admin created with ID: " + user.getId());
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void blockUnblockUser() {
        ui.printSubHeader("Block/Unblock User");
        String username = ui.readString("Enter username");
        User user = bankSystem.getUserManager().findByUsername(username);
        
        if (user == null) {
            ui.printError("User not found.");
            ui.waitForEnter();
            return;
        }
        
        System.out.printf("User: %s (%s) - Currently %s%n", 
            user.getUsername(), user.getRole(), user.isLocked() ? "LOCKED" : "ACTIVE");
        
        if (user.isLocked()) {
            if (ui.readYesNo("Unblock this user?")) {
                bankSystem.getUserManager().unlockUser(user);
                bankSystem.saveToFile();
                ui.printSuccess("User unblocked.");
            }
        } else {
            if (ui.readYesNo("Block this user?")) {
                bankSystem.getUserManager().lockUser(user);
                bankSystem.saveToFile();
                ui.printSuccess("User blocked.");
            }
        }
        ui.waitForEnter();
    }
    
    private void viewAllAccounts() {
        ui.printSubHeader("All Accounts");
        List<Account> accounts = bankSystem.getAccountManager().getAllAccounts();
        
        for (Account account : accounts) {
            System.out.printf("%-22s | %.2f EUR | %s | %s%n",
                account.getIban(), account.getBalance(), 
                account.getAccountType(), account.getStatus());
        }
        ui.waitForEnter();
    }
    
    private void viewAccountDetails() {
        ui.printSubHeader("Account Details");
        String iban = ui.readString("Enter IBAN");
        Account account = bankSystem.getAccountManager().findByIban(iban);
        
        if (account == null) {
            ui.printError("Account not found.");
            ui.waitForEnter();
            return;
        }
        
        System.out.println("IBAN: " + account.getIban());
        System.out.println("Type: " + account.getAccountType());
        System.out.printf("Balance: %.2f EUR%n", account.getBalance());
        System.out.println("Status: " + account.getStatus());
        System.out.println("Owner: " + account.getPrimaryOwner());
        
        if (account instanceof PersonalAccount) {
            PersonalAccount pa = (PersonalAccount) account;
            List<IndividualUser> coOwners = pa.getSecondaryOwners();
            if (!coOwners.isEmpty()) {
                System.out.println("Co-owners:");
                for (IndividualUser owner : coOwners) {
                    System.out.println("  - " + owner.getFullName());
                }
            }
        }
        
        ui.printSeparator();
        ui.printInfo("Recent Transactions:");
        List<Transaction> transactions = bankSystem.getTransactionManager().getRecentTransactions(account, 10);
        for (Transaction t : transactions) {
            System.out.println(t.toShortString());
        }
        
        ui.waitForEnter();
    }
    
    private void createAccountForUser() {
        ui.printSubHeader("Create Account for User");
        String username = ui.readString("Enter username");
        User user = bankSystem.getUserManager().findByUsername(username);
        
        if (user == null) {
            ui.printError("User not found.");
            ui.waitForEnter();
            return;
        }
        
        if (user instanceof IndividualUser) {
            double balance = ui.readDouble("Initial balance (EUR)");
            PersonalAccount account = bankSystem.getAccountManager().createPersonalAccount(
                (IndividualUser) user, new BigDecimal(balance));
            bankSystem.saveToFile();
            ui.printSuccess("Account created: " + account.getIban());
        } else if (user instanceof BusinessUser) {
            double balance = ui.readDouble("Initial balance (EUR)");
            double fee = ui.readDouble("Monthly maintenance fee (EUR)");
            BusinessAccount account = bankSystem.getAccountManager().createBusinessAccount(
                (BusinessUser) user, new BigDecimal(balance), new BigDecimal(fee));
            bankSystem.saveToFile();
            ui.printSuccess("Account created: " + account.getIban());
        } else {
            ui.printError("Cannot create accounts for admin users.");
        }
        ui.waitForEnter();
    }
    
    private void viewAllStandingOrders() {
        ui.printSubHeader("All Standing Orders");
        List<StandingOrder> orders = bankSystem.getStandingOrderManager().getAllStandingOrders();
        
        if (orders.isEmpty()) {
            ui.printInfo("No standing orders in the system.");
        } else {
            for (StandingOrder order : orders) {
                System.out.println(order.toString());
            }
        }
        ui.waitForEnter();
    }
    
    private void viewAllBills() {
        ui.printSubHeader("All Bills");
        List<Bill> bills = bankSystem.getBillManager().getAllBills();
        
        if (bills.isEmpty()) {
            ui.printInfo("No bills in the system.");
        } else {
            for (Bill bill : bills) {
                System.out.println(bill.toString());
            }
        }
        ui.waitForEnter();
    }
    
    private void simulateTimePassing() {
        ui.printSubHeader("Simulate Time Passing");
        ui.printInfo("Current system date: " + bankSystem.getCurrentDate());
        
        String targetDateStr = ui.readString("Enter target date (dd/MM/yyyy)");
        
        try {
            LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            if (targetDate.isBefore(bankSystem.getCurrentDate())) {
                ui.printError("Cannot simulate backwards in time.");
                ui.waitForEnter();
                return;
            }
            
            if (ui.readYesNo("Simulate time passing to " + targetDate + "?")) {
                bankSystem.simulateTimePassing(targetDate);
                ui.printSuccess("Time simulation complete. New date: " + bankSystem.getCurrentDate());
            }
        } catch (DateTimeParseException e) {
            ui.printError("Invalid date format. Use dd/MM/yyyy");
        }
        ui.waitForEnter();
    }
    
    // ==================== Common Methods ====================
    
    private void changePassword() {
        ui.printSubHeader("Change Password");
        String oldPassword = ui.readPassword("Current password");
        String newPassword = ui.readPassword("New password");
        String confirmPassword = ui.readPassword("Confirm new password");
        
        if (!newPassword.equals(confirmPassword)) {
            ui.printError("Passwords do not match.");
            ui.waitForEnter();
            return;
        }
        
        try {
            User user = bankSystem.getAuthManager().getCurrentUser();
            user.changePassword(oldPassword, newPassword);
            bankSystem.saveToFile();
            ui.printSuccess("Password changed successfully!");
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        ui.waitForEnter();
    }
    
    private void logout() {
        bankSystem.getAuthManager().logout();
        ui.printInfo("You have been logged out.");
    }
    
    // ==================== Helper Methods ====================
    
    private PersonalAccount selectAccount(List<PersonalAccount> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return null;
        return accounts.get(choice - 1);
    }
    
    private Account selectAccountGeneric(List<Account> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("[%d] %s (%.2f EUR)%n", i + 1, 
                accounts.get(i).getIban(), accounts.get(i).getBalance());
        }
        
        int choice = ui.readIntInRange("Select account (0 to cancel)", 0, accounts.size());
        if (choice == 0) return null;
        return accounts.get(choice - 1);
    }
}
