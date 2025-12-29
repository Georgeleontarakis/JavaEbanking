package com.bankoftuc.bridge;

import com.bankoftuc.model.Account;
import com.bankoftuc.bridge.TransferImplementor.TransferRequest;
import com.bankoftuc.bridge.TransferImplementor.TransferResult;
import java.math.BigDecimal;

/**
 * Bridge Pattern - Refined Abstraction.
 * Extends the abstraction for external bank transfers.
 * Handles validation and fee calculation.
 */
public class ExternalTransfer extends TransferAbstraction {
    
    public ExternalTransfer(TransferImplementor implementor) {
        super(implementor);
    }
    
    @Override
    public TransferResult transfer(Account fromAccount, TransferRequest request) {
        // Validate request
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return TransferResult.failure("Invalid amount", "VALIDATION_ERROR");
        }
        
        if (request.getRecipientAccount() == null || request.getRecipientAccount().isEmpty()) {
            return TransferResult.failure("Recipient account is required", "VALIDATION_ERROR");
        }
        
        // Calculate total including fee
        BigDecimal totalAmount = request.getAmount().add(implementor.getFee());
        
        // Check sufficient funds
        if (fromAccount.getBalance().compareTo(totalAmount) < 0) {
            return TransferResult.failure(
                String.format("Insufficient funds. Required: %.2f EUR (including %.2f EUR fee)",
                    totalAmount, implementor.getFee()),
                "INSUFFICIENT_FUNDS"
            );
        }
        
        // Check service availability
        if (!implementor.isAvailable()) {
            return TransferResult.failure(
                implementor.getMechanismName() + " service is currently unavailable",
                "SERVICE_UNAVAILABLE"
            );
        }
        
        // Execute transfer via implementor
        System.out.println(String.format("[%s] Processing transfer of %.2f EUR...",
            implementor.getMechanismName(), request.getAmount()));
        
        TransferResult result = implementor.executeTransfer(request);
        
        // If successful, deduct from account
        if (result.isSuccess()) {
            fromAccount.withdraw(totalAmount);
            System.out.println(String.format("[%s] Deducted %.2f EUR (%.2f + %.2f fee) from %s",
                implementor.getMechanismName(),
                totalAmount, request.getAmount(), implementor.getFee(),
                fromAccount.getIban()));
        }
        
        return result;
    }
    
    /**
     * Transfer with simplified parameters
     */
    public TransferResult transfer(Account fromAccount, String toAccount, 
                                    BigDecimal amount, String description) {
        TransferRequest request = new TransferRequest();
        request.setSenderAccount(fromAccount.getIban());
        request.setRecipientAccount(toAccount);
        request.setAmount(amount);
        request.setDescription(description);
        
        return transfer(fromAccount, request);
    }
    
    /**
     * Create a SEPA transfer handler
     */
    public static ExternalTransfer createSepaTransfer() {
        return new ExternalTransfer(new SepaImplementor());
    }
    
    /**
     * Create a SWIFT transfer handler
     */
    public static ExternalTransfer createSwiftTransfer() {
        return new ExternalTransfer(new SwiftImplementor());
    }
}
