package com.bankoftuc.bridge;

import com.bankoftuc.manager.BankTransferAPI;
import java.math.BigDecimal;

/**
 * Bridge Pattern - SWIFT Concrete Implementor.
 * Implements transfer via SWIFT (Society for Worldwide Interbank Financial Telecommunication).
 * Uses the external Bank Transfer API.
 */
public class SwiftImplementor implements TransferImplementor {
    
    private static final BigDecimal SWIFT_FEE = new BigDecimal("25.00");
    private static final String MECHANISM_NAME = "SWIFT";
    
    @Override
    public TransferResult executeTransfer(TransferRequest request) {
        System.out.println("[SWIFT] Initiating international transfer...");
        
        try {
            // Call the external SWIFT API
            BankTransferAPI.TransferResult apiResult = BankTransferAPI.executeSwiftTransfer(
                request.getCurrency() != null ? request.getCurrency() : "EUR",
                request.getAmount().doubleValue(),
                request.getRecipientName() != null ? request.getRecipientName() : "Beneficiary",
                request.getRecipientAddress() != null ? request.getRecipientAddress() : "Unknown",
                request.getRecipientAccount(),
                request.getBankName() != null ? request.getBankName() : "Unknown Bank",
                request.getBankCode(), // SWIFT code
                request.getBankCountry() != null ? request.getBankCountry() : "XX",
                request.getChargeModel() != null ? request.getChargeModel() : "SHA"
            );
            
            if (apiResult.isSuccess()) {
                System.out.println("[SWIFT] Transfer successful. TxID: " + apiResult.getTransactionId());
                return TransferResult.success(
                    "SWIFT transfer completed successfully",
                    apiResult.getTransactionId()
                );
            } else {
                System.out.println("[SWIFT] Transfer failed: " + apiResult.getMessage());
                return TransferResult.failure(
                    apiResult.getMessage(),
                    "SWIFT_FAILED"
                );
            }
            
        } catch (Exception e) {
            System.err.println("[SWIFT] Error: " + e.getMessage());
            return TransferResult.failure(
                "SWIFT transfer error: " + e.getMessage(),
                "SWIFT_ERROR"
            );
        }
    }
    
    @Override
    public String getMechanismName() {
        return MECHANISM_NAME;
    }
    
    @Override
    public BigDecimal getFee() {
        return SWIFT_FEE;
    }
    
    @Override
    public boolean isAvailable() {
        // Check if API is reachable
        return BankTransferAPI.testConnection();
    }
}
