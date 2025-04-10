import java.util.Map;

/**
 * Provides shared functionality for gateway implementations, such as config handling.
 * This class is optional. You can skip it if you don't need shared logic.
 */
public abstract class BaseGateway implements PaymentGateway {
    protected Map<String, String> config;

    public BaseGateway(Map<String, String> config) {
        this.config = config;
    }

    /**
     * Utility to generate a unique transaction ID using a given prefix.
     */
    protected String generateTransactionId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
} 