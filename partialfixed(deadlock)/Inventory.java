import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class Inventory {
    private Map<String, Integer> stock;
    private Map<String, Lock> locks;
    private Random random = new Random();

    public Inventory() {
        stock = new HashMap<>();
        locks = new HashMap<>();
    }

    public void addIngredient(String ingredient, int quantity) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
        locks.putIfAbsent(ingredient, new ReentrantLock());  // Create a lock for each ingredient
    }

    public boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, 0) > 0;
    }

    // Simulate deadlock by locking ingredients in different orders between chefs
    public boolean useIngredientsForDish(Dish dish) {
        boolean success = true;

        // Get ingredients and shuffle them to randomize the order of locking
        var ingredients = new ArrayList<>(dish.getIngredients().keySet());
        Collections.shuffle(ingredients);  // Randomize the order to make more obvious
        
        // Lock each ingredient before using
        for (String ingredient : ingredients) {
            Lock lock = locks.get(ingredient);
            if (lock != null) {
                System.out.println(Thread.currentThread().getName() + " locking " + ingredient);
                try {
                    Thread.sleep(random.nextInt(100));  // Simulate random delays
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                lock.lock();  // Acquire the lock on the ingredient
            }
        }

        // Try to use the ingredients after acquiring locks
        try {
            for (String ingredient : ingredients) {
                if (!useIngredient(ingredient)) {
                    success = false;
                    break;
                }
            }
        } finally {
            // Unlock all ingredients in reverse order
            for (int i = ingredients.size() - 1; i >= 0; i--) {
                String ingredient = ingredients.get(i);
                Lock lock = locks.get(ingredient);
                if (lock != null) {
                    System.out.println(Thread.currentThread().getName() + " unlocking " + ingredient);
                    try {
                        Thread.sleep(random.nextInt(100));  // Simulate random delays
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    lock.unlock();  // Release the lock on the ingredient
                }
            }
        }

        return success;
    }

    // Using ingredient while holding lock (still potential for deadlock with multiple ingredients)
    public boolean useIngredient(String ingredient) {
        if (hasIngredient(ingredient)) {
            int count = stock.get(ingredient);
            try {
                Thread.sleep(random.nextInt(100));  // Simulate random delays
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
