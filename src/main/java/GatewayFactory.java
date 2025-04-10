import java.util.Map;

/**
 * A factory class to create different PaymentGateway implementations
 * based on a serviceName (e.g. "stripe", "paypal").
 */
public class GatewayFactory {

    private final ConfigurationManager configManager;

    public GatewayFactory(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Creates a PaymentGateway instance, populated with relevant config
     * from the ConfigurationManager.
     *
     * @param serviceName A string identifier for the gateway (e.g. "stripe", "paypal").
     * @return A concrete PaymentGateway instance (e.g. StripeGateway or PayPalGateway).
     */
    public PaymentGateway createGateway(String serviceName) {
        // Load the config data for the given service
        Map<String, String> serviceConfig = configManager.getConfig(serviceName);

        // Basic example switch - you can expand or refine as needed
        switch (serviceName.toLowerCase()) {
            case "stripe":
                return new StripeGateway(serviceConfig);
            case "paypal":
                return new PayPalGateway(serviceConfig);
            default:
                throw new IllegalArgumentException("Unsupported gateway: " + serviceName);
        }
    }
}
