import java.util.Map;

/**
 * Handles validation specific to credit card transactions.
 */
public class CreditCardPayment extends Payment {

    /**
     * Constructs a CreditCardPayment object.
     */
    public CreditCardPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates credit card details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Card number length must be at least 12 characters
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.getOrDefault("card_number", "").length() >= 12;
    }
} 