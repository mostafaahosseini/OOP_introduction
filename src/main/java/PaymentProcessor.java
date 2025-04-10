import java.util.Map;

/**
 * Coordinates payment validation and delegates to a specified PaymentGateway
 * for the actual external processing (e.g., contacting Stripe or PayPal).
 */
public class PaymentProcessor {

    private final PaymentGateway gateway;

    /**
     * Constructs the PaymentProcessor with a specified gateway (e.g., Stripe, PayPal).
     */
    public PaymentProcessor(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Processes a Payment object by:
     * 1) Validating the Payment
     * 2) Invoking the gateway's processPayment method
     */
    public Map<String, String> processPayment(Payment payment) {
        // Validate the domain logic first
        if (!payment.validatePayment()) {
            return Map.of("status", "failed", "message", "Validation error");
        }

        // Delegate to the gateway for external processing
        return gateway.processPayment(payment);
    }

    /**
     * Initiates a refund for a given transaction ID and amount
     * using the same PaymentGateway.
     */
    public Map<String, String> refundPayment(String transactionId, double amount) {
        return gateway.refundPayment(transactionId, amount);
    }

    /**
     * Retrieves the status of a transaction from the PaymentGateway.
     */
    public String getTransactionStatus(String transactionId) {
        return gateway.getTransactionStatus(transactionId);
    }
}
