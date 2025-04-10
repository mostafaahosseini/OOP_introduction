package Payment;

import java.util.Date;
import java.util.Map;

public class PaymentProcessor {
    private Map<String, String> config;

    public PaymentProcessor(Map<String, String> config) {
        this.config = config;
    }

    public Map<String, String> processPayment(String paymentType, double amount, String currency,
                                              Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        Payment payment;
        switch (paymentType) {
            case "credit_card":
                payment = new CreditCardPayment(amount, currency, customerInfo, paymentDetails);
                break;
            case "digital_wallet":
                payment = new DigitalWalletPayment(amount, currency, customerInfo, paymentDetails);
                break;
            case "bank_transfer":
                payment = new BankTransferPayment(amount, currency, customerInfo, paymentDetails);
                break;
            default:
                return Map.of("status", "failed", "message", "Unknown payment type");
        }

        if (!payment.validatePayment()) {
            return Map.of("status", "failed", "message", "Validation error");
        }

        Map<String, String> result = handleProcessing(paymentType, amount, currency, customerInfo, paymentDetails);
        logTransaction(paymentType, amount, currency, customerInfo, paymentDetails, result);
        return result;
    }

    private Map<String, String> handleProcessing(String type, double amount, String currency,
                                                 Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        String endpointKey = type + "_endpoint";
        String endpoint = config.get(endpointKey);
        String transactionId = type.substring(0, 2).toUpperCase() + new Date().getTime();

        System.out.println("Connecting to API at " + endpoint);
        System.out.println("Processing " + type + " payment for " + customerInfo.get("name"));

        return Map.of("status", "success", "transaction_id", transactionId);
    }

    private void logTransaction(String paymentType, double amount, String currency,
                                Map<String, String> customerInfo, Map<String, String> paymentDetails,
                                Map<String, String> result) {
        String logEntry = String.format("%s - %s payment of %.2f %s for %s: %s",
                new Date(), paymentType, amount, currency, customerInfo.get("name"), result);
        System.out.println("LOG: " + logEntry);
    }
}

