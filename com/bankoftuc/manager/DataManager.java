package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages data persistence using CSV files.
 * Each entity type is stored in a separate CSV file.
 */
public class DataManager {
    
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String BILLS_FILE = DATA_DIR + "/bills.csv";
    private static final String STANDING_ORDERS_FILE = DATA_DIR + "/standing_orders.csv";
    private static final String SYSTEM_FILE = DATA_DIR + "/system.csv";
    private static final String CO_OWNERS_FILE = DATA_DIR + "/co_owners.csv";
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Ensure data directory exists
     */
    public static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Check if data files exist
     */
    public static boolean dataExists() {
        return new File(USERS_FILE).exists();
    }
    
    /**
     * Delete all data files
     */
    public static void deleteAllData() {
        new File(USERS_FILE).delete();
        new File(ACCOUNTS_FILE).delete();
        new File(TRANSACTIONS_FILE).delete();
        new File(BILLS_FILE).delete();
        new File(STANDING_ORDERS_FILE).delete();
        new File(SYSTEM_FILE).delete();
        new File(CO_OWNERS_FILE).delete();
    }
    
    // ==================== SAVE METHODS ====================
    
    /**
     * Save all users to CSV
     */
    public static void saveUsers(List<User> users) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            // Header
            writer.println("type,id,username,passwordHash,phoneNumber,failedLoginAttempts,locked,fullName,address,vatNumber,businessName,adminLevel");
            
