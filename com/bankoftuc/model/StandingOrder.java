package com.bankoftuc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a standing order (recurring payment or transfer).
 * Can be either a Transfer Standing Order or a Payment Standing Order (for bills).
 */
public class StandingOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum OrderType {
        TRANSFER,       // Regular transfer to another account
        BILL_PAYMENT    // Payment for bills with matching RF code
    }
    
    public enum OrderStatus {
        ACTIVE, PAUSED, CANCELLED, COMPLETED
    }
    
    private String id;
    private BigDecimal amount;
    private int frequencyMonths;  // How often to execute (in months)
    private int executionDay;     // Day of month to execute
    private LocalDate nextExecutionDate;
    private OrderStatus status;
    private OrderType type;
    
    // For transfers
    private Account sourceAccount;
    private Account destinationAccount;
    
    // For bill payments
    private String rfCode;  // Reference code to match bills
    private String providerName;  // Provider name for bill payments
    
    private String description;
    private LocalDateTime createdAt;
    private Customer owner;  // The customer who created this standing order
    
    // ==================== CONSTRUCTORS WITH systemDate PARAMETER ====================
    // Use these when creating standing orders during time simulation
    
    /**
     * Constructor for Transfer Standing Order
     * @param systemDate The current system date (use BankSystem.getCurrentDate())
     */
    public StandingOrder(String id, Account sourceAccount, Account destinationAccount,
                         BigDecimal amount, int frequencyMonths, int executionDay,
                         String description, Customer owner, LocalDate systemDate) {
        this.id = id;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.frequencyMonths = frequencyMonths;
        this.executionDay = executionDay;
        this.description = description;
        this.type = OrderType.TRANSFER;
        this.status = OrderStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
        calculateNextExecutionDate(systemDate);
    }
    
    /**
     * Constructor for Bill Payment Standing Order
     * Default: Monthly execution on day 15
     * @param systemDate The current system date (use BankSystem.getCurrentDate())
     */
    public StandingOrder(String id, Account sourceAccount, String rfCode,
                         String providerName, Customer owner, LocalDate systemDate) {
        this.id = id;
        this.sourceAccount = sourceAccount;
        this.rfCode = rfCode;
        this.providerName = providerName;
        this.type = OrderType.BILL_PAYMENT;
        this.status = OrderStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
        this.description = "Auto-pay bills from " + providerName;
        
        // Default: Monthly on day 15
        this.frequencyMonths = 1;
        this.executionDay = 15;
        calculateNextExecutionDate(systemDate);
    }
    
    /**
     * Constructor for Bill Payment Standing Order with custom frequency
     * @param systemDate The current system date (use BankSystem.getCurrentDate())
     */
    public StandingOrder(String id, Account sourceAccount, String rfCode,
                         String providerName, Customer owner, 
                         int frequencyMonths, int executionDay, LocalDate systemDate) {
        this.id = id;
        this.sourceAccount = sourceAccount;
        this.rfCode = rfCode;
        this.providerName = providerName;
        this.type = OrderType.BILL_PAYMENT;
        this.status = OrderStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
        this.description = "Auto-pay bills from " + providerName;
        this.frequencyMonths = frequencyMonths;
        this.executionDay = executionDay;
        calculateNextExecutionDate(systemDate);
    }
    
    // ==================== BACKWARDS COMPATIBLE CONSTRUCTORS ====================
    // These use LocalDate.now() - use for loading from CSV or when system date unavailable
    
    /**
     * Constructor for Transfer Standing Order (backwards compatible)
     * Uses LocalDate.now() - prefer the version with systemDate parameter
     */
    public StandingOrder(String id, Account sourceAccount, Account destinationAccount,
                         BigDecimal amount, int frequencyMonths, int executionDay,
                         String description, Customer owner) {
        this(id, sourceAccount, destinationAccount, amount, frequencyMonths, executionDay,
             description, owner, LocalDate.now());
    }
    
    /**
     * Constructor for Bill Payment Standing Order (backwards compatible)
     * Uses LocalDate.now() - prefer the version with systemDate parameter
     */
    public StandingOrder(String id, Account sourceAccount, String rfCode,
                         String providerName, Customer owner) {
        this(id, sourceAccount, rfCode, providerName, owner, LocalDate.now());
    }
    
    /**
     * Constructor for Bill Payment with custom frequency (backwards compatible)
     * Uses LocalDate.now() - prefer the version with systemDate parameter
     */
    public StandingOrder(String id, Account sourceAccount, String rfCode,
                         String providerName, Customer owner,
                         int frequencyMonths, int executionDay) {
        this(id, sourceAccount, rfCode, providerName, owner, frequencyMonths, executionDay, LocalDate.now());
    }
    
    /**
     * Calculate the next execution date based on current date
     */
    public void calculateNextExecutionDate(LocalDate currentDate) {
        // Handle both TRANSFER and BILL_PAYMENT the same way
        int day = Math.min(executionDay, currentDate.lengthOfMonth());
        if (day <= 0) day = 15; // Default to 15 if not set
        
        LocalDate next = currentDate.withDayOfMonth(day);
        
        // If the calculated date is not after current date, move to next period
        if (!next.isAfter(currentDate)) {
            int months = frequencyMonths > 0 ? frequencyMonths : 1;
            next = next.plusMonths(months);
            next = next.withDayOfMonth(Math.min(day, next.lengthOfMonth()));
        }
        
        this.nextExecutionDate = next;
    }
    
    /**
     * Check if this order should be executed on the given date
     */
    public boolean shouldExecute(LocalDate currentDate) {
        if (status != OrderStatus.ACTIVE) {
            return false;
        }
        if (type == OrderType.TRANSFER) {
            return nextExecutionDate != null && !nextExecutionDate.isAfter(currentDate);
        }
        // For BILL_PAYMENT, also check the execution date
        if (nextExecutionDate != null) {
            return !nextExecutionDate.isAfter(currentDate);
        }
        return true;  // Bill payments check for matching bills
    }
    
    /**
     * Update the order after execution
     */
    public void recordExecution() {
        if (nextExecutionDate != null) {
            int months = frequencyMonths > 0 ? frequencyMonths : 1;
            nextExecutionDate = nextExecutionDate.plusMonths(months);
            nextExecutionDate = nextExecutionDate.withDayOfMonth(
                Math.min(executionDay, nextExecutionDate.lengthOfMonth()));
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public int getFrequencyMonths() { return frequencyMonths; }
    public void setFrequencyMonths(int frequencyMonths) { this.frequencyMonths = frequencyMonths; }
    
    public int getExecutionDay() { return executionDay; }
    public void setExecutionDay(int executionDay) { this.executionDay = executionDay; }
    
    public LocalDate getNextExecutionDate() { return nextExecutionDate; }
    public void setNextExecutionDate(LocalDate nextExecutionDate) { 
        this.nextExecutionDate = nextExecutionDate; 
    }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public OrderType getType() { return type; }
    public void setType(OrderType type) { this.type = type; }
    
    public Account getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }
    
    public Account getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(Account destinationAccount) { 
        this.destinationAccount = destinationAccount; 
    }
    
    public String getRfCode() { return rfCode; }
    public void setRfCode(String rfCode) { this.rfCode = rfCode; }
    
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public Customer getOwner() { return owner; }
    public void setOwner(Customer owner) { this.owner = owner; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (type == OrderType.TRANSFER) {
            return String.format("StandingOrder[%s | TRANSFER | %.2f€ | Every %d month(s) | Next: %s | %s]",
                id, amount, frequencyMonths, 
                nextExecutionDate != null ? nextExecutionDate.format(formatter) : "N/A",
                status);
        } else {
            return String.format("StandingOrder[%s | BILL_PAYMENT | Provider: %s | RF: %s | Next: %s | %s]",
                id, providerName, rfCode,
                nextExecutionDate != null ? nextExecutionDate.format(formatter) : "N/A",
                status);
        }
    }
    
    public String toDetailedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order ID: %s\n", id));
        sb.append(String.format("Type: %s\n", type));
        sb.append(String.format("Status: %s\n", status));
        sb.append(String.format("Source Account: %s\n", sourceAccount.getIban()));
        
        if (type == OrderType.TRANSFER) {
            sb.append(String.format("Destination: %s\n", destinationAccount.getIban()));
            sb.append(String.format("Amount: %.2f€\n", amount));
            sb.append(String.format("Frequency: Every %d month(s)\n", frequencyMonths));
            sb.append(String.format("Execution Day: %d\n", executionDay));
            sb.append(String.format("Next Execution: %s\n", 
                nextExecutionDate != null ? nextExecutionDate.format(formatter) : "N/A"));
        } else {
            sb.append(String.format("Provider: %s\n", providerName));
            sb.append(String.format("RF Code: %s\n", rfCode));
            if (amount != null) {
                sb.append(String.format("Amount: %.2f€\n", amount));
            }
            sb.append(String.format("Frequency: Every %d month(s)\n", frequencyMonths > 0 ? frequencyMonths : 1));
            sb.append(String.format("Execution Day: %d\n", executionDay > 0 ? executionDay : 15));
            sb.append(String.format("Next Execution: %s\n", 
                nextExecutionDate != null ? nextExecutionDate.format(formatter) : "N/A"));
        }
        sb.append(String.format("Description: %s\n", description));
        return sb.toString();
    }
}