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
    
    /**
     * Constructor for Transfer Standing Order
     */
    public StandingOrder(String id, Account sourceAccount, Account destinationAccount,
                         BigDecimal amount, int frequencyMonths, int executionDay,
                         String description, Customer owner) {
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
        calculateNextExecutionDate(LocalDate.now());
    }
    
    /**
     * Constructor for Bill Payment Standing Order
     */
    public StandingOrder(String id, Account sourceAccount, String rfCode,
                         String providerName, Customer owner) {
        this.id = id;
        this.sourceAccount = sourceAccount;
        this.rfCode = rfCode;
        this.providerName = providerName;
        this.type = OrderType.BILL_PAYMENT;
        this.status = OrderStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
        this.description = "Auto-pay bills from " + providerName;
    }
    
    /**
     * Calculate the next execution date based on current date
     */
    public void calculateNextExecutionDate(LocalDate currentDate) {
        if (type == OrderType.TRANSFER) {
            LocalDate next = currentDate.withDayOfMonth(Math.min(executionDay, 
                currentDate.lengthOfMonth()));
            if (!next.isAfter(currentDate)) {
                next = next.plusMonths(frequencyMonths);
                next = next.withDayOfMonth(Math.min(executionDay, next.lengthOfMonth()));
            }
            this.nextExecutionDate = next;
        }
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
        return true;  // Bill payments check for matching bills
    }
    
    /**
     * Update the order after execution
     */
    public void recordExecution() {
        if (type == OrderType.TRANSFER && nextExecutionDate != null) {
            nextExecutionDate = nextExecutionDate.plusMonths(frequencyMonths);
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
            return String.format("StandingOrder[%s | BILL_PAYMENT | Provider: %s | RF: %s | %s]",
                id, providerName, rfCode, status);
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
        }
        sb.append(String.format("Description: %s\n", description));
        return sb.toString();
    }
}
