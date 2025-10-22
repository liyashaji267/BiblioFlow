import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PayPalClient {
    private static final String CLIENT_ID;
    private static final String CLIENT_SECRET;
    
    static {
        // Load environment variables from .env file
        Map<String, String> env = loadEnvFile();
        CLIENT_ID = env.getOrDefault("CLIENT_ID", System.getenv("PAYPAL_CLIENT_ID"));
        CLIENT_SECRET = env.getOrDefault("CLIENT_SECRET", System.getenv("PAYPAL_CLIENT_SECRET"));
        
        // Validate credentials
        if (CLIENT_ID == null || CLIENT_ID.isEmpty() || CLIENT_SECRET == null || CLIENT_SECRET.isEmpty()) {
            System.err.println("Warning: PayPal credentials not found. Please check your .env file or environment variables.");
        }
    }

    private static final PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
            CLIENT_ID,
            CLIENT_SECRET
    );

    public static PayPalHttpClient client = new PayPalHttpClient(environment);
    
    private static Map<String, String> loadEnvFile() {
        Map<String, String> envMap = new HashMap<>();
        try {
            String filePath = System.getProperty("user.dir") + "/.env";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        envMap.put(key, value);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Note: .env file not found or couldn't be read. Using environment variables.");
        }
        return envMap;
    }
    
    public static boolean isConfigured() {
        return CLIENT_ID != null && !CLIENT_ID.isEmpty() && 
               CLIENT_SECRET != null && !CLIENT_SECRET.isEmpty();
    }
}