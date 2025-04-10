package Payment;

import java.util.Date;
import java.util.Map;

public abstract class Payment {
    protected double amount;
    protected String currency;
    protected Map<String, String> customerInfo;
    protected Map<String, String> paymentDetails;
    protected Date timestamp;

    public Payment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        this.amount = amount;
        this.currency = currency;
        this.customerInfo = customerInfo;
        this.paymentDetails = paymentDetails;
        this.timestamp = new Date();
    }

    public abstract boolean validatePayment();
}

