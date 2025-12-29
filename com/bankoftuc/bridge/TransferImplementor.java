package com.bankoftuc.bridge;

import java.math.BigDecimal;

/**
 * Bridge Pattern - Implementor Interface.
 * Defines the interface for different transfer mechanisms.
 * Allows separation of transfer abstraction from implementation.
 */
public interface TransferImplementor {
    
    /**
     * Execute the transfer via external system
     * @return TransferResult with success status and details
     */
    TransferResult executeTransfer(TransferRequest request);
    
    /**
     * Get the name of this transfer mechanism
     */
    String getMechanismName();
    
    /**
     * Get the fee for this transfer type
     */
    BigDecimal getFee();
    
    /**
     * Check if the service is available
     */
    boolean isAvailable();
    
    /**
     * Transfer request data class
     */
    public static class TransferRequest {
        private String senderAccount;
        private String recipientAccount;
        private String recipientName;
        private String recipientAddress;
        private BigDecimal amount;
        private String currency;
        private String bankCode;
        private String bankName;
        private String bankCountry;
        private String chargeModel;
        private String description;
        
        public TransferRequest() {
            this.currency = "EUR";
            this.chargeModel = "SHA";
        }
        
        // Getters and setters
        public String getSenderAccount() { return senderAccount; }
        public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
        
        public String getRecipientAccount() { return recipientAccount; }
        public void setRecipientAccount(String recipientAccount) { this.recipientAccount = recipientAccount; }
        
        public String getRecipientName() { return recipientName; }
        public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
        
        public String getRecipientAddress() { return recipientAddress; }
        public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getBankCode() { return bankCode; }
        public void setBankCode(String bankCode) { this.bankCode = bankCode; }
        
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        
        public String getBankCountry() { return bankCountry; }
        public void setBankCountry(String bankCountry) { this.bankCountry = bankCountry; }
        
        public String getChargeModel() { return chargeModel; }
        public void setChargeModel(String chargeModel) { this.chargeModel = chargeModel; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * Transfer result data class
     */
    public static class TransferResult {
        private boolean success;
        private String message;
        private String transactionId;
        private String errorCode;
        
        public TransferResult(boolean success, String message, String transactionId) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public static TransferResult success(String message, String transactionId) {
            return new TransferResult(true, message, transactionId);
        }
        
        public static TransferResult failure(String message, String errorCode) {
            TransferResult result = new TransferResult(false, message, null);
            result.errorCode = errorCode;
            return result;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
        public String getErrorCode() { return errorCode; }
    }
}
