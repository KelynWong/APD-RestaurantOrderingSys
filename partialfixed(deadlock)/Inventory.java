package apd.proj.partialfixed;

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
        Collections.shuffle(ingredients);  // Randomize the order to increase the chance of deadlock
    
        // List to track successfully locked ingredients
        ArrayList<String> lockedIngredients = new ArrayList<>();
    
        // Acquire all locks before using any ingredient
        try {
            for (String ingredient : ingredients) {
                Lock lock = locks.get(ingredient);
                if (lock != null) {
                    System.out.println(Thread.currentThread().getName() + " locking " + ingredient);
                    lock.lock();  // Acquire the lock
                    lockedIngredients.add(ingredient);  // Track the locked ingredient
                }
            }
    
            // Try to use the ingredients after acquiring all locks
            for (String ingredient : ingredients) {
                if (!useIngredient(ingredient)) {
                    success = false;
                    break;
                }
            }
        } finally {
            // Release all locks at the end
            for (int i = lockedIngredients.size() - 1; i >= 0; i--) {
                String ingredient = lockedIngredients.get(i);
                Lock lock = locks.get(ingredient);
                if (lock != null) {
                    System.out.println(Thread.currentThread().getName() + " unlocking " + ingredient);
                    lock.unlock();  // Release the lock
                }
            }
        }
    
        // If any ingredient usage failed, revert the changes
        if (!success) {
            for (String ingredient : ingredients) {
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
