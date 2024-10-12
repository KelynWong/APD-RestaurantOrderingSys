package apd.proj.notfixed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Inventory {
    // Singleton instance
    private static Inventory instance;

    private Map<String, Integer> stock;
    private Random random = new Random();

    private int sleepTime;

    private Inventory() {
        stock = new HashMap<>();

        // Load configuration values
        Config config = Config.getInstance();
        this.sleepTime = config.getIntValue("SLEEP_TIME");
    }

    // Method to get the single instance of Inventory
    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void addIngredient(String ingredient, int quantity) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
    }

    public boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, 0) > 0;
    }

    public boolean useIngredientsForDish(Dish dish) {
        boolean success = true;

        // Get ingredients needed for the dish
        var ingredients = new ArrayList<>(dish.getIngredients().keySet());

        // List to track successfully used ingredients for later reversion
        ArrayList<String> usedIngredients = new ArrayList<>();

        // Try to use the ingredients
        for (String ingredient : ingredients) {
            if (useIngredient(ingredient)) {
                usedIngredients.add(ingredient);  // Keep track of successfully used ingredients
            } else {
                success = false;
                break;
            }
        }

        // If any ingredient usage failed, revert the changes
        if (!success) {
            // Revert the used ingredients (put them back into inventory)
            for (String ingredient : usedIngredients) {
                returnIngredient(ingredient);
            }
        }

        return success;
    }

    public void returnIngredient(String ingredient) {
        if (ingredient != null && stock.containsKey(ingredient)) {
            stock.put(ingredient, stock.get(ingredient) + 1);  // Add back the ingredient
        }
    }

    public boolean useIngredient(String ingredient) {
        if (hasIngredient(ingredient)) {
            int count = stock.get(ingredient);
            try {
                Thread.sleep(sleepTime); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count--;  // Decrement the stock count
            stock.put(ingredient, count);
            return true;
        } else {
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + stock);
    }
}
