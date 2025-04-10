import java.util.Map;

/**
 * A concrete implementation of PaymentGateway for PayPal.
 */
public class PayPalGateway extends BaseGateway {

    public PayPalGateway(Map<String, String> config) {
        super(config);
    }

    @Override
    public Map<String, String> processPayment(Payment payment) {
        String endpoint = config.getOrDefault("endpoint", "https://api.paypal.com");
        System.out.println("PayPalGateway: Processing payment at endpoint: " + endpoint);

        String transactionId = generateTransactionId("PP_");
        System.out.println("PayPalGateway: Transaction ID = " + transactionId);

        return Map.of("status", "success", "transaction_id", transactionId);
    }

    @Override
    public Map<String, String> refundPayment(String transactionId, double amount) {
        System.out.println("PayPalGateway: Refunding " + amount + " for transaction " + transactionId);
        return Map.of("status", "refunded", "refund_id", "REF_" + transactionId);
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        System.out.println("PayPalGateway: Checking status for transaction " + transactionId);
        return "completed";
    }
}
