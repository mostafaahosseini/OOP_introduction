import java.util.HashMap;
import java.util.Map;

/**
 * Loads configuration from environment variables using a naming convention.
 * For example, if serviceName is "stripe", we might look for:
 * STRIPE_ENDPOINT, STRIPE_API_KEY, etc.
 */
public class EnvConfigurationManager implements ConfigurationManager {

    @Override
    public Map<String, String> getConfig(String serviceName) {
        Map<String, String> configMap = new HashMap<>();
        // Convert the service name to uppercase to match typical env variable style
        String upperName = serviceName.toUpperCase();  // e.g. "stripe" -> "STRIPE"

        // Example environment variables:
        // STRIPE_ENDPOINT, STRIPE_API_KEY
        // or PAYPAL_ENDPOINT, PAYPAL_CLIENT_ID, etc.
        String endpoint = System.getenv(upperName + "_ENDPOINT");
        if (endpoint != null) {
            configMap.put("endpoint", endpoint);
        }

        String apiKey = System.getenv(upperName + "_API_KEY");
        if (apiKey != null) {
            configMap.put("apiKey", apiKey);
        }

        // Optionally handle more fields like client ID, secret, etc.
        String clientId = System.getenv(upperName + "_CLIENT_ID");
        if (clientId != null) {
            configMap.put("clientId", clientId);
        }

        return configMap;
    }
}
