import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Sample config maps
        // In reality, you'd load these from environment variables, a .properties file, etc.
        Map<String, String> stripeConfig = Map.of(
                "stripeEndpoint", "https://api.stripe.com",
                "apiKey", "sk_test_123"
        );
        Map<String, String> paypalConfig = Map.of(
                "paypalEndpoint", "https://api.paypal.com",
                "clientId", "paypal_client_456"
        );

        // A typical Payment object (from Stage 2) -- for example, a credit card payment
        Map<String, String> customerInfo = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> cardDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");
        Payment creditCardPayment = new CreditCardPayment(100, "USD", customerInfo, cardDetails);

        // 1) Using StripeGateway
        PaymentGateway stripeGateway = new StripeGateway(stripeConfig);
        PaymentProcessor stripeProcessor = new PaymentProcessor(stripeGateway);

        Map<String, String> stripeResult = stripeProcessor.processPayment(creditCardPayment);
        System.out.println("Stripe Result: " + stripeResult);

        // 2) Switching to PayPalGateway at runtime
        PaymentGateway paypalGateway = new PayPalGateway(paypalConfig);
        PaymentProcessor paypalProcessor = new PaymentProcessor(paypalGateway);

        Map<String, String> paypalResult = paypalProcessor.processPayment(creditCardPayment);
        System.out.println("PayPal Result: " + paypalResult);

        // Demonstrating refund
        System.out.println("Refunding on PayPal...");
        Map<String, String> refundResult = paypalProcessor.refundPayment(paypalResult.get("transaction_id"), 50.0);
        System.out.println("Refund Result: " + refundResult);

        // Checking transaction status
        String status = paypalProcessor.getTransactionStatus(paypalResult.get("transaction_id"));
        System.out.println("PayPal Transaction Status: " + status);
    }
} 