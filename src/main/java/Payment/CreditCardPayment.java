package Payment;

import java.util.Map;

public class CreditCardPayment extends Payment {
    public CreditCardPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.getOrDefault("card_number", "").length() >= 12;
    }
}
