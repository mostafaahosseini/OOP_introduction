import java.util.Date;
import java.util.Map;

/**
 * Represents a general payment with basic attributes (amount, currency, customer info, etc.).
 * Subclasses must provide their own implementation of validatePayment().
 */
public abstract class Payment {
    protected double amount;
    protected String currency;
    protected Map<String, String> customerInfo;
    protected Map<String, String> paymentDetails;
    protected Date timestamp;

    /**
     * Constructs a Payment object with common fields.
     *
     * @param amount         The payment amount.
     * @param currency       The currency code (e.g., USD, EUR, GBP).
     * @param customerInfo   A map containing customer information (e.g., name, email).
     * @param paymentDetails A map containing payment-specific data (e.g., card number).
     */
    public Payment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        this.amount = amount;
        this.currency = currency;
        this.customerInfo = customerInfo;
        this.paymentDetails = paymentDetails;
        this.timestamp = new Date();
    }

    /**
     * Validates the payment details based on business rules.
     * Concrete subclasses must override this to provide type-specific validation.
     *
     * @return true if the payment is valid; false otherwise.
     */
    public abstract boolean validatePayment();
}
