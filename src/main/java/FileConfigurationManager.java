import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads configuration from a .properties file on the classpath, for example:
 *   stripe.endpoint=https://api.stripe.com
 *   stripe.apiKey=sk_test_123
 *   paypal.endpoint=https://api.paypal.com
 *   paypal.clientId=paypal_client_456
 */
public class FileConfigurationManager implements ConfigurationManager {

    private final Properties properties;

    public FileConfigurationManager(String propertiesFilename) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFilename)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Properties file not found: " + propertiesFilename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getConfig(String serviceName) {
        Map<String, String> configMap = new HashMap<>();
        // Usually we do something like "stripe.endpoint", "stripe.apiKey"
        String prefix = serviceName.toLowerCase() + ".";

        String endpoint = properties.getProperty(prefix + "endpoint");
        if (endpoint != null) {
            configMap.put("endpoint", endpoint);
        }

        String apiKey = properties.getProperty(prefix + "apiKey");
        if (apiKey != null) {
            configMap.put("apiKey", apiKey);
        }

        String clientId = properties.getProperty(prefix + "clientId");
        if (clientId != null) {
            configMap.put("clientId", clientId);
        }

        return configMap;
    }
}
