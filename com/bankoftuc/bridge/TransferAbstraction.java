package com.bankoftuc.bridge;

import com.bankoftuc.model.Account;
import com.bankoftuc.model.Transaction;
import com.bankoftuc.bridge.TransferImplementor.TransferRequest;
import com.bankoftuc.bridge.TransferImplementor.TransferResult;
import java.math.BigDecimal;

/**
 * Bridge Pattern - Abstraction.
 * Provides high-level interface for transfers.
 * Delegates implementation details to TransferImplementor.
 */
public abstract class TransferAbstraction {
    
    protected TransferImplementor implementor;
    
    public TransferAbstraction(TransferImplementor implementor) {
        this.implementor = implementor;
    }
    
    /**
     * Execute a transfer
     */
    public abstract TransferResult transfer(Account fromAccount, TransferRequest request);
    
    /**
     * Get the fee for this transfer
     */
    public BigDecimal getFee() {
        return implementor.getFee();
    }
    
    /**
     * Get mechanism name
     */
    public String getMechanismName() {
        return implementor.getMechanismName();
    }
    
    /**
     * Check availability
     */
    public boolean isAvailable() {
        return implementor.isAvailable();
    }
    
    /**
     * Change the implementor at runtime
     */
    public void setImplementor(TransferImplementor implementor) {
        this.implementor = implementor;
    }
}
