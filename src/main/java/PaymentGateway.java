import java.util.Map;

/**
 * Defines the contract for external payment gateway integrations.
 * Implementing classes should handle communication with a third-party service.
 */
public interface PaymentGateway {

    /**
     * Processes a Payment object through this gateway.
     *
     * @param payment A concrete Payment object containing amount, currency, etc.
     * @return A map containing the status of the operation, transaction ID, or error details.
     */
    Map<String, String> processPayment(Payment payment);

    /**
     * Initiates a refund for a given transaction.
     *
     * @param transactionId The ID of the transaction to refund.
     * @param amount        The amount to refund.
     * @return A map containing refund status and new transaction info, if applicable.
     */
    Map<String, String> refundPayment(String transactionId, double amount);

    /**
     * Retrieves the current status of a transaction in the gateway.
     *
     * @param transactionId The ID of the transaction to check.
     * @return A string describing the transaction status (e.g., "completed", "pending", etc.).
     */
    String getTransactionStatus(String transactionId);
}
