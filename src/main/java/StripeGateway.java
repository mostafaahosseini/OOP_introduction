import java.util.Map;

/**
 * A concrete implementation of PaymentGateway for Stripe.
 * In a real system, this would handle HTTP requests to Stripe's endpoints, etc.
 */
public class StripeGateway extends BaseGateway {

    public StripeGateway(Map<String, String> config) {
        super(config);
    }

    @Override
    public Map<String, String> processPayment(Payment payment) {
        // For example, read an "endpoint" or "apiKey" from config
        String endpoint = config.getOrDefault("stripeEndpoint", "https://api.stripe.com");
        System.out.println("StripeGateway: Processing payment for " + payment.customerInfo.get("name"));
        System.out.println("StripeGateway: Using endpoint: " + endpoint);

        // Generate a unique transaction ID
        String transactionId = generateTransactionId("STR_");

        // Return a mock success result
        return Map.of(
                "status", "success",
                "transaction_id", transactionId
        );
    }

    @Override
    public Map<String, String> refundPayment(String transactionId, double amount) {
        System.out.println("StripeGateway: Refunding " + amount + " for transaction " + transactionId);
        // Return a mock refund response
        return Map.of("status", "refunded", "refund_id", "REF_" + transactionId);
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        System.out.println("StripeGateway: Checking status for transaction " + transactionId);
        // Return a mock status
        return "completed";
    }
} 