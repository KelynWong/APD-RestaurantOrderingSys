import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class Inventory {
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

    public synchronized void addIngredient(String ingredient, int quantity) {
        stock.computeIfAbsent(ingredient, k -> new AtomicInteger(0)).addAndGet(quantity);
        locks.putIfAbsent(ingredient, new ReentrantLock());
    }

    public synchronized boolean hasIngredient(String ingredient) {
        return stock.getOrDefault(ingredient, new AtomicInteger(0)).get() > 0;
    }

    public synchronized boolean useIngredientsForDish(Dish dish) {
        boolean success = true;

        // Get ingredients and shuffle them to randomize the order of locking
        var ingredients = new ArrayList<>(dish.getIngredients().keySet());
        Collections.shuffle(ingredients);  // Randomize the order to make more obvious

        ingredients.sort(String::compareTo);  // Sort the ingredients to ensure the locking order is always the same
        
        // Lock each ingredient before using
        for (String ingredient : ingredients) {
            Lock lock = locks.get(ingredient);
            if (lock != null) {
                System.out.println(Thread.currentThread().getName() + " locking " + ingredient);
                try {
                    Thread.sleep(sleepTime);  
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
                        Thread.sleep(sleepTime);  // Simulate random delays
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    lock.unlock();  // Release the lock on the ingredient
                }
            }
        }

        return success;
    }

    public synchronized boolean useIngredient(String ingredient) {
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
