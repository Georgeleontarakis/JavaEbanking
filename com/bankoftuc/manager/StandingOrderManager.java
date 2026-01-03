package com.bankoftuc.manager;

import com.bankoftuc.model.*;
import com.bankoftuc.model.StandingOrder.OrderStatus;
import com.bankoftuc.model.StandingOrder.OrderType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Manages standing orders - creation, execution, and management.
 */
public class StandingOrderManager {
    
    private List<StandingOrder> standingOrders;
    private AtomicInteger orderIdCounter;
    
    public StandingOrderManager() {
        this.standingOrders = new ArrayList<>();
        this.orderIdCounter = new AtomicInteger(1);
    }
    
    public StandingOrderManager(List<StandingOrder> standingOrders) {
        this.standingOrders = standingOrders;
        this.orderIdCounter = new AtomicInteger(standingOrders.size() + 1);
    }
    
    /**
     * Create a transfer standing order
     */
    public StandingOrder createTransferStandingOrder(Account sourceAccount, Account destinationAccount,
                                                      BigDecimal amount, int frequencyMonths, 
                                                      int executionDay, String description,
                                                      Customer owner) {
        String id = "SO" + String.format("%06d", orderIdCounter.getAndIncrement());
        StandingOrder order = new StandingOrder(id, sourceAccount, destinationAccount,
                                                 amount, frequencyMonths, executionDay,
                                                 description, owner);
        standingOrders.add(order);
        return order;
    }
    
    /**
     * Create a bill payment standing order from an existing bill
     * This ensures the bill exists and captures the correct amount
     */
    public StandingOrder createBillPaymentStandingOrder(Account sourceAccount, Bill bill, Customer owner) {
        String id = "SO" + String.format("%06d", orderIdCounter.getAndIncrement());
        StandingOrder order = new StandingOrder(id, sourceAccount, bill.getRfCode(), 
                                                 bill.getProviderName(), owner);
        // Set the amount from the bill
        order.setAmount(bill.getAmount());
        standingOrders.add(order);
        return order;
    }
    
    /**
     * Create a bill payment standing order (legacy - for manual entry)
     * @deprecated Use createBillPaymentStandingOrder(Account, Bill, Customer) instead
     */
    @Deprecated
    public StandingOrder createBillPaymentStandingOrder(Account sourceAccount, String rfCode,
                                                         String providerName, Customer owner) {
        String id = "SO" + String.format("%06d", orderIdCounter.getAndIncrement());
        StandingOrder order = new StandingOrder(id, sourceAccount, rfCode, providerName, owner);
        standingOrders.add(order);
        return order;
    }
    
    /**
     * Create a bill payment standing order with amount
     */
    public StandingOrder createBillPaymentStandingOrder(Account sourceAccount, String rfCode,
                                                         String providerName, BigDecimal amount,
                                                         Customer owner) {
        String id = "SO" + String.format("%06d", orderIdCounter.getAndIncrement());
        StandingOrder order = new StandingOrder(id, sourceAccount, rfCode, providerName, owner);
        order.setAmount(amount);
        standingOrders.add(order);
        return order;
    }
    
