package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import com.bankoftuc.model.Bill.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Manages bills - creation, payment, and retrieval.
 */
public class BillManager {
    
    private List<Bill> bills;
    private AtomicInteger billIdCounter;
    private AtomicInteger rfCodeCounter;
    
    public BillManager() {
        this.bills = new ArrayList<>();
        this.billIdCounter = new AtomicInteger(1);
        this.rfCodeCounter = new AtomicInteger(1000);
    }
    
    public BillManager(List<Bill> bills) {
        this.bills = bills;
        this.billIdCounter = new AtomicInteger(bills.size() + 1);
        this.rfCodeCounter = new AtomicInteger(1000 + bills.size());
    }
    
    /**
     * Generate RF code for a new bill
     */
    private String generateRfCode() {
        return "RF" + String.format("%08d", rfCodeCounter.getAndIncrement());
    }
    
    /**
     * Create a new bill
     */
    public Bill createBill(IndividualUser owner, BusinessUser issuer, String providerName,
                           BigDecimal amount, LocalDate dueDate, String rfCode) {
        String id = "BILL" + String.format("%06d", billIdCounter.getAndIncrement());
        String rf = rfCode != null ? rfCode : generateRfCode();
        
        Bill bill = new Bill(id, providerName, amount, dueDate, rf, owner, issuer);
        bills.add(bill);
        return bill;
    }
    
    /**
     * Create a new bill with auto-generated RF code
     */
    public Bill createBill(IndividualUser owner, BusinessUser issuer, String providerName,
                           BigDecimal amount, LocalDate dueDate) {
        return createBill(owner, issuer, providerName, amount, dueDate, null);
    }
    
    /**
     * Get all unpaid bills for a user
     */
    public List<Bill> getUnpaidBillsForUser(IndividualUser user) {
        return bills.stream()
            .filter(b -> b.getOwner().getId().equals(user.getId()))
            .filter(b -> b.getStatus() == BillStatus.UNPAID || b.getStatus() == BillStatus.OVERDUE)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bills for a user
     */
    public List<Bill> getBillsForUser(IndividualUser user) {
        return bills.stream()
            .filter(b -> b.getOwner().getId().equals(user.getId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bills issued by a business
     */
    public List<Bill> getBillsIssuedByBusiness(BusinessUser business) {
        return bills.stream()
            .filter(b -> b.getIssuer().getId().equals(business.getId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Find bill by ID
     */
    public Bill findById(String id) {
        return bills.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Find unpaid bills by RF code
     */
    public List<Bill> findUnpaidByRfCode(String rfCode) {
        return bills.stream()
            .filter(b -> b.getRfCode().equals(rfCode))
            .filter(b -> b.getStatus() == BillStatus.UNPAID || b.getStatus() == BillStatus.OVERDUE)
            .collect(Collectors.toList());
    }
    
    /**
     * Find unpaid bills by provider name
     */
    public List<Bill> findUnpaidByProvider(String providerName) {
        return bills.stream()
            .filter(b -> b.getProviderName().equalsIgnoreCase(providerName))
            .filter(b -> b.getStatus() == BillStatus.UNPAID || b.getStatus() == BillStatus.OVERDUE)
            .collect(Collectors.toList());
    }
    
    /**
     * Mark a bill as paid
     */
    public void markBillAsPaid(Bill bill, LocalDateTime paidDate) {
        bill.markAsPaid(paidDate);
    }
    
    /**
     * Update bill status to overdue if applicable
     */
    public void updateOverdueBills(LocalDate currentDate) {
        for (Bill bill : bills) {
            bill.checkAndUpdateOverdue(currentDate);
        }
    }
    
    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        return new ArrayList<>(bills);
    }
    
    /**
     * Get the bills list reference
     */
    public List<Bill> getBills() {
        return bills;
    }
}
