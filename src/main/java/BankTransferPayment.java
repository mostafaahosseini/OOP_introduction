import java.util.Map;

/**
 * Handles validation specific to bank transfer transactions.
 */
public class BankTransferPayment extends Payment {

    /**
     * Constructs a BankTransferPayment object.
     */
    public BankTransferPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates bank transfer details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Must contain an 'account_number' field
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("account_number");
    }
} 