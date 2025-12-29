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
     * Create a bill payment standing order
     */
    public StandingOrder createBillPaymentStandingOrder(Account sourceAccount, String rfCode,
                                                         String providerName, Customer owner) {
        String id = "SO" + String.format("%06d", orderIdCounter.getAndIncrement());
        StandingOrder order = new StandingOrder(id, sourceAccount, rfCode, providerName, owner);
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
     * Get standing orders for a User (delegates to getStandingOrdersForCustomer).
     * Added for GUI compatibility.
     */
    public List<StandingOrder> getStandingOrdersForUser(User user) {
        // Check if the user is a Customer (IndividualUser and BusinessUser should be Customers)
        if (user instanceof Customer) {
            return getStandingOrdersForCustomer((Customer) user);
        }
        // If the user isn't a customer (e.g. Admin), return empty list
        return new ArrayList<>();
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
                }
            } catch (Exception e) {
                System.err.println("Failed to execute standing order " + order.getId() + ": " + e.getMessage());
            }
        }
        
        // Execute bill payment standing orders
        for (StandingOrder order : getActiveBillPaymentOrders()) {
            try {
                List<Bill> matchingBills = billManager.findUnpaidByRfCode(order.getRfCode());
                if (matchingBills.isEmpty()) {
                    matchingBills = billManager.findUnpaidByProvider(order.getProviderName());
                }
                
                for (Bill bill : matchingBills) {
                    Account source = order.getSourceAccount();
                    if (source.getBalance().compareTo(bill.getAmount()) >= 0) {
                        source.withdraw(bill.getAmount());
                        bill.markAsPaid(currentDate.atStartOfDay());
                        
                        transactionManager.deposit(source, bill.getAmount().negate(), 
                            "Bill payment: " + bill.getProviderName());
                        executedOrders.add(order);
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
}
