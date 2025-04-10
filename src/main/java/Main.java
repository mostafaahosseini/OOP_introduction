import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1) Select which ConfigurationManager to use:
        // Option A: Load from environment variables
        ConfigurationManager configManager = new EnvConfigurationManager();

        // Option B (if you have a .properties file):
        // ConfigurationManager configManager = new FileConfigurationManager("gateway.properties");

        // 2) Build GatewayFactory
        GatewayFactory factory = new GatewayFactory(configManager);

        // 3) Choose which gateway you want at runtime (e.g. "stripe" or "paypal")
        PaymentGateway gateway = factory.createGateway("stripe");

        // 4) Inject gateway into PaymentProcessor
        PaymentProcessor processor = new PaymentProcessor(gateway);

        // 5) Build a Payment object (from Stage 2)
        Map<String, String> customerInfo = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> cardDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");
        Payment payment = new CreditCardPayment(100, "USD", customerInfo, cardDetails);

        // 6) Process Payment
        Map<String, String> result = processor.processPayment(payment);
        System.out.println("Payment Result: " + result);

        // 7) Optionally refund or check status
        String transactionId = result.get("transaction_id");
        Map<String, String> refundResult = processor.refundPayment(transactionId, 50.0);
        System.out.println("Refund Result: " + refundResult);

        System.out.println("Transaction Status: " + processor.getTransactionStatus(transactionId));
    }
}
