// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;

// public class RestaurantMain {
//     public static void main(String[] args) {
//         Kitchen kitchen = new Kitchen();
//         Inventory inventory = new Inventory();

//         // Add some ingredients to the inventory - init inventory
//         for (int i = 0; i < 200; i++){
//             inventory.addIngredient("egg", 1);
//             inventory.addIngredient("milk", 1);
//             inventory.addIngredient("butter", 1);
//             inventory.addIngredient("water", 1);
//             inventory.addIngredient("salt", 1);
//         }

//         // Add dishes to the kitchen - init kitchen
//         for (int i = 0; i < 100; i++){
//             kitchen.addDish(new SteamedEgg());
//             kitchen.addDish(new Omelette());
//         }

//         // Display the initial inventory
//         inventory.displayInventory();
       
//         // Create a fixed thread pool using the Executor Framework
//         int numberOfChefs = 10;  // For example, we want 2 chef threads
//         ExecutorService executorService = Executors.newFixedThreadPool(numberOfChefs);

//         // Submit Chef tasks to the executor
//         for (int i = 0; i < numberOfChefs; i++) {
//             Chef chef = new Chef(kitchen, inventory);
//             executorService.submit(chef);
//         }

//         // Properly shut down the ExecutorService
//         executorService.shutdown();

//         try {
//             // Wait indefinitely until all tasks are finished
//             executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt(); // Restore interrupted status
//         }

//         // Display the final inventory after all chef threads have finished
//         inventory.displayInventory();
//     }
// }
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

        // Display initial inventory
        inventory.displayInventory();

        // Create a fixed thread pool for waiters using the Executor Framework
        int numberOfWaiters = 10;
        ExecutorService waiterExecutor = Executors.newFixedThreadPool(numberOfWaiters);

        // Submit Waiter tasks to the executor
        for (int i = 0; i < numberOfWaiters; i++) {
            waiterExecutor.submit(new Waiter(kitchen));
        }

        // Properly shut down the ExecutorService
        waiterExecutor.shutdown();
        //  Create a fixed thread pool using the Executor Framework
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
            // Wait indefinitely until all tasks are finished
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            waiterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }

        // try {
        //     // Wait indefinitely until all tasks are finished
        //     waiterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt(); // Restore interrupted status
        // }

        // Display the final inventory
        inventory.displayInventory();
    }
}

