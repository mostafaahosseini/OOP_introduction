import java.util.Map;

/**
 * A concrete implementation of PaymentGateway for Stripe.
 */
public class StripeGateway extends BaseGateway {

    public StripeGateway(Map<String, String> config) {
        super(config);
    }

    @Override
    public Map<String, String> processPayment(Payment payment) {
        String endpoint = config.getOrDefault("endpoint", "https://api.stripe.com");
        System.out.println("StripeGateway: Processing payment at endpoint: " + endpoint);

        String transactionId = generateTransactionId("STR_");
        System.out.println("StripeGateway: Transaction ID = " + transactionId);

        return Map.of("status", "success", "transaction_id", transactionId);
    }

    @Override
    public Map<String, String> refundPayment(String transactionId, double amount) {
        System.out.println("StripeGateway: Refunding " + amount + " for transaction " + transactionId);
        return Map.of("status", "refunded", "refund_id", "REF_" + transactionId);
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        System.out.println("StripeGateway: Checking status for transaction " + transactionId);
        return "completed";
    }
}
