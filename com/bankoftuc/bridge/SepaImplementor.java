package com.bankoftuc.bridge;

import com.bankoftuc.manager.BankTransferAPI;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bridge Pattern - SEPA Concrete Implementor.
 * Implements transfer via SEPA (Single Euro Payments Area) protocol.
 * Uses the external Bank Transfer API.
 */
public class SepaImplementor implements TransferImplementor {
    
    private static final BigDecimal SEPA_FEE = new BigDecimal("1.50");
    private static final String MECHANISM_NAME = "SEPA";
    
    @Override
    public TransferResult executeTransfer(TransferRequest request) {
        System.out.println("[SEPA] Initiating European transfer...");
        
        try {
            // Call the external SEPA API
            BankTransferAPI.TransferResult apiResult = BankTransferAPI.executeSepaTransfer(
                request.getAmount().doubleValue(),
                request.getRecipientName() != null ? request.getRecipientName() : "Beneficiary",
                request.getRecipientAccount(),
                request.getBankCode() != null ? request.getBankCode() : "ETHNGRAA",
                request.getBankName() != null ? request.getBankName() : "Unknown Bank",
                LocalDate.now(),
                request.getChargeModel() != null ? request.getChargeModel() : "SHA"
            );
            
            if (apiResult.isSuccess()) {
                System.out.println("[SEPA] Transfer successful. TxID: " + apiResult.getTransactionId());
                return TransferResult.success(
                    "SEPA transfer completed successfully",
                    apiResult.getTransactionId()
                );
            } else {
                System.out.println("[SEPA] Transfer failed: " + apiResult.getMessage());
                return TransferResult.failure(
                    apiResult.getMessage(),
                    "SEPA_FAILED"
                );
            }
            
        } catch (Exception e) {
            System.err.println("[SEPA] Error: " + e.getMessage());
            return TransferResult.failure(
                "SEPA transfer error: " + e.getMessage(),
                "SEPA_ERROR"
            );
        }
    }
    
    @Override
    public String getMechanismName() {
        return MECHANISM_NAME;
    }
    
    @Override
    public BigDecimal getFee() {
        return SEPA_FEE;
    }
    
    @Override
    public boolean isAvailable() {
        // Check if API is reachable
        return BankTransferAPI.testConnection();
    }
}
