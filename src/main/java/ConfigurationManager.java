import java.util.Map;

/**
 * Responsible for loading configuration details (e.g., API keys, endpoints)
 * from external sources such as environment variables or property files.
 */
public interface ConfigurationManager {

    /**
     * Retrieves configuration properties for a given service/gateway name.
     *
     * @param serviceName The identifier for the gateway (e.g., "stripe", "paypal").
     * @return A map of configuration keys to values (e.g., endpoint URLs, API keys).
     */
    Map<String, String> getConfig(String serviceName);
}
