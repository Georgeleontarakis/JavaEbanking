package com.bankoftuc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a bill issued by a business to an individual customer.
 */
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum BillStatus {
        UNPAID, PAID, OVERDUE, CANCELLED
    }
    
    private String id;
    private String providerName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private BillStatus status;
    private LocalDateTime paidDate;
    private String rfCode;  // Reference code for payment
    private IndividualUser owner;  // The customer who owes this bill
    private BusinessUser issuer;  // The business that issued the bill
    
    public Bill(String id, String providerName, BigDecimal amount, LocalDate dueDate, 
                String rfCode, IndividualUser owner, BusinessUser issuer) {
        this.id = id;
        this.providerName = providerName;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = BillStatus.UNPAID;
        this.rfCode = rfCode;
        this.owner = owner;
        this.issuer = issuer;
    }
    
    /**
     * Mark the bill as paid
     */
    public void markAsPaid(LocalDateTime paidDate) {
        this.status = BillStatus.PAID;
        this.paidDate = paidDate;
    }
    
    /**
     * Check if the bill is overdue
     */
    public boolean isOverdue(LocalDate currentDate) {
        return status == BillStatus.UNPAID && currentDate.isAfter(dueDate);
    }
    
    /**
     * Update status to overdue if applicable
     */
    public void checkAndUpdateOverdue(LocalDate currentDate) {
        if (isOverdue(currentDate)) {
            status = BillStatus.OVERDUE;
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    
    public LocalDateTime getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDateTime paidDate) { this.paidDate = paidDate; }
    
    public String getRfCode() { return rfCode; }
    public void setRfCode(String rfCode) { this.rfCode = rfCode; }
    
    public IndividualUser getOwner() { return owner; }
    public void setOwner(IndividualUser owner) { this.owner = owner; }
    
    public BusinessUser getIssuer() { return issuer; }
    public void setIssuer(BusinessUser issuer) { this.issuer = issuer; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Bill[%s | %s | %.2f€ | Due: %s | RF: %s | Status: %s]", 
            id, providerName, amount, dueDate.format(formatter), rfCode, status);
    }
    
    public String toDetailedString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Bill ID: %s\n", id));
        sb.append(String.format("Provider: %s\n", providerName));
        sb.append(String.format("Amount: %.2f€\n", amount));
        sb.append(String.format("Due Date: %s\n", dueDate.format(dateFormatter)));
        sb.append(String.format("RF Code: %s\n", rfCode));
        sb.append(String.format("Status: %s\n", status));
        if (paidDate != null) {
            sb.append(String.format("Paid Date: %s\n", 
                paidDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        }
        return sb.toString();
    }
}
