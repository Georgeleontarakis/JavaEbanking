package com.bankoftuc.manager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Client for the Bank Transfer Simulation API.
 * Handles SEPA and SWIFT transfer requests.
 * 
 * API Base URL: http://147.27.70.44:3020
 * SEPA: 75% success rate, 25% failure rate
 * SWIFT: 75% success rate, 25% failure rate
 */
public class BankTransferAPI {
    
    private static final String BASE_URL = "http://147.27.70.44:3020";
    private static final String SEPA_ENDPOINT = "/transfer/sepa";
    private static final String SWIFT_ENDPOINT = "/transfer/swift";
    private static final int TIMEOUT = 10000; // 10 seconds
    
    /**
     * Result of an API transfer request
     */
    public static class TransferResult {
        private boolean success;
        private String message;
        private String transactionId;
        private String rawResponse;
        
        public TransferResult(boolean success, String message, String transactionId, String rawResponse) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
            this.rawResponse = rawResponse;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
        public String getRawResponse() { return rawResponse; }
        
        @Override
        public String toString() {
            return String.format("TransferResult{success=%s, message='%s', transactionId='%s'}", 
                success, message, transactionId);
        }
    }
    
    /**
     * Execute a SEPA transfer
     * 
     * @param amount Transfer amount
     * @param creditorName Name of the creditor
     * @param creditorIban IBAN of the creditor
     * @param creditorBankBic BIC of the creditor's bank
     * @param creditorBankName Name of the creditor's bank
     * @param executionDate Requested execution date
     * @param charges Charge model: "SHA" (shared) or "OUR" (sender pays all)
     * @return TransferResult with success status and details
     */
    public static TransferResult executeSepaTransfer(
            double amount,
            String creditorName,
            String creditorIban,
            String creditorBankBic,
            String creditorBankName,
            LocalDate executionDate,
            String charges) {
        
        String dateStr = executionDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        // Build JSON request
        String jsonRequest = String.format(
            "{" +
            "\"amount\": %.2f," +
            "\"creditor\": {" +
            "  \"name\": \"%s\"," +
            "  \"iban\": \"%s\"" +
            "}," +
            "\"creditorBank\": {" +
            "  \"bic\": \"%s\"," +
            "  \"name\": \"%s\"" +
            "}," +
            "\"execution\": {" +
            "  \"requestedDate\": \"%s\"," +
            "  \"charges\": \"%s\"" +
            "}" +
            "}",
            amount,
            escapeJson(creditorName),
            escapeJson(creditorIban),
            escapeJson(creditorBankBic),
            escapeJson(creditorBankName),
            dateStr,
            charges
        );
        
        return sendRequest(BASE_URL + SEPA_ENDPOINT, jsonRequest);
    }
    
    /**
     * Execute a SEPA transfer with default values
     */
    public static TransferResult executeSepaTransfer(double amount, String creditorIban) {
        return executeSepaTransfer(
            amount,
            "Beneficiary",
            creditorIban,
            "ETHNGRAA",  // Default: National Bank of Greece
            "National Bank of Greece",
            LocalDate.now(),
            "SHA"
        );
    }
    
    /**
     * Execute a SWIFT transfer
     * 
     * @param currency Currency code (e.g., "EUR")
     * @param amount Transfer amount
     * @param beneficiaryName Name of the beneficiary
     * @param beneficiaryAddress Address of the beneficiary
     * @param beneficiaryAccount Account number of the beneficiary
     * @param bankName Name of the beneficiary's bank
     * @param swiftCode SWIFT code of the beneficiary's bank
     * @param bankCountry Country of the beneficiary's bank
     * @param chargingModel Fee charging model: "SHA" or "OUR"
     * @return TransferResult with success status and details
     */
    public static TransferResult executeSwiftTransfer(
            String currency,
            double amount,
            String beneficiaryName,
            String beneficiaryAddress,
            String beneficiaryAccount,
            String bankName,
            String swiftCode,
            String bankCountry,
            String chargingModel) {
        
        // Build JSON request
        String jsonRequest = String.format(
            "{" +
            "\"currency\": \"%s\"," +
            "\"amount\": %.2f," +
            "\"beneficiary\": {" +
            "  \"name\": \"%s\"," +
            "  \"address\": \"%s\"," +
            "  \"account\": \"%s\"" +
            "}," +
            "\"beneficiaryBank\": {" +
            "  \"name\": \"%s\"," +
            "  \"swiftCode\": \"%s\"," +
            "  \"country\": \"%s\"" +
            "}," +
            "\"fees\": {" +
            "  \"chargingModel\": \"%s\"" +
            "}," +
            "\"correspondentBank\": {" +
            "  \"required\": false" +
            "}" +
            "}",
            currency,
            amount,
            escapeJson(beneficiaryName),
            escapeJson(beneficiaryAddress),
            escapeJson(beneficiaryAccount),
            escapeJson(bankName),
            escapeJson(swiftCode),
            escapeJson(bankCountry),
            chargingModel
        );
        
        return sendRequest(BASE_URL + SWIFT_ENDPOINT, jsonRequest);
    }
    
    /**
     * Execute a SWIFT transfer with default values
     */
    public static TransferResult executeSwiftTransfer(double amount, String beneficiaryAccount, String swiftCode) {
        return executeSwiftTransfer(
            "EUR",
            amount,
            "Beneficiary",
            "Unknown Address",
            beneficiaryAccount,
            "Unknown Bank",
            swiftCode,
            "XX",
            "SHA"
        );
    }
    
    /**
     * Send HTTP POST request to the API
     */
    private static TransferResult sendRequest(String urlString, String jsonBody) {
        HttpURLConnection connection = null;
        
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            
            // Send request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode >= 200 && responseCode < 300) 
                ? connection.getInputStream() 
                : connection.getErrorStream();
            
            String response = readStream(inputStream);
            
            // Parse response
            return parseResponse(response, responseCode);
            
        } catch (java.net.SocketTimeoutException e) {
            return new TransferResult(false, "Connection timeout - API server not responding", null, null);
        } catch (java.net.ConnectException e) {
            return new TransferResult(false, "Cannot connect to API server at " + BASE_URL, null, null);
        } catch (IOException e) {
            return new TransferResult(false, "Network error: " + e.getMessage(), null, null);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Read input stream to string
     */
    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
    
    /**
     * Parse JSON response from API
     */
    private static TransferResult parseResponse(String response, int httpCode) {
        // Simple JSON parsing without external libraries
        String status = extractJsonValue(response, "status");
        String message = extractJsonValue(response, "message");
        String transactionId = extractJsonValue(response, "transaction_id");
        
        boolean success = "success".equalsIgnoreCase(status) && httpCode == 200;
        
        return new TransferResult(success, message, transactionId, response);
    }
    
    /**
     * Extract a value from JSON string (simple implementation)
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '"')) {
            startIndex++;
        }
        
        // Check if it's a quoted string
        if (startIndex > 0 && json.charAt(startIndex - 1) == '"') {
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex == -1) return null;
            return json.substring(startIndex, endIndex);
        } else {
            // Not quoted - find end of value
            int endIndex = startIndex;
            while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
                endIndex++;
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
    
    /**
     * Escape special characters for JSON
     */
    private static String escapeJson(String value) {
        if (value == null) return "";
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    /**
     * Test the API connection
     */
    public static boolean testConnection() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode > 0;
        } catch (IOException e) {
            return false;
        }
    }
}
