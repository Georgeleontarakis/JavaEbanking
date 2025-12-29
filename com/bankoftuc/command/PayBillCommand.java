package com.bankoftuc.command;

import com.bankoftuc.model.*;
import com.bankoftuc.manager.BillManager;
import java.math.BigDecimal;

/**
 * Command Pattern - Pay Bill Command.
 * Encapsulates a bill payment operation.
 */
public class PayBillCommand implements Command {
    
    private Bill bill;
    private Account payerAccount;
    private BillManager billManager;
    private BigDecimal fee;
    private boolean executed;
    private Bill.BillStatus previousStatus;
    
    private static final BigDecimal BILL_PAYMENT_FEE = new BigDecimal("0.50");
    
    public PayBillCommand(Bill bill, Account payerAccount, BillManager billManager) {
        this.bill = bill;
        this.payerAccount = payerAccount;
        this.billManager = billManager;
        this.fee = BILL_PAYMENT_FEE;
        this.executed = false;
    }
    
    @Override
    public boolean execute() {
        if (executed) {
            return false; // Already executed
        }
        
        // Check if bill is already paid
        if (bill.getStatus() == Bill.BillStatus.PAID) {
            System.err.println("Bill is already paid");
            return false;
        }
        
        // Calculate total amount (bill + fee)
        BigDecimal totalAmount = bill.getAmount().add(fee);
        
        // Check sufficient funds
        if (payerAccount.getBalance().compareTo(totalAmount) < 0) {
            System.err.println("Insufficient funds for bill payment");
            return false;
        }
        
        try {
            previousStatus = bill.getStatus();
            
            // Withdraw amount from payer's account
            payerAccount.withdraw(totalAmount);
            
            // Mark bill as paid
            billManager.markBillAsPaid(bill, java.time.LocalDateTime.now());
            
            executed = true;
            return true;
        } catch (Exception e) {
            System.err.println("Bill payment failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed) {
            return false; // Nothing to undo
        }
        
        try {
            // Refund the amount
            BigDecimal totalAmount = bill.getAmount().add(fee);
            payerAccount.deposit(totalAmount);
            
            // Reset bill status
            bill.setStatus(previousStatus);
            bill.setPaidDate(null);
            
            executed = false;
            return true;
        } catch (Exception e) {
            System.err.println("Undo bill payment failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Pay bill RF:%s (%.2f EUR + %.2f EUR fee) from %s",
            bill.getRfCode(), bill.getAmount(), fee, payerAccount.getIban());
    }
    
    @Override
    public boolean isUndoable() {
        return executed;
    }
}
