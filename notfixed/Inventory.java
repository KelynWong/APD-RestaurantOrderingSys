import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Inventory {
    private Map<String, Integer> stock;
    private Random random = new Random();

    public Inventory() {
        stock = new HashMap<>();
    }

    public void addIngredient(String ingredient, int quantity) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
    }

    public boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, 0) > 0;
    }

    // Potential race condition here when multiple chefs try to use the same ingredient concurrently
    public boolean useIngredient(String ingredient) {
        if (hasIngredient(ingredient)) {
            int count = stock.get(ingredient);
            try {
                Thread.sleep(random.nextInt(50));  // Simulate random delays
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count--;  // Decrement the stock count (potential race condition)
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