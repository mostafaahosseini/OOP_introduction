import java.util.Map;

/**
 * Handles validation specific to digital wallet transactions.
 */
public class DigitalWalletPayment extends Payment {

    /**
     * Constructs a DigitalWalletPayment object.
     */
    public DigitalWalletPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates digital wallet details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Must contain a 'wallet_id' field
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("wallet_id");
    }
} 