package com.bankoftuc.builder;

import com.bankoftuc.model.*;
import com.bankoftuc.model.Bill.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Builder Pattern - Builds Bill objects step by step.
 * Allows flexible construction of complex Bill objects.
 */
public class BillBuilder {
    
    private String id;
    private String providerName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private BillStatus status;
    private LocalDateTime paidDate;
    private String rfCode;
    private IndividualUser owner;      // Customer who must pay
    private BusinessUser issuer;       // Business that issued the bill
    
    private static long rfCounter = 1;
    private static long idCounter = 1;
    
    public BillBuilder() {
        // Set defaults
        this.status = BillStatus.UNPAID;
        this.dueDate = LocalDate.now().plusDays(30);
    }
    
    public BillBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    public BillBuilder withProviderName(String providerName) {
        this.providerName = providerName;
        return this;
    }
    
    public BillBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    public BillBuilder withAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
        return this;
    }
    
    public BillBuilder withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }
    
    public BillBuilder withDueDateInDays(int days) {
        this.dueDate = LocalDate.now().plusDays(days);
        return this;
    }
    
    public BillBuilder withStatus(BillStatus status) {
        this.status = status;
        return this;
    }
    
    public BillBuilder withPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
        return this;
    }
    
    public BillBuilder withRfCode(String rfCode) {
        this.rfCode = rfCode;
        return this;
    }
    
    public BillBuilder forCustomer(IndividualUser owner) {
        this.owner = owner;
        return this;
    }
    
    public BillBuilder issuedBy(BusinessUser issuer) {
        this.issuer = issuer;
        if (this.providerName == null || this.providerName.isEmpty()) {
            this.providerName = issuer.getBusinessName();
        }
        return this;
    }
    
    /**
     * Configure as utility bill
     */
    public BillBuilder asUtilityBill(String utilityType, BigDecimal amount) {
        this.providerName = utilityType + " Provider";
        this.amount = amount;
        return this;
    }
    
    /**
     * Configure as invoice from business
     */
    public BillBuilder asInvoice(BusinessUser business, BigDecimal amount) {
        this.issuer = business;
        this.providerName = business.getBusinessName();
        this.amount = amount;
        return this;
    }
    
    /**
     * Build the Bill object
     */
    public Bill build() {
        validate();
        
        // Generate ID if not set
        if (id == null || id.isEmpty()) {
            id = String.format("BILL%06d", idCounter++);
        }
        
        // Generate RF code if not set
        if (rfCode == null || rfCode.isEmpty()) {
            rfCode = String.format("RF%08d", rfCounter++);
        }
        
        Bill bill = new Bill(id, providerName, amount, dueDate, rfCode, owner, issuer);
        bill.setStatus(status);
        if (paidDate != null) {
            bill.setPaidDate(paidDate);
        }
        
        return bill;
    }
    
    /**
     * Validate required fields
     */
    private void validate() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Bill amount must be positive");
        }
        if (providerName == null || providerName.isEmpty()) {
            throw new IllegalStateException("Provider name is required");
        }
        if (owner == null) {
            throw new IllegalStateException("Bill owner (customer) is required");
        }
        if (dueDate == null) {
            throw new IllegalStateException("Due date is required");
        }
    }
    
    /**
     * Reset builder for reuse
     */
    public BillBuilder reset() {
        this.id = null;
        this.providerName = null;
        this.amount = null;
        this.dueDate = LocalDate.now().plusDays(30);
        this.status = BillStatus.UNPAID;
        this.paidDate = null;
        this.rfCode = null;
        this.owner = null;
        this.issuer = null;
        return this;
    }
    
    /**
     * Set RF counter (used when loading from persistence)
     */
    public static void setRfCounter(long counter) {
        rfCounter = counter;
    }
    
    public static void setIdCounter(long counter) {
        idCounter = counter;
    }
}
