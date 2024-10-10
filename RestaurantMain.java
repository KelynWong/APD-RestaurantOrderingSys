import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add some ingredients to the inventory
        inventory.addIngredient("egg", 100);
        inventory.addIngredient("milk", 100);
        inventory.addIngredient("butter", 100);
        inventory.addIngredient("water", 100);
        inventory.addIngredient("salt", 100);

        // Create a fixed thread pool for waiters
        int numberOfWaiters = 2;
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
        for (int i = 0; numberOfChefs > i; i++) {
            chefExecutor.submit(new Chef(kitchen, inventory));
        }

        // Properly shut down the executors after tasks are submitted
        waiterExecutor.shutdown();
        chefExecutor.shutdown();

        try {
            // Wait for all waiter tasks to complete
            waiterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            // Wait for all chef tasks to complete
            chefExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            System.out.println("All tasks have completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset the interrupted status
        }

        // Display final kitchen state (optional)
        if (chefExecutor.isTerminated()) {
            System.out.println("Chefs have completed their tasks. Inventory is final.");
            // Display the final inventory
            inventory.displayInventory();
            // Display final kitchen state (optional)
            kitchen.printDishes();
        } else {
            System.out.println("Not all chefs have completed their tasks. Inventory might not be final.");
        }
    }
}
