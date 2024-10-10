import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Inventory {
    // Singleton instance
    private static Inventory instance;

    private Map<String, Integer> stock;
    private Random random = new Random();

    private Inventory() {
        stock = new HashMap<>();
    }

    // Method to get the single instance of Inventory
    public static synchronized Inventory getInstance() {
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
        // Collections.shuffle(ingredients);  // Randomize the order

        // Try to use the ingredients
        for (String ingredient : ingredients) {
            if (!useIngredient(ingredient)) {
                success = false;
                break;
            }
        }

        return success;
    }

    public boolean useIngredient(String ingredient) {
        if (hasIngredient(ingredient)) {
            int count = stock.get(ingredient);
            try {
                Thread.sleep(random.nextInt(10));  // Simulate random delays
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
