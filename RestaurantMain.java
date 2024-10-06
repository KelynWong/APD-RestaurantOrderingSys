import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add some ingredients to the inventory
        inventory.addIngredient("egg", 5000);
        inventory.addIngredient("milk", 3000);
        inventory.addIngredient("butter", 2000);
        inventory.addIngredient("water", 2000);
        inventory.addIngredient("salt", 2000);

        // Create a fixed thread pool for waiters
        int numberOfWaiters = 5;
        ExecutorService waiterExecutor = Executors.newFixedThreadPool(numberOfWaiters);

        // Create a fixed thread pool for chefs
        int numberOfChefs = 5;
        ExecutorService chefExecutor = Executors.newFixedThreadPool(numberOfChefs);

        // Submit Waiter tasks to the executor
        for (int i = 0; i < numberOfWaiters; i++) {
            Order order = new Order();  // Assuming Order object is created
            waiterExecutor.submit(new Waiter(kitchen, order));
        }

        // Submit Chef tasks to the executor
        for (int i = 0; i < numberOfChefs; i++) {
            chefExecutor.submit(new Chef(kitchen, inventory));
        }

        // Properly shut down the executors
        waiterExecutor.shutdown();
        chefExecutor.shutdown();

        try {
            waiterExecutor.awaitTermination(10, TimeUnit.SECONDS);
            chefExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Display final kitchen state (optional)
        kitchen.displayToMakeDishes();
        kitchen.displayMadeDishes();
    }
}
