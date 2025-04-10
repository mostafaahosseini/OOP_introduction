package Payment;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> config = Map.of(
                "credit_card_endpoint", "https://api.creditcard.com/process",
                "digital_wallet_endpoint", "https://api.digitalwallet.com/process",
                "bank_transfer_endpoint", "https://api.banktransfer.com/process"
        );

        PaymentProcessor processor = new PaymentProcessor(config);
        Map<String, String> customer = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> paymentDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");

        Map<String, String> result = processor.processPayment("credit_card", 100, "USD", customer, paymentDetails);
        System.out.println("Final Result: " + result);
    }
}
