package apd.proj.fixed;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Inventory {
    // Singleton instance
    private static Inventory instance;

    private Map<String, AtomicInteger> stock;
    private Map<String, Lock> locks;
    private Random random = new Random();

    private int sleepTime;

    private Inventory() {
        stock = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();

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
        stock.computeIfAbsent(ingredient, k -> new AtomicInteger(0)).addAndGet(quantity);
        locks.putIfAbsent(ingredient, new ReentrantLock());
    }

    public boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, new AtomicInteger(0)).get() > 0;
    }

    public boolean useIngredientsForDish(Dish dish) {
        boolean success = true;
    
        // Get ingredients and shuffle them to randomize the order of locking
        var ingredients = new ArrayList<>(dish.getIngredients().keySet());
        Collections.shuffle(ingredients);  // Randomize the order to make more obvious
    
        ingredients.sort(String::compareTo);  // Sort the ingredients to ensure the locking order is always the same
    
        // List to track successfully used ingredients for later reversion
        ArrayList<String> usedIngredients = new ArrayList<>();
    
        // Lock and use each ingredient
        for (String ingredient : ingredients) {
            Lock lock = locks.get(ingredient);
            if (lock != null) {
                try {
                    // Acquire the lock on the ingredient
                    System.out.println(Thread.currentThread().getName() + " locking " + ingredient);
                    lock.lock();
                    Thread.sleep(sleepTime);  
                    
                    // Try to use the ingredient
                    if (!useIngredient(ingredient)) {
                        success = false;
                        break;
                    } else {
                        // If successful, add the ingredient to the used list
                        usedIngredients.add(ingredient);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    success = false;
                    break;
                } finally {
                    // Unlock the ingredient in the finally block
                    System.out.println(Thread.currentThread().getName() + " unlocking " + ingredient);
                    lock.unlock();
                }
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
        AtomicInteger count = stock.get(ingredient);
        if (count != null) {
            count.incrementAndGet();  // Add back the ingredient
        }
    }    

    public boolean useIngredient(String ingredient) {
        AtomicInteger count = stock.get(ingredient);
        if (count != null && count.get() > 0) {
            try {
                Thread.sleep(sleepTime); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count.decrementAndGet();
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + stock);
    }
}
