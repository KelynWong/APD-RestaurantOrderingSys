package apd.proj.partialfixed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {
    private static Config instance = null;
    private Map<String, String> configValues;
    private Map<String, Integer> dishOrders;  // Stores the dish orders

    private Config() {
        configValues = new HashMap<>();
        dishOrders = new HashMap<>();
        loadConfig();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void loadConfig() {
        // List of known configuration keys
        Set<String> configKeys = new HashSet<>(Arrays.asList(
            "MAX_SERVING_ATTEMPTS", "SERVING_WAIT_TIME", "MAX_ATTEMPTS", 
            "MAX_EMPTY_CHECKS", "SLEEP_TIME", "NUMBER_OF_CHEFS", "NUMBER_OF_WAITERS", "MAKE_INVENTORY_LIMITED"
        ));

        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;  // Skip comments and empty lines
                }

                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];

                    // Log the key-value pairs being parsed for debugging
                    System.out.println("Parsed config: " + key + " = " + value);

                    // Check if the key is in the list of configuration keys
                    if (configKeys.contains(key)) {
                        configValues.put(key, value);
                    } else {
                        // Assume it's a dish order if it's not in the configKeys set
                        dishOrders.put(key, Integer.parseInt(value));
                    }
                }
            }
            // Debugging: Print the parsed config values and dish orders
            System.out.println("Config Values: " + configValues);
            System.out.println("Dish Orders: " + dishOrders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    public String getValue(String key) {
        return configValues.get(key);
    }

    public int getIntValue(String key) {
        // First check the configValues map for configuration parameters
        String value = configValues.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);  // Convert the value to an integer
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format for key: " + key);
            }
        }
    
        // If the key is not in configValues, check the dishOrders map
        Integer dishOrderValue = dishOrders.get(key);
        if (dishOrderValue != null) {
            return dishOrderValue;
        }
    
        return 0;  // Return 0 if the value is not found in either map
    }        

    // Returns the dish orders
    public Map<String, Integer> getDishOrders() {
        return dishOrders;
    }
}
