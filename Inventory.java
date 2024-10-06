import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> stock;

    public Inventory() {
        stock = new HashMap<>();
    }

    // Add a new ingredient to the inventory
    public void addIngredient(String ingredient, int quantity) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
    }

    // Check if the inventory has a certain ingredient
    public boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, 0) > 0;
    }

    // Use an ingredient from the inventory (if available)
    // potential race condition here
    public boolean useIngredient(String ingredient) {
        if (hasIngredient(ingredient)) {
            
            int count = stock.get(ingredient);
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count--;
            stock.put(ingredient, count);  // Decrease the stock by 1
            return true;
        } else {
            return false;  // Ingredient not available
        }
    }

    // Display current inventory
    public void displayInventory() {
        System.out.println("Current Inventory: " + stock);
    }
}