            for (User user : users) {
                StringBuilder sb = new StringBuilder();
                
                if (user instanceof IndividualUser) {
                    IndividualUser iu = (IndividualUser) user;
                    sb.append("INDIVIDUAL,");
                    sb.append(escapeCSV(iu.getId())).append(",");
                    sb.append(escapeCSV(iu.getUsername())).append(",");
                    sb.append(escapeCSV(getPasswordHash(iu))).append(",");
                    sb.append(escapeCSV(iu.getPhoneNumber())).append(",");
                    sb.append(iu.getFailedLoginAttempts()).append(",");
                    sb.append(iu.isLocked()).append(",");
                    sb.append(escapeCSV(iu.getFullName())).append(",");
                    sb.append(escapeCSV(iu.getAddress())).append(",");
                    sb.append(escapeCSV(iu.getVatNumber())).append(",");
                    sb.append(","); // businessName empty
                    sb.append(""); // adminLevel empty
                } else if (user instanceof BusinessUser) {
                    BusinessUser bu = (BusinessUser) user;
                    sb.append("BUSINESS,");
                    sb.append(escapeCSV(bu.getId())).append(",");
                    sb.append(escapeCSV(bu.getUsername())).append(",");
                    sb.append(escapeCSV(getPasswordHash(bu))).append(",");
                    sb.append(escapeCSV(bu.getPhoneNumber())).append(",");
                    sb.append(bu.getFailedLoginAttempts()).append(",");
                    sb.append(bu.isLocked()).append(",");
                    sb.append(","); // fullName empty
                    sb.append(","); // address empty
                    sb.append(escapeCSV(bu.getVatNumber())).append(",");
                    sb.append(escapeCSV(bu.getBusinessName())).append(",");
                    sb.append(""); // adminLevel empty
                } else if (user instanceof AdminUser) {
                    AdminUser au = (AdminUser) user;
                    sb.append("ADMIN,");
                    sb.append(escapeCSV(au.getId())).append(",");
                    sb.append(escapeCSV(au.getUsername())).append(",");
                    sb.append(escapeCSV(getPasswordHash(au))).append(",");
                    sb.append(escapeCSV(au.getPhoneNumber())).append(",");
                    sb.append(au.getFailedLoginAttempts()).append(",");
                    sb.append(au.isLocked()).append(",");
                    sb.append(","); // fullName empty
                    sb.append(","); // address empty
                    sb.append(","); // vatNumber empty
                    sb.append(","); // businessName empty
                    sb.append(au.getAdminLevel());
                }
                
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Save all accounts to CSV
     */
    public static void saveAccounts(List<Account> accounts) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            // Header
            writer.println("type,iban,balance,status,interestRate,accruedInterest,ownerUsername,monthlyMaintenanceFee");
            
            for (Account account : accounts) {
                StringBuilder sb = new StringBuilder();
                
                if (account instanceof PersonalAccount) {
                    PersonalAccount pa = (PersonalAccount) account;
                    sb.append("PERSONAL,");
                    sb.append(escapeCSV(pa.getIban())).append(",");
                    sb.append(pa.getBalance()).append(",");
                    sb.append(pa.getStatus()).append(",");
                    sb.append(pa.getInterestRate()).append(",");
                    sb.append(pa.getAccruedInterest()).append(",");
                    sb.append(escapeCSV(pa.getPrimaryOwner().getUsername())).append(",");
                    sb.append(""); // no maintenance fee
                } else if (account instanceof BusinessAccount) {
                    BusinessAccount ba = (BusinessAccount) account;
                    sb.append("BUSINESS,");
                    sb.append(escapeCSV(ba.getIban())).append(",");
                    sb.append(ba.getBalance()).append(",");
                    sb.append(ba.getStatus()).append(",");
                    sb.append(ba.getInterestRate()).append(",");
                    sb.append(ba.getAccruedInterest()).append(",");
                    sb.append(escapeCSV(ba.getOwner().getUsername())).append(",");
                    sb.append(ba.getMonthlyMaintenanceFee());
                }
                
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }
    
    /**
     * Save co-owners relationships to CSV
     */
    public static void saveCoOwners(List<Account> accounts) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CO_OWNERS_FILE))) {
            // Header
            writer.println("accountIban,coOwnerUsername");
            
            for (Account account : accounts) {
                if (account instanceof PersonalAccount) {
                    PersonalAccount pa = (PersonalAccount) account;
                    for (IndividualUser coOwner : pa.getSecondaryOwners()) {
                        writer.println(escapeCSV(pa.getIban()) + "," + escapeCSV(coOwner.getUsername()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving co-owners: " + e.getMessage());
        }
    }
    
    /**
     * Save all transactions to CSV
     */
    public static void saveTransactions(List<Transaction> transactions) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            // Header
            writer.println("id,dateTime,amount,type,description,balanceAfter,fromAccountIban,toAccountIban,status");
            
            for (Transaction t : transactions) {
                StringBuilder sb = new StringBuilder();
                sb.append(t.getId()).append(",");
                sb.append(t.getDateTime().format(DATETIME_FORMAT)).append(",");
                sb.append(t.getAmount()).append(",");
                sb.append(t.getType()).append(",");
                sb.append(escapeCSV(t.getDescription())).append(",");
                sb.append(t.getBalanceAfter()).append(",");
                sb.append(t.getFromAccount() != null ? escapeCSV(t.getFromAccount().getIban()) : "").append(",");
                sb.append(t.getToAccount() != null ? escapeCSV(t.getToAccount().getIban()) : "").append(",");
                sb.append(t.getStatus());
                
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
    
    /**
     * Save all bills to CSV
     */
    public static void saveBills(List<Bill> bills) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(BILLS_FILE))) {
            // Header
            writer.println("id,providerName,amount,dueDate,status,paidDate,rfCode,ownerUsername,issuerUsername");
            
            for (Bill bill : bills) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCSV(bill.getId())).append(",");
                sb.append(escapeCSV(bill.getProviderName())).append(",");
                sb.append(bill.getAmount()).append(",");
                sb.append(bill.getDueDate().format(DATE_FORMAT)).append(",");
                sb.append(bill.getStatus()).append(",");
                sb.append(bill.getPaidDate() != null ? bill.getPaidDate().format(DATETIME_FORMAT) : "").append(",");
                sb.append(escapeCSV(bill.getRfCode())).append(",");
                sb.append(escapeCSV(bill.getOwner().getUsername())).append(",");
                sb.append(escapeCSV(bill.getIssuer().getUsername()));
                
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving bills: " + e.getMessage());
        }
    }
    
    /**
     * Save all standing orders to CSV
     */
    public static void saveStandingOrders(List<StandingOrder> orders) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(STANDING_ORDERS_FILE))) {
            // Header
            writer.println("id,type,amount,frequencyMonths,executionDay,nextExecutionDate,status,sourceAccountIban,destinationAccountIban,rfCode,providerName,description,ownerUsername");
            
            for (StandingOrder order : orders) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCSV(order.getId())).append(",");
                sb.append(order.getType()).append(",");
                sb.append(order.getAmount() != null ? order.getAmount() : "").append(",");
                sb.append(order.getFrequencyMonths()).append(",");
                sb.append(order.getExecutionDay()).append(",");
                sb.append(order.getNextExecutionDate() != null ? order.getNextExecutionDate().format(DATE_FORMAT) : "").append(",");
                sb.append(order.getStatus()).append(",");
                sb.append(order.getSourceAccount() != null ? escapeCSV(order.getSourceAccount().getIban()) : "").append(",");
                sb.append(order.getDestinationAccount() != null ? escapeCSV(order.getDestinationAccount().getIban()) : "").append(",");
                sb.append(order.getRfCode() != null ? escapeCSV(order.getRfCode()) : "").append(",");
                sb.append(order.getProviderName() != null ? escapeCSV(order.getProviderName()) : "").append(",");
                sb.append(order.getDescription() != null ? escapeCSV(order.getDescription()) : "").append(",");
                sb.append(escapeCSV(order.getOwner().getUsername()));
                
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving standing orders: " + e.getMessage());
        }
    }
    
    /**
     * Save system state (current date, counters, etc.)
     */
    public static void saveSystemState(LocalDate currentDate) {
        ensureDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(SYSTEM_FILE))) {
            writer.println("key,value");
            writer.println("currentDate," + currentDate.format(DATE_FORMAT));
        } catch (IOException e) {
            System.err.println("Error saving system state: " + e.getMessage());
        }
    }
    
    // ==================== LOAD METHODS ====================
    
    /**
     * Load all users from CSV
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 7) continue;
                
                String type = parts[0];
                String id = parts[1];
                String username = parts[2];
                String passwordHash = parts[3];
                String phoneNumber = parts[4];
                int failedAttempts = Integer.parseInt(parts[5]);
                boolean locked = Boolean.parseBoolean(parts[6]);
                
                User user = null;
                
                switch (type) {
                    case "INDIVIDUAL":
                        String fullName = parts.length > 7 ? parts[7] : "";
                        String address = parts.length > 8 ? parts[8] : "";
                        String vatNumber = parts.length > 9 ? parts[9] : "";
                        IndividualUser iu = new IndividualUser(id, username, "temp", fullName, address, phoneNumber, vatNumber);
                        setPasswordHashDirectly(iu, passwordHash);
                        user = iu;
                        break;
                    case "BUSINESS":
                        String bVatNumber = parts.length > 9 ? parts[9] : "";
                        String businessName = parts.length > 10 ? parts[10] : "";
                        BusinessUser bu = new BusinessUser(id, username, "temp", businessName, phoneNumber, bVatNumber);
                        setPasswordHashDirectly(bu, passwordHash);
                        user = bu;
                        break;
                    case "ADMIN":
                        int adminLevel = parts.length > 11 && !parts[11].isEmpty() ? Integer.parseInt(parts[11]) : 1;
                        AdminUser au = new AdminUser(id, username, "temp", phoneNumber, adminLevel);
                        setPasswordHashDirectly(au, passwordHash);
                        user = au;
                        break;
                }
                
                if (user != null) {
                    user.setLocked(locked);
                    for (int i = 0; i < failedAttempts; i++) {
                        user.recordFailedLogin();
                    }
                    if (!locked) user.resetFailedAttempts();
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Load all accounts from CSV
     */
    public static List<Account> loadAccounts(List<User> users) {
        List<Account> accounts = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) return accounts;
        
        Map<String, User> userMap = new HashMap<>();
        for (User u : users) {
            userMap.put(u.getUsername(), u);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 7) continue;
                
                String type = parts[0];
                String iban = parts[1];
                BigDecimal balance = new BigDecimal(parts[2]);
                Account.AccountStatus status = Account.AccountStatus.valueOf(parts[3]);
                BigDecimal interestRate = new BigDecimal(parts[4]);
                BigDecimal accruedInterest = new BigDecimal(parts[5]);
                String ownerUsername = parts[6];
                
                User owner = userMap.get(ownerUsername);
                if (owner == null) continue;
                
                Account account = null;
                
                if (type.equals("PERSONAL") && owner instanceof IndividualUser) {
                    PersonalAccount pa = new PersonalAccount(iban, balance, (IndividualUser) owner);
                    pa.setStatus(status);
                    pa.setInterestRate(interestRate);
                    account = pa;
                } else if (type.equals("BUSINESS") && owner instanceof BusinessUser) {
                    BigDecimal fee = parts.length > 7 && !parts[7].isEmpty() ? new BigDecimal(parts[7]) : new BigDecimal("25.00");
                    BusinessAccount ba = new BusinessAccount(iban, balance, (BusinessUser) owner, fee);
                    ba.setStatus(status);
                    ba.setInterestRate(interestRate);
                    account = ba;
                }
                
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        
        // Load co-owners
        loadCoOwners(accounts, users);
        
        return accounts;
    }
    
    /**
     * Load co-owners relationships from CSV
     */
    private static void loadCoOwners(List<Account> accounts, List<User> users) {
        File file = new File(CO_OWNERS_FILE);
        if (!file.exists()) return;
        
        Map<String, Account> accountMap = new HashMap<>();
        for (Account a : accounts) {
            accountMap.put(a.getIban(), a);
        }
        
        Map<String, IndividualUser> individualMap = new HashMap<>();
        for (User u : users) {
            if (u instanceof IndividualUser) {
                individualMap.put(u.getUsername(), (IndividualUser) u);
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 2) continue;
                
                String iban = parts[0];
                String coOwnerUsername = parts[1];
                
                Account account = accountMap.get(iban);
                IndividualUser coOwner = individualMap.get(coOwnerUsername);
                
                if (account instanceof PersonalAccount && coOwner != null) {
                    ((PersonalAccount) account).addSecondaryOwner(coOwner);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading co-owners: " + e.getMessage());
        }
    }
    
    /**
     * Load all transactions from CSV
     */
    public static List<Transaction> loadTransactions(List<Account> accounts) {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return transactions;
        
        Map<String, Account> accountMap = new HashMap<>();
        for (Account a : accounts) {
            accountMap.put(a.getIban(), a);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 9) continue;
                
                long id = Long.parseLong(parts[0]);
                LocalDateTime dateTime = LocalDateTime.parse(parts[1], DATETIME_FORMAT);
                BigDecimal amount = new BigDecimal(parts[2]);
                Transaction.TransactionType type = Transaction.TransactionType.valueOf(parts[3]);
                String description = parts[4];
                BigDecimal balanceAfter = new BigDecimal(parts[5]);
                Account fromAccount = !parts[6].isEmpty() ? accountMap.get(parts[6]) : null;
                Account toAccount = !parts[7].isEmpty() ? accountMap.get(parts[7]) : null;
                Transaction.TransactionStatus status = Transaction.TransactionStatus.valueOf(parts[8]);
                
                Transaction t = new Transaction(id, fromAccount, toAccount, amount, type, description);
                t.setDateTime(dateTime);
                t.setBalanceAfter(balanceAfter);
                t.setStatus(status);
                
                transactions.add(t);
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Load all bills from CSV
     */
    public static List<Bill> loadBills(List<User> users) {
        List<Bill> bills = new ArrayList<>();
        File file = new File(BILLS_FILE);
        if (!file.exists()) return bills;
        
        Map<String, IndividualUser> individualMap = new HashMap<>();
        Map<String, BusinessUser> businessMap = new HashMap<>();
        for (User u : users) {
            if (u instanceof IndividualUser) {
                individualMap.put(u.getUsername(), (IndividualUser) u);
            } else if (u instanceof BusinessUser) {
                businessMap.put(u.getUsername(), (BusinessUser) u);
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 9) continue;
                
                String id = parts[0];
                String providerName = parts[1];
                BigDecimal amount = new BigDecimal(parts[2]);
                LocalDate dueDate = LocalDate.parse(parts[3], DATE_FORMAT);
                Bill.BillStatus status = Bill.BillStatus.valueOf(parts[4]);
                LocalDateTime paidDate = !parts[5].isEmpty() ? LocalDateTime.parse(parts[5], DATETIME_FORMAT) : null;
                String rfCode = parts[6];
                IndividualUser owner = individualMap.get(parts[7]);
                BusinessUser issuer = businessMap.get(parts[8]);
                
                if (owner == null || issuer == null) continue;
                
                Bill bill = new Bill(id, providerName, amount, dueDate, rfCode, owner, issuer);
                bill.setStatus(status);
                if (paidDate != null) {
                    bill.setPaidDate(paidDate);
                }
                
                bills.add(bill);
            }
        } catch (IOException e) {
            System.err.println("Error loading bills: " + e.getMessage());
        }
        
        return bills;
    }
    
    /**
     * Load all standing orders from CSV
     */
    public static List<StandingOrder> loadStandingOrders(List<Account> accounts, List<User> users) {
        List<StandingOrder> orders = new ArrayList<>();
        File file = new File(STANDING_ORDERS_FILE);
        if (!file.exists()) return orders;
        
        Map<String, Account> accountMap = new HashMap<>();
        for (Account a : accounts) {
            accountMap.put(a.getIban(), a);
        }
        
        Map<String, Customer> customerMap = new HashMap<>();
        for (User u : users) {
            if (u instanceof Customer) {
                customerMap.put(u.getUsername(), (Customer) u);
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length < 13) continue;
                
                String id = parts[0];
                StandingOrder.OrderType type = StandingOrder.OrderType.valueOf(parts[1]);
                BigDecimal amount = !parts[2].isEmpty() ? new BigDecimal(parts[2]) : null;
                int frequencyMonths = !parts[3].isEmpty() ? Integer.parseInt(parts[3]) : 0;
                int executionDay = !parts[4].isEmpty() ? Integer.parseInt(parts[4]) : 0;
                LocalDate nextExecutionDate = !parts[5].isEmpty() ? LocalDate.parse(parts[5], DATE_FORMAT) : null;
                StandingOrder.OrderStatus status = StandingOrder.OrderStatus.valueOf(parts[6]);
                Account sourceAccount = !parts[7].isEmpty() ? accountMap.get(parts[7]) : null;
                Account destAccount = !parts[8].isEmpty() ? accountMap.get(parts[8]) : null;
                String rfCode = parts[9];
                String providerName = parts[10];
                String description = parts[11];
                Customer owner = customerMap.get(parts[12]);
                
                if (sourceAccount == null || owner == null) continue;
                
                StandingOrder order;
                if (type == StandingOrder.OrderType.TRANSFER) {
                    order = new StandingOrder(id, sourceAccount, destAccount, amount, frequencyMonths, executionDay, description, owner);
                } else {
                    order = new StandingOrder(id, sourceAccount, rfCode, providerName, owner);
                }
                order.setStatus(status);
                if (nextExecutionDate != null) {
                    order.setNextExecutionDate(nextExecutionDate);
                }
                
                orders.add(order);
            }
        } catch (IOException e) {
            System.err.println("Error loading standing orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Load system state from CSV
     */
    public static LocalDate loadSystemDate() {
        File file = new File(SYSTEM_FILE);
        if (!file.exists()) return LocalDate.now();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length >= 2 && parts[0].equals("currentDate")) {
                    return LocalDate.parse(parts[1], DATE_FORMAT);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading system state: " + e.getMessage());
        }
        
        return LocalDate.now();
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Escape a value for CSV (handle commas, quotes, newlines)
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Parse a CSV line handling quoted values
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++; // Skip next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    result.add(current.toString());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    /**
     * Get password hash from user using reflection
     */
    private static String getPasswordHash(User user) {
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("passwordHash");
            field.setAccessible(true);
            return (String) field.get(user);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Set password hash directly using reflection
     */
    private static void setPasswordHashDirectly(User user, String hash) {
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("passwordHash");
            field.setAccessible(true);
            field.set(user, hash);
        } catch (Exception e) {
            System.err.println("Error setting password hash: " + e.getMessage());
        }
    }
}
