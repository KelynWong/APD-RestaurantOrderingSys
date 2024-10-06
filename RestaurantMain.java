import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add some ingredients to the inventory - init inventory
        for (int i = 0; i < 2000; i++){
            inventory.addIngredient("egg", 1);
            inventory.addIngredient("milk", 1);
            inventory.addIngredient("butter", 1);
            inventory.addIngredient("water", 1);
            inventory.addIngredient("salt", 1);
        }

        

        // Add dishes to the kitchen - init kitchen
        for (int i = 0; i < 1000; i++){
            kitchen.addDish(new SteamedEgg());
            kitchen.addDish(new Omelette());
        }
        

        // Display the initial inventory
        inventory.displayInventory();
        // Create a fixed thread pool using the Executor Framework
        int numberOfChefs = 10;  // For example, we want 2 chef threads
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfChefs);

        // Submit Chef tasks to the executor
        for (int i = 0; i < numberOfChefs; i++) {
            Chef chef = new Chef(kitchen, inventory);
            executorService.submit(chef);
        }

        // Properly shut down the ExecutorService
        executorService.shutdown();
         try {
            // Wait for all tasks to finish or time out after 1 minute
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow(); // Force shutdown if not finished
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // while (!executorService.isTerminated()) 

        // Create and start the Chef (in a separate thread)
        // Chef chef = new Chef(kitchen, inventory);
        // Thread chefThread = new Thread(chef);
        // chefThread.start();

        // Wait for the chef to finish

        // Display the final inventory
        inventory.displayInventory();
    }
}