    /**
     * Get all standing orders for a customer
     */
    public List<StandingOrder> getStandingOrdersForCustomer(Customer customer) {
        return standingOrders.stream()
            .filter(so -> so.getOwner().getId().equals(customer.getId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get active standing orders for a customer
     */
    public List<StandingOrder> getActiveStandingOrdersForCustomer(Customer customer) {
        return standingOrders.stream()
            .filter(so -> so.getOwner().getId().equals(customer.getId()))
            .filter(so -> so.getStatus() == OrderStatus.ACTIVE)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all due transfer standing orders for a given date
     */
    public List<StandingOrder> getDueTransferOrders(LocalDate currentDate) {
        return standingOrders.stream()
            .filter(so -> so.getType() == OrderType.TRANSFER)
            .filter(so -> so.getStatus() == OrderStatus.ACTIVE)
            .filter(so -> so.shouldExecute(currentDate))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all active bill payment standing orders
     */
    public List<StandingOrder> getActiveBillPaymentOrders() {
        return standingOrders.stream()
            .filter(so -> so.getType() == OrderType.BILL_PAYMENT)
            .filter(so -> so.getStatus() == OrderStatus.ACTIVE)
            .collect(Collectors.toList());
    }
    
    /**
     * Get due bill payment standing orders for a given date
     */
    public List<StandingOrder> getDueBillPaymentOrders(LocalDate currentDate) {
        return standingOrders.stream()
            .filter(so -> so.getType() == OrderType.BILL_PAYMENT)
            .filter(so -> so.getStatus() == OrderStatus.ACTIVE)
            .filter(so -> so.shouldExecute(currentDate))
            .collect(Collectors.toList());
    }
    
    /**
     * Find standing order by ID
     */
    public StandingOrder findById(String id) {
        return standingOrders.stream()
            .filter(so -> so.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Pause a standing order
     */
    public void pauseOrder(StandingOrder order) {
        if (order.getStatus() == OrderStatus.ACTIVE) {
            order.setStatus(OrderStatus.PAUSED);
        }
    }
    
    /**
     * Resume a paused standing order
     */
    public void resumeOrder(StandingOrder order) {
        if (order.getStatus() == OrderStatus.PAUSED) {
            order.setStatus(OrderStatus.ACTIVE);
        }
    }
    
    /**
     * Cancel a standing order
     */
    public void cancelOrder(StandingOrder order) {
        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
        }
    }
    
    /**
     * Execute due orders (called during time simulation)
     * @param currentDate The simulated current date
     * @param transactionManager For creating transactions
     * @param billManager For finding bills to pay
     * @return List of executed standing orders
     */
    public List<StandingOrder> executeDueOrders(LocalDate currentDate, 
                                                 TransactionManager transactionManager,
                                                 BillManager billManager) {
        List<StandingOrder> executedOrders = new ArrayList<>();
        
        // Execute transfer standing orders
        for (StandingOrder order : getDueTransferOrders(currentDate)) {
            try {
                Account source = order.getSourceAccount();
                Account dest = order.getDestinationAccount();
                BigDecimal amount = order.getAmount();
                
                if (source.getBalance().compareTo(amount) >= 0) {
                    transactionManager.transfer(source, dest, amount, 
                        "Standing Order: " + order.getDescription());
                    order.recordExecution();
                    executedOrders.add(order);
                    System.out.println("[OK] Executed transfer standing order " + order.getId() + 
                                       " - " + amount + " EUR");
                } else {
                    System.out.println("[WARN] Insufficient funds for standing order " + order.getId());
                }
            } catch (Exception e) {
                System.err.println("Failed to execute standing order " + order.getId() + ": " + e.getMessage());
            }
        }
        
        // Execute bill payment standing orders
        for (StandingOrder order : getDueBillPaymentOrders(currentDate)) {
            try {
                // First try to find bill by RF code
                List<Bill> matchingBills = billManager.findUnpaidByRfCode(order.getRfCode());
                
                // If no bills found by RF code, try by provider name for the order owner
                if (matchingBills.isEmpty()) {
                    List<Bill> providerBills = billManager.findUnpaidByProvider(order.getProviderName());
                    // Filter to only bills belonging to the order owner
                    for (Bill bill : providerBills) {
                        if (bill.getOwner().getId().equals(order.getOwner().getId())) {
                            matchingBills.add(bill);
                        }
                    }
                }
                
                if (matchingBills.isEmpty()) {
                    // No unpaid bills found - use the standing order amount if set
                    BigDecimal amount = order.getAmount();
                    if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                        Account source = order.getSourceAccount();
                        if (source.getBalance().compareTo(amount) >= 0) {
                            // Create a bill payment transaction without an actual bill
                            transactionManager.recordBillPayment(source, amount, 
                                "Auto-pay " + order.getProviderName() + " (Standing Order)");
                            order.recordExecution();
                            executedOrders.add(order);
                            System.out.println("[OK] Executed bill payment standing order " + order.getId() + 
                                               " - " + amount + " EUR to " + order.getProviderName());
                        } else {
                            System.out.println("[WARN] Insufficient funds for bill payment order " + order.getId());
                        }
                    } else {
                        System.out.println("[INFO] No unpaid bills found for standing order " + order.getId() + 
                                           " (Provider: " + order.getProviderName() + ", RF: " + order.getRfCode() + ")");
                    }
                } else {
                    // Pay all matching unpaid bills
                    for (Bill bill : matchingBills) {
                        Account source = order.getSourceAccount();
                        BigDecimal amount = bill.getAmount();
                        
                        if (source.getBalance().compareTo(amount) >= 0) {
                            // Withdraw from source account
                            source.withdraw(amount);
                            
                            // Mark bill as paid
                            bill.markAsPaid(currentDate.atStartOfDay());
                            
                            // Record the transaction properly
                            transactionManager.recordBillPayment(source, amount, 
                                "Bill payment: " + bill.getProviderName() + " (RF: " + bill.getRfCode() + ")");
                            
                            order.recordExecution();
                            executedOrders.add(order);
                            System.out.println("[OK] Paid bill " + bill.getId() + " - " + amount + 
                                               " EUR to " + bill.getProviderName());
                        } else {
                            System.out.println("[WARN] Insufficient funds to pay bill " + bill.getId());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to execute bill payment order " + order.getId() + ": " + e.getMessage());
            }
        }
        
        return executedOrders;
    }
    
    /**
     * Get all standing orders
     */
    public List<StandingOrder> getAllStandingOrders() {
        return new ArrayList<>(standingOrders);
    }
    
    /**
     * Get the standing orders list reference
     */
    public List<StandingOrder> getStandingOrders() {
        return standingOrders;
    }
    
    /**
     * Set the order ID counter (used when loading from persistence)
     */
    public void setOrderIdCounter(int counter) {
        this.orderIdCounter.set(counter);
    }
}